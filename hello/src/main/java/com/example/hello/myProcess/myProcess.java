package com.example.hello.myProcess;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.example.hello.controller.InterruptType;
import com.example.hello.controller.SystemCallType;
import com.example.hello.controller.myInterrupt;
import com.example.hello.controller.myKernel;
import com.example.hello.myInstrunction.Instruction;
import com.example.hello.myInstrunction.InstructionType;
import com.example.hello.myProcess.PCB.P_STATE;

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

    // ProcessManager模块初始化
    public myProcess(myKernel kernel) {
        this.kernel = kernel;
        next_pid = 0;
        ProcessMap = new HashMap<>();
        LongTermCounter = 0;
        ShortTermCounter = 0;

        // 创建初始进程
        PCB init = new PCB();
        init.p_id = next_pid++;
        ProcessMap.put(0, init);
    }

    // 向kernel中央模块发送中断请求
    private void sendInterrupt(InterruptType interrupt, Object... objs) {
        kernel.receiveInterrupt(new myInterrupt(interrupt, objs));
    }

    // 周期性执行
    public void update() {

        // 模拟长期调度,100个系统时间片执行1次长期调度
        this.LongTermCounter = this.LongTermCounter % 100 + 1;

        if (this.LongTermCounter == 100) {
            int pid = this.getFromLongTermQueue();
            if (pid != -1) {
                this.newToReady(pid);
            }
        }

        //如果时间片耗尽，若为RR，则进行CPU调度
        if(this.strategy == scheduleStrategy.RR)
            this.schedule();
        else if (this.current_pid == -1) {
            // 若可，从就绪队列中调度出一个进程，尝试切换为运行态
            this.schedule();
        }

        // 模拟当前进程运行
        processRunning();
    
        // 检查当前进程是否已经完成
        if (CheckFinish()) {
            // 若完成,发送中断系统调用ProcessExit
            this.sendInterrupt(InterruptType.SystemCall, SystemCallType.ProcessExit, this.current_pid);
            // 启动进程调度（短期和长期）,切换进程
            this.current_pid = -1;
            return;
            // 返回
        }

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
        p.ir = p.bursts.get(p.pc);

        // 根据IR当前指令，推进当前进程

        switch (p.ir.getType()) {
            // 若指令为计算，更新当前指令剩余时间
            case InstructionType.Calculate:
                int remain_burst = (int) p.ir.getArguments()[0] - 100;
                if (remain_burst <= 0) {
                    remain_burst = 0;
                    p.pc++;
                }
                p.ir.ModifyArgument(InstructionType.Calculate, remain_burst);
                break;
            // 若指令为文件读写或IO读写
            // 转换当前进程状态从running为waiting，移入等待队列
            // 发送中断系统调用FileRead、FileWrite、IORequest
            // 启动进程调度（短期和长期）,切换进程
            case InstructionType.Printer:
                this.RunningtoWaiting(0);
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.IORequest, p.p_id, p.ir.getArguments()[0]);
                this.schedule();
                break;
            case InstructionType.Keyboard:
                this.RunningtoWaiting(1);
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.IORequest, p.p_id, p.ir.getArguments()[0]);
                this.schedule();
                break;
            case InstructionType.ReadFile:
                this.RunningtoWaiting(2);
                int fd1 = this.ProcessMap.get(p.p_id).FileTable.get(0);
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileRead, p.p_id, fd1,  p.ir.getArguments()[0]);
                this.schedule();
                break;
            case InstructionType.WriteFile:
                this.RunningtoWaiting(3);
                int fd2 = this.ProcessMap.get(p.p_id).FileTable.get(0);
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileWrite, p.p_id, fd2,  p.ir.getArguments()[0]);
                this.schedule();
                break;

            // 若指令为文件或目录操作
            // 发送中断系统调用FileNew、FileDelete、DirNew、DirDelete
            case InstructionType.CreateFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileNew, p.p_id, p.ir.getArguments()[0],
                        p.ir.getArguments()[1]);
                this.RunningtoReady();
                this.schedule();
                break;
            case InstructionType.DeleteFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileDelete, p.p_id, p.ir.getArguments()[0],
                        p.ir.getArguments()[1]);
                this.RunningtoReady();
                this.schedule();
                break;
            case InstructionType.CreateDir:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.DirNew, p.p_id, p.ir.getArguments()[0],
                        p.ir.getArguments()[1]);
                this.RunningtoReady();
                this.schedule();
                break;
            case InstructionType.DeleteDir:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.DirDelete, p.p_id, p.ir.getArguments()[0],
                        p.ir.getArguments()[1]);
                this.RunningtoReady();
                this.schedule();
                break;
            case InstructionType.OpenFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileOpen, p.p_id, p.ir.getArguments()[0], 
                        p.ir.getArguments()[1]);
                this.RunningtoReady();
                this.schedule();
                break;
            case InstructionType.CloseFile:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.FileClose, p.p_id, p.ir.getArguments()[0],
                        p.ir.getArguments()[1]);
                this.RunningtoReady();
                this.schedule();
                break;

            // 若指令为cond系列操作
            // CondNew为进程和子进程们添加信号量
            case InstructionType.CondNew:
                break;
            case InstructionType.CondWait:
                break;
            case InstructionType.CondSignal:
                break;

            // 若指令为fork
            // 发送中断系统调用ProcessNew，或者。。。
            case InstructionType.Fork:
                break;

            // 若指令为wait，且合法（有子进程）
            // 转换当前进程状态从running为waiting，移入等待队列
            // 启动进程调度（短期和长期）,切换进程
            case InstructionType.Wait:
                break;

            // 若指令非法
            // 发送中断系统调用ProcessExit
            default:
                this.sendInterrupt(InterruptType.SystemCall, SystemCallType.ProcessExit, this.current_pid);
        }

    }

    // 检查当前进程是否已经完成
    private boolean CheckFinish() {
        PCB p = this.ProcessMap.get(this.current_pid);
        if (p.pc >= p.bursts.size()) {
            return true;
        } else
            return false;
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
        parent.c_id.remove((Object) pid);

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
            int pid = this.LongTermQueue.get(0);
            this.LongTermQueue.remove(0);
            return pid;
        }
    }

    // 模拟CPU调度
    public void schedule() {
        switch (this.strategy) {
            case scheduleStrategy.RR:
                RR();
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
        p.state = P_STATE.WAITING;
        this.queue.Waiting_Queues.get(waiting_for).add(p.p_id);
        this.current_pid = -1;
    }

    // 将运行的进程转换为READY状态，并加入相应的Ready队列
    public void RunningtoReady() {
        PCB p = this.ProcessMap.get(this.current_pid);
        p.state = P_STATE.READY;
        this.queue.Ready_Queue.add(p.p_id);
        this.current_pid = -1;
    }

    /*
     * 文件打开表模块
     */

    // 进程的文件打开表操作
    public void addOpenFile(int pid, int fd) {
        this.ProcessMap.get(pid).FileTable.add(fd);
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

    //采用RR调度进程
    void  RR(){
        int size = this.queue.Ready_Queue.size();
        if(size > 0){
            int end_pid = current_pid;
            int pid = this.queue.Ready_Queue.get(0);
            this.queue.Ready_Queue.remove(0);
            this.readyToRunning(pid);
            this.queue.Ready_Queue.add(end_pid);
        }
        else
            this.readyToRunning(current_pid);
    }

    // 采用FCFS调度进程
    void FCFS() {
        int size = this.queue.Ready_Queue.size();
        // 如果waiting队列不为空,则将第一个进程选为下一个running进程
        if (size > 0) {
            int pid = this.queue.Ready_Queue.get(0);
            this.queue.Ready_Queue.remove(0);
            this.readyToRunning(pid);
        }
    }

    // 采用SJF调度进程(非抢占式)
    void SJF() {
        int size = this.queue.Ready_Queue.size();
        // 如果waiting队列不为空,则以SJF策略选择下一个running进程
        if (size > 0) {
            // 计算ready队列中每个进程的剩余CPU执行时间
            Integer[] remain_CPUburst = new Integer[this.current_pid];
            for (int i : remain_CPUburst) {
                i = 10000;
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
            Integer[] processpriority = new Integer[this.current_pid];
            for (int i : processpriority) {
                i = 0;
            }
            for (PCB p : ProcessMap.values()) {
                if (p.p_id != 0)
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
            int pid = this.queue.Second_Queue.remove(0);
            this.readyToRunning(pid);
        }

    }
}
