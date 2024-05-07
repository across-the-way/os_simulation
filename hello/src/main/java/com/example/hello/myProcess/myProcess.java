package com.example.hello.myProcess;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.hello.controller.myKernel;
import com.example.hello.myInstrunction.Instruction;
import com.example.hello.myInstrunction.InstructionType;
import com.example.hello.myInterrupt.InterruptType;
import com.example.hello.myInterrupt.SystemCallType;
import com.example.hello.myInterrupt.myInterrupt;
import com.example.hello.myProcess.PCB.P_STATE;
import com.example.hello.myDevice.Device;

public class myProcess {
    private myKernel kernel;

    private scheduleStrategy strategy;
    private int next_pid;
    private int current_pid;
    private ProcessQueue queue;

    Map<Integer, PCB> ProcessMap;
    List<Integer> LongTermQueue;
    private int LongTermCounter;
    private int ShortTermCounter;
    private int Second_Queue_Threshold;

    Map<String, mySemaphore> SemaphoreMap;

    // ProcessManager模块初始化
    public myProcess(myKernel kernel) {
        this.kernel = kernel;
        next_pid = 0;
        ProcessMap = new HashMap<>();
        queue = new ProcessQueue();
        LongTermQueue = new ArrayList<>();
        SemaphoreMap = new HashMap<>();

        LongTermCounter = 0;
        ShortTermCounter = 0;
        Second_Queue_Threshold = this.kernel.getSysData().Second_Queue_Threshold;

        // 创建初始进程
        PCB init = new PCB();
        init.p_id = next_pid++;
        ProcessMap.put(0, init);

        // -1表示一开始没有进程在执行
        current_pid = -1;
        // 用于调试
        strategy = this.kernel.getSysData().CPUstrategy;
    }

    // 向kernel中央模块发送中断请求
    private void sendInterrupt(InterruptType interrupt, Object... objs) {
        kernel.receiveInterrupt(new myInterrupt(interrupt, objs));
    }

    // 周期性执行
    public void update() {
        // 模拟长期调度,100个系统时间片执行1次长期调度
        this.LongTermCounter = this.LongTermCounter % this.kernel.getSysData().LongTermScale + 1;

        // 到了长期调度时间片或者当前系统负载没有达到设定阈值下限
        if (this.LongTermCounter == this.kernel.getSysData().LongTermScale
                || this.getLoad() < this.kernel.getSysData().LongTerm_FloorThreshold) {
            // 直到长期队列为空或者系统负载到达阈值上限之前增加负载
            while (this.LongTermQueue.size() > 0 && this.getLoad() < this.kernel.getSysData().LongTerm_CeilThreshold) {
                int pid = this.getFromLongTermQueue();
                if (pid != -1) {
                    this.newToReady(pid);
                }
            }
        }

        System.out.println("Loading Process Number:" + this.getLoad());

        // 信号量表的定期更新
        Semaphore_update();

        // Fork的wait队列更新
        ForkWaitUpdate();

        // Swapped Process 的TTL更新
        SwappedProcess_Update();

        // 动态调整多级队列
        MLFQ_DynamicAdjust();

        // 从就绪队列中调度出一个进程，尝试切换为运行态
        this.schedule();
        // 若无或无法，返回
        if (this.current_pid == -1) {
            return;
        }

        // 模拟当前进程运行
        processRunning();

        // 指定短期调度时间片和系统时间片单独处理,即调用update函数模拟处理TimeoutSlice中断
    }

    // 模拟CPU执行
    private void processRunning() {

        PCB p = this.ProcessMap.get(this.current_pid);

        // 若PC值超限，发送中断PageFault
        if (kernel.isPageFault(this.current_pid, p.pc)) {
            this.sendInterrupt(InterruptType.PageFault, this.current_pid, p.pc);
            return;
        }

        // 读取PC更新IR，并更新PC
        int instruction_index = (p.pc - p.memory_start) / this.kernel.getSysData().InstructionLength;
        p.ir = p.bursts.get(instruction_index);

        // 根据IR当前指令，推进当前进程

        switch (p.ir.getType()) {
            // 若指令为计算，更新当前指令剩余时间
            case InstructionType.Calculate:
                int remain_burst = (int) p.ir.getArguments()[0] - 1;
                if (remain_burst <= 0) {
                    remain_burst = 0;
                    p.pc += this.kernel.getSysData().InstructionLength;
                }
                p.ir.ModifyArgument(0, remain_burst);
                System.out.println("Process " + p.p_id + " is running !CPU burst: remain "
                        + remain_burst * this.kernel.getSysData().SystemPulse + "ms");
                break;
            case InstructionType.AccessMemory:
                int virtual_address = (int) p.ir.getArguments()[0];
                this.kernel.getMm().accessMemory(p.p_id, virtual_address);
                p.pc += this.kernel.getSysData().InstructionLength;
                break;
            // 若指令为文件读写或IO读写
            // 转换当前进程状态从running为waiting，移入等待队列
            // 发送中断系统调用FileRead、FileWrite、IORequest
            // 启动进程调度（短期和长期）,切换进程
            case InstructionType.Printer:
                if (!p.OverResource("printer") && kernel.getSysData().AvailableResource("printer")) {
                    this.RunningtoWaiting(0);
                    this.sendInterrupt(InterruptType.SystemCall, SystemCallType.IORequest, 0, p.p_id,
                            p.ir.getArguments()[0]);
                }
                break;
            case InstructionType.Keyboard:
                if (!p.OverResource("keyboard") && kernel.getSysData().AvailableResource("keyboard")) {
                    this.RunningtoWaiting(1);
                    this.sendInterrupt(InterruptType.SystemCall, SystemCallType.IORequest, 1, p.p_id,
                            p.ir.getArguments()[0]);
                }
                break;
            case InstructionType.Device:
                if (!p.OverResource("device..") && kernel.getSysData().AvailableResource("device..")) {
                    this.RunningtoWaiting(6);
                    this.sendInterrupt(InterruptType.SystemCall, SystemCallType.IORequest, 6, p.p_id,
                            p.ir.getArguments()[0]);
                }
                break;
            case InstructionType.ReadFile:
                if (!p.OverResource("file") && kernel.getSysData().AvailableResource("file")) {
                    this.RunningtoWaiting(2);
                    this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileRead, p.p_id,
                            p.ir.getArguments()[0],
                            p.ir.getArguments()[1]);
                }
                break;
            case InstructionType.WriteFile:
                if (!p.OverResource("file") && kernel.getSysData().AvailableResource("file")) {
                    this.RunningtoWaiting(3);
                    this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileWrite, p.p_id,
                            p.ir.getArguments()[0],
                            p.ir.getArguments()[1]);
                }
                break;

            // 若指令为文件或目录操作
            // 发送中断系统调用FileNew、FileDelete、DirNew、DirDelete
            case InstructionType.CreateFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileNew, p.p_id, p.ir.getArguments()[0],
                        p.ir.getArguments()[1]);
                p.pc += this.kernel.getSysData().InstructionLength;
                this.RunningtoReady();
                break;
            case InstructionType.DeleteFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileDelete, p.p_id, p.ir.getArguments()[0]);
                p.pc += this.kernel.getSysData().InstructionLength;
                this.RunningtoReady();
                break;
            case InstructionType.CreateDir:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.DirNew, p.p_id, p.ir.getArguments()[0],
                        p.ir.getArguments()[1]);
                p.pc += this.kernel.getSysData().InstructionLength;
                this.RunningtoReady();
                break;
            case InstructionType.DeleteDir:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.DirDelete, p.p_id, p.ir.getArguments()[0]);
                p.pc += this.kernel.getSysData().InstructionLength;
                this.RunningtoReady();
                break;
            case InstructionType.OpenFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileOpen, p.p_id, p.ir.getArguments()[0]);
                this.RunningtoReady();
                p.pc += this.kernel.getSysData().InstructionLength;
                break;
            case InstructionType.CloseFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileClose, p.p_id, p.ir.getArguments()[0]);
                this.RunningtoReady();
                p.pc += this.kernel.getSysData().InstructionLength;
                break;
            // 若指令为cond系列操作
            // CondNew为进程和子进程们添加信号量
            case InstructionType.CondNew:
                mySemaphore semaphore = new mySemaphore((String) p.ir.getArguments()[0], (int) p.ir.getArguments()[1]);
                this.SemaphoreMap.put(semaphore.name, semaphore);
                p.pc += this.kernel.getSysData().InstructionLength;
                break;
            case InstructionType.CondWait:
                Semaphore_Wait(p.p_id);
                break;
            case InstructionType.CondSignal:
                Semaphore_Signal(p.p_id);
                break;

            // 若指令为fork
            // 发送中断系统调用ProcessNew，或者。。。
            case InstructionType.Fork:
                if (p.ir.getArguments().length > 1) {
                    this.sendInterrupt(InterruptType.SystemCall, SystemCallType.Fork, p.p_id, p.ir.getArguments()[0],
                            p.ir.getArguments()[1]);
                } else {
                    this.sendInterrupt(InterruptType.SystemCall, SystemCallType.Fork, p.p_id, p.ir.getArguments()[0]);
                }
                break;

            // 若指令为wait，且合法（有子进程）
            // 转换当前进程状态从running为waiting，移入等待队列
            // 启动进程调度（短期和长期）,切换进程
            case InstructionType.Wait:
                break;

            case InstructionType.Exit:
                System.out.println("Process" + p.p_id + " is Exiting safely");
                p.pc += this.kernel.getSysData().InstructionLength;
                // 若完成,发送中断系统调用ProcessExit
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.ProcessExit, this.current_pid);
                // 启动进程调度（短期和长期）,切换进程
                this.current_pid = -1;
                break;
            // 若指令非法
            // 发送中断系统调用ProcessExit
            default:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.ProcessExit);
        }

    }

    /*
     * 进程创建/删除
     */

    // 创建进程
    public int createPCB(Object[] instructions) {
        // 新建一个PCB
        PCB p = new PCB(instructions);

        // 分配PID为next_pid
        p.p_id = this.next_pid;

        // 存在当前执行进程,构建父子进程关系
        if (this.current_pid != -1) {
            p.pp_id = this.current_pid;
            PCB currentProcess = this.ProcessMap.get(this.current_pid);
            currentProcess.c_id.add(p.p_id);
        } else {
            p.pp_id = -1;
        }

        // 加入pid-PCB映射表
        this.ProcessMap.put(p.p_id, p);

        return next_pid++;
    }

    public int ForkPCB(int pp_id, int index) {
        // 新建一个PCB
        PCB p = new PCB(this.getPCB(pp_id));

        // 分配PID为next_pid
        p.p_id = this.next_pid;

        // 分配该进程的pc
        p.pc = p.memory_start + index * this.kernel.getSysData().InstructionLength;

        // 存在当前执行进程,构建父子进程关系
        if (pp_id != -1) {
            p.pp_id = pp_id;
            PCB parent = this.ProcessMap.get(pp_id);
            parent.c_id.add(p.p_id);
        } else {
            p.pp_id = -1;
        }

        // 加入pid-PCB映射表
        this.ProcessMap.put(p.p_id, p);

        return next_pid++;
    }

    // 终止进程
    public void deletePCB(int pid) {
        // init进程无法终止
        if (pid == 0)
            return;

        // 将其子进程转交其父进程或0进程
        PCB p = this.ProcessMap.get(pid);
        PCB parent;
        if (this.ProcessMap.containsKey(p.pp_id)) {
            parent = this.ProcessMap.get(p.pp_id);
        } else {
            // 没有父进程,将所有子进程交给0进程处理
            parent = this.ProcessMap.get(0);

        }
        for (int i = 0; i < p.c_id.size(); i++) {
            parent.c_id.add(p.c_id.get(i));
            PCB child = this.ProcessMap.get(p.c_id.get(i));
            child.pp_id = p.pp_id;
        }
        if (parent.c_id.contains(pid)) {
            parent.c_id.remove((Object) pid);
        }

        // 若父进程调用过wait，移入就绪队列(fork 进程间通信)

        // 将PCB从pid-PCB映射表中删除
        this.ProcessMap.remove(pid);

        // 将PCB从各个队列移除
        this.queue.RemoveProcess(pid);

    }

    // 将创建的作业加入长期调度队列
    public void addToLongTermQueue(int pid) {
        // pid入长期调度队列
        this.LongTermQueue.add(pid);

    }

    // 从长期调度队列选择一个作业, 调度策略：FCFS
    public int getFromLongTermQueue() {
        // 作业池里没有进程
        if (LongTermQueue.size() < 1) {
            return -1;
        } else {
            int pid = this.LongTermQueue.removeFirst();
            return pid;
        }
    }

    // 获得当前CPU短期调度负载数
    int getLoad() {
        int load = 0;
        if (this.current_pid != -1)
            load++;
        load += this.queue.Ready_Queue.size();
        for (List<Integer> waiting_queue : this.queue.Waiting_Queues) {
            load += waiting_queue.size();
        }
        if (this.strategy == scheduleStrategy.MLFQ) {
            load += this.queue.Second_Queue.size();
        }
        load += this.queue.Swapped_Ready_Queue.size() + this.queue.Swapped_Waiting_Queue.size();
        return load;
    }

    public PCB getPCB(int pid) {
        if (this.ProcessMap.containsKey(pid))
            return this.ProcessMap.get(pid);
        else
            return null;
    }

    // 模拟CPU调度
    public void schedule() {
        // 当前有进程在执行,保存现场
        /*
         * current_pid = -1的情况:
         * 1.当前CPU负载为0
         * 2.Processrunning 函数执行的指令
         * 2.1 调用 runningtowaiting函数
         * 2.2 调用 runningtoready函数
         * 2.3 产生 ProcessExit 的 systemcall
         * (目前系统只保留 process manager update 函数的 schedule,即不会在上述情况后调用schedule使current pid
         * 发生变化)
         * 
         * current_pid != -1 的情况:
         * 当前存在负载并且当前进程执行的指令并没有触发中断(在下一个时钟周期片可能发生变化)
         */
        if (this.strategy == scheduleStrategy.MLFQ) {
            if (this.current_pid != -1) {
                PCB p = this.ProcessMap.get(this.current_pid);
                int index = (p.pc - p.memory_start) / this.kernel.getSysData().InstructionLength;
                // 如果时间片用完后该进程只剩下Exit命令或者没有命令,则不会调整到二级队列
                // 如果当前指令为Fork指令,时间片耗尽后不应该将该进程移到二级队列,如果有Wait则应该移到Waiting队列,如果没有则移到Ready队列
                if (index < p.bursts.size() - 1 && !p.bursts.get(index).getType().equals(InstructionType.Fork)) {
                    // 将当前进程调整到二级队列
                    this.RunningtoSecondReady();
                } else {
                    return;
                }
            }
        } else if (this.current_pid != -1) {
            this.RunningtoReady();
        }
        switch (this.strategy) {
            case scheduleStrategy.RR:
                FCFS();
                break;
            case scheduleStrategy.FCFS:
                FCFS();
                break;
            case scheduleStrategy.SJF:
                SJF();
                break;
            case scheduleStrategy.Priority:
                Priority_NonPreemptive();
                break;
            case scheduleStrategy.MLFQ:
                MLFQ();
                break;
            default:
                break;
        }
    }

    /*
     * 进程状态转换模块
     */

    // 将指定pid的进程从NEW切换到REEADY状态
    public boolean newToReady(int pid) {
        if (this.ProcessMap.containsKey(pid)) {
            PCB p = this.ProcessMap.get(pid);
            if (p.state != PCB.P_STATE.NEW)
                return false;
            else {
                p.state = PCB.P_STATE.READY;
                this.queue.Ready_Queue.add(p.p_id);
                return true;
            }
        } else
            return false;
    }

    // 将指定pid的进程从READY切换到RUNNING状态
    public boolean readyToRunning(int pid) {
        if (this.ProcessMap.containsKey(pid)) {
            PCB p = this.ProcessMap.get(pid);
            if (p.state != PCB.P_STATE.READY)
                return false;
            else {
                p.state = PCB.P_STATE.RUNNING;
                this.current_pid = p.p_id;
                this.queue.Ready_Queue.remove((Object) p.p_id);
                return true;
            }
        } else
            return false;
    }

    // 将指定pid的进程从WAIT状态转换到READY状态
    public boolean waitToReady(int pid) {
        if (this.ProcessMap.containsKey(pid)) {
            PCB p = this.ProcessMap.get(pid);
            if (p.state != PCB.P_STATE.WAITING)
                return false;
            else {
                p.state = PCB.P_STATE.READY;
                this.queue.Ready_Queue.add(p.p_id);
                this.queue.Waiting_Queues.get(p.waiting_for).remove((Object) pid);
                p.waiting_for = -1;
                return true;
            }
        } else
            return false;
    }

    // 将运行的进程转换到WAIT状态，并加入相应的Waiting队列
    public void RunningtoWaiting(int waiting_for) {
        PCB p = this.ProcessMap.get(this.current_pid);
        if (p != null) {
            p.state = P_STATE.WAITING;
            p.waiting_for = waiting_for;
            this.queue.Waiting_Queues.get(waiting_for).add(p.p_id);
            this.current_pid = -1;
        }
    }

    // 将运行的进程转换为READY状态，并加入相应的Ready队列
    public void RunningtoReady() {
        PCB p = this.ProcessMap.get(this.current_pid);
        if (p != null) {
            p.state = P_STATE.READY;
            this.queue.Ready_Queue.add(p.p_id);
            this.current_pid = -1;
        }
    }

    // 将运行的进程转换为READY状态.并加入二级Ready队列
    public void RunningtoSecondReady() {
        PCB p = this.ProcessMap.get(this.current_pid);
        if (p != null) {
            p.state = P_STATE.READY;
            this.queue.Second_Queue.add(new TTLItem(this.current_pid));
            this.current_pid = -1;
        }
    }

    // 将Ready的进程转换为SwappedREADY状态.并加入SwappedReady队列
    public void ReadyToSwappedReady(int pid) {
        PCB p = this.ProcessMap.get(pid);
        if (p != null) {
            p.state = P_STATE.SWAPPED_READY;
            if (this.isInReadyQueue(pid)) {
                this.queue.Ready_Queue.remove((Object) pid);
            } else {
                this.queue.RemoveProcessFromSecondReady(pid);
            }
            this.queue.Swapped_Ready_Queue.add(new TTLItem(pid));
        }
    }

    // 将Waiting的进程转换为SwappedWaiting状态.并加入SwappedWaitiing队列
    public void WaitingToSwappedWaiting(int pid) {
        PCB p = this.ProcessMap.get(pid);
        if (p != null) {
            p.state = P_STATE.SWAPPED_WAITING;
            this.queue.Waiting_Queues.get(p.waiting_for).remove((Object) pid);
            this.queue.Swapped_Waiting_Queue.add(new TTLItem(pid));
        }
    }

    // 将SwappedWaiting的进程转换为SwappedReady状态.并加入SwappedReady队列
    public void SwappedWaitingToSwappedReady(int pid) {
        PCB p = this.ProcessMap.get(pid);
        if (p != null) {
            p.state = P_STATE.SWAPPED_READY;
            this.queue.RemoveProcessFromSwappedWaiting(pid);
            this.queue.Swapped_Waiting_Queue.add(new TTLItem(pid));
        }
    }

    // 将SwappedReady的进程转换为Ready状态.并加入Ready队列
    public void SwappedReadyToReady(int pid) {
        PCB p = this.ProcessMap.get(pid);
        if (p != null) {
            p.state = P_STATE.READY;
            this.queue.RemoveProcessFromSwappedReady(pid);
            this.queue.Ready_Queue.add(pid);
        }
    }

    // 将SwappedWaiting的进程转换为Waiting状态.并加入Waiting队列
    public void SwappedWaitingToWaiting(int pid) {
        PCB p = this.ProcessMap.get(pid);
        if (p != null) {
            p.state = P_STATE.WAITING;
            this.queue.RemoveProcessFromSwappedWaiting(pid);
            this.queue.Waiting_Queues.get(p.waiting_for).add(pid);
        }
    }

    /*
     * 文件打开表模块
     */

    // 进程的文件打开表操作
    public boolean addOpenFile(int pid, int fd) {
        if (!this.ProcessMap.get(pid).FileTable.contains(fd)) {
            this.ProcessMap.get(pid).FileTable.add(fd);
            return true;
        } else {
            return false;
        }
    }

    public void removeOpenFile(int pid, int fd) {
        this.ProcessMap.get(pid).FileTable.remove((Object) fd);
    }

    /*
     * CPU调度模块
     */

    <T extends Comparable<T>> int findIndexByMinValue(T[] array) {
        int res = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].compareTo(array[res]) < 0)
                res = i;
        }
        return res;
    }

    <T extends Comparable<T>> int findIndexByMaxValue(T[] array) {
        int res = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].compareTo(array[res]) > 0)
                res = i;
        }
        return res;
    }

    // 采用FCFS调度进程
    void FCFS() {
        int size = this.queue.Ready_Queue.size();
        // 如果waiting队列不为空,则将第一个进程选为下一个running进程
        if (size > 0) {
            int pid = this.queue.Ready_Queue.getFirst();
            this.queue.Ready_Queue.removeFirst();
            this.readyToRunning(pid);
        }
    }

    // 采用SJF调度进程(非抢占式)
    void SJF() {
        int size = this.queue.Ready_Queue.size();
        // 如果waiting队列不为空,则以SJF策略选择下一个running进程
        if (size > 0) {
            // 计算ready队列中每个进程的剩余CPU执行时间
            Integer[] remain_CPUburst = new Integer[this.next_pid];
            for (int i = 0; i < this.next_pid; i++) {
                remain_CPUburst[i] = 10000;
            }
            for (PCB p : ProcessMap.values()) {
                if (p.p_id == 0)
                    continue;
                int cpu_burst = 0;
                for (int j = p.pc; j < p.bursts.size(); j++) {
                    if (p.bursts.get(j).getType() == InstructionType.Calculate) {
                        cpu_burst += (int) p.bursts.get(j).getArguments()[0];
                    }
                }
                remain_CPUburst[p.p_id] = cpu_burst;
            }

            // 找到CPUburst最小的PCB,更新queue
            int SJF_index = findIndexByMinValue(remain_CPUburst);
            this.queue.Ready_Queue.remove((Object) SJF_index);
            this.readyToRunning(SJF_index);
        }
    }

    // 采用非抢占式优先级调度
    void Priority_NonPreemptive() {
        int size = this.queue.Ready_Queue.size();
        // 如果waiting队列不为空,则以SJF策略选择下一个running进程
        if (size > 0) {
            // 将所有process的priority复制到一个数组
            Integer[] processpriority = new Integer[this.next_pid];
            for (int i = 0; i < this.next_pid; i++) {
                processpriority[i] = 10000;
            }
            for (PCB p : ProcessMap.values()) {
                if (p.p_id != 0 && this.queue.Ready_Queue.contains(p.p_id))
                    processpriority[p.p_id] = p.priority;
            }

            // 找到priority最小的PCB,更新queue
            int Pri_Npree_index = findIndexByMinValue(processpriority);
            this.queue.Ready_Queue.remove((Object) Pri_Npree_index);
            this.readyToRunning(Pri_Npree_index);
        }
    }

    // 多级队列调度,第一级采用优先级调度,第二级采用FCFS调度
    void MLFQ() {
        int size1 = this.queue.Ready_Queue.size();
        int size2 = this.queue.Second_Queue.size();
        if (size1 > 0) {
            Priority_NonPreemptive();
        } else if (size2 > 0) {
            int pid = this.queue.Second_Queue.removeFirst().getPid();
            this.readyToRunning(pid);
        }

    }

    // 多级队列动态调整
    void MLFQ_DynamicAdjust() {
        if (this.strategy != scheduleStrategy.MLFQ) {
            return;
        }

        int size = this.queue.Second_Queue.size();
        if (size > 0) {
            // 防止二级队列的进程饿死
            for (int i = 0; i < this.queue.Second_Queue.size(); i++) {
                TTLItem item = this.queue.Second_Queue.get(i);
                item.setTTL(item.getTTL() + 100);
                if (item.getTTL() > this.Second_Queue_Threshold) {
                    this.queue.Ready_Queue.add(item.getPid());
                    this.queue.Second_Queue.remove(item);
                    i--;
                }
            }
        }

    }

    /*
     * 中期调度
     */

    // 换入进程选择
    // 算法：
    public int Choose_SwappedIn() {
        if (this.queue.Swapped_Ready_Queue.isEmpty() && this.queue.Swapped_Waiting_Queue.isEmpty()) {
            return -1;
        }
        int choose_pid = -1;
        int choose_ttl = -1;
        for (TTLItem item : this.queue.Swapped_Ready_Queue) {
            if (choose_pid == -1) {
                choose_pid = item.getPid();
                choose_ttl = item.getTTL();
            } else if (item.getTTL() > choose_ttl) {
                choose_pid = item.getPid();
                choose_ttl = item.getTTL();
            }
        }

        for (TTLItem item : this.queue.Swapped_Waiting_Queue) {
            if (choose_pid == -1) {
                choose_pid = item.getPid();
                choose_ttl = item.getTTL();
            } else if (item.getTTL() > choose_ttl) {
                choose_pid = item.getPid();
                choose_ttl = item.getTTL();
            }
        }
        return choose_pid;
    }

    // 换出进程选择
    // 算法：结合进程内存占用大小和进程优先级
    public int Choose_SwappedOut() {
        int choose_pid = -1;
        for (PCB p : this.ProcessMap.values()) {
            // 换出进程条件：不是init进程/不是当前正在执行的进程/选择的进程不能已经处于Swapped Space
            if (p.p_id != 0 && p.p_id != this.current_pid) {
                if (p.state != P_STATE.SWAPPED_READY && p.state != P_STATE.SWAPPED_WAITING) {
                    if (choose_pid == -1) {
                        choose_pid = p.p_id;
                    }
                    if (SwappedOut_isPrior(p.p_id, choose_pid)) {
                        choose_pid = p.p_id;
                    }
                }
            }
        }

        return choose_pid;
    }

    private boolean SwappedOut_isPrior(int pid, int choose_pid) {
        PCB p1 = this.ProcessMap.get(pid);
        PCB p2 = this.ProcessMap.get(choose_pid);
        // 换出内存占用最大的进程,如果有多个一样最大的进程，换出优先级最低(数值最大)的进程
        if (p1.memory_allocate > p2.memory_allocate
                || (p1.memory_allocate == p2.memory_allocate && p1.priority > p2.priority))
            return true;
        return false;
    }

    public boolean isInReadyQueue(int pid) {
        return this.queue.Ready_Queue.contains(pid);
    }

    public void SwappedProcess_Update() {
        for (TTLItem item : this.queue.Swapped_Ready_Queue) {
            item.setTTL(item.getTTL() + 1);
        }
        for (TTLItem item : this.queue.Swapped_Waiting_Queue) {
            item.setTTL(item.getTTL() + 1);
        }
    }

    /*
     * 信号量wait和signal命令封装
     */

    void Semaphore_Wait(int pid) {
        PCB p = this.ProcessMap.get(pid);
        Instruction ir = p.ir;

        if (!this.SemaphoreMap.containsKey((String) ir.getArguments()[0])) {
            // 该进程wait一个不存在的信号量
            this.sendInterrupt(InterruptType.SystemCall, SystemCallType.ProcessExit, pid);
            this.current_pid = -1;
            return;
        }

        if (this.SemaphoreMap.get((String) ir.getArguments()[0]).wait(pid)) {
            p.pc += this.kernel.getSysData().InstructionLength;
        } else {
            // wait 返回false 说明该信号量资源<0,无法分配,所以该进程进入等待状态
            this.RunningtoWaiting(5);
        }
    }

    void Semaphore_Signal(int pid) {
        PCB p = this.ProcessMap.get(pid);
        Instruction ir = p.ir;

        if (!this.SemaphoreMap.containsKey((String) ir.getArguments()[0])) {
            // 该进程wait一个不存在的信号量
            this.sendInterrupt(InterruptType.SystemCall, SystemCallType.ProcessExit, pid);
            this.current_pid = -1;
            return;
        }

        if (this.SemaphoreMap.get((String) ir.getArguments()[0]).signal(pid)) {
            p.pc += this.kernel.getSysData().InstructionLength;
        } else {
            // signal 返回false 说明该进程之前没有wait该信号量,处理方式为非法访问,直接结束该进程
            this.sendInterrupt(InterruptType.SystemCall, SystemCallType.ProcessExit, pid);
            this.current_pid = -1;
        }
    }

    void Semaphore_update() {

        for (mySemaphore semaphore : this.SemaphoreMap.values()) {
            // 清空没有引用的semaphore
            if (semaphore.isClear()) {
                this.SemaphoreMap.remove(semaphore.name);
                continue;
            }

            // 对于有资源的semaphore,唤醒wait的进程
            if (semaphore.resource >= 0) {
                int pid = semaphore.WakeProcess();
                if (pid != -1)
                    this.waitToReady(pid);
            }

            semaphore.ttl--;

        }

    }

    /*
     * fork update操作
     */
    public void ForkWaitUpdate() {
        for (int i = 0; i < this.queue.Waiting_Queues.get(4).size(); i++) {
            if (ProcessMap.get(this.queue.Waiting_Queues.get(4).get(i)).c_id.size() == 0) {
                int pid = this.queue.Waiting_Queues.get(4).remove(i);
                this.waitToReady(pid);
                this.ProcessMap.get(pid).pc += this.kernel.getSysData().InstructionLength;
            }
        }
    }

    /*
     * 挂载设备
     */
    public void MountDevice(String ResourceName) {
        for (PCB p : this.ProcessMap.values()) {
            if (p.p_id == 0)
                continue;
            p.MountDevice(ResourceName);
        }
    }

    /*
     * 向前端传数据的接口
     */
    public List<PCB> getPCBs() {
        List<PCB> pcbList = new ArrayList<>();
        for (PCB item : this.ProcessMap.values()) {
            if (item.p_id == 0) {
                continue;
            } else {
                pcbList.add(item);
            }
        }
        return pcbList;
    }

    public ProcessQueue getQueue() {
        return this.queue;
    }

    public Map<String, Integer> getResourceMap(int pid) {
        return this.ProcessMap.get(pid).getAvailable();
    }
}