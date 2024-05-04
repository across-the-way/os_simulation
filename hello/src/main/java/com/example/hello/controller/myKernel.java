package com.example.hello.controller;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import com.example.hello.SysConfig.SysData;
import com.example.hello.myDevice.Device;
import com.example.hello.myDevice.myDevice;
import com.example.hello.myFile.myFile;
import com.example.hello.myInterrupt.SystemCallType;
import com.example.hello.myInterrupt.myInterrupt;
import com.example.hello.myMemory.myMemory;
import com.example.hello.myProcess.PCB;
import com.example.hello.myProcess.myProcess;
import com.example.hello.myProcess.scheduleStrategy;
import com.example.hello.myProcess.PCB.P_STATE;
import com.example.hello.myTerminal.TerminalCallType;
import com.example.hello.myTerminal.Terminalfunc;
import com.example.hello.myTest.myTest;

import jakarta.websocket.OnClose;

public class myKernel implements Runnable {
    private static myKernel instance;

    private myClock timer;
    private myProcess pm;
    private myMemory mm;
    private myFile fs;
    private myDevice io;

    private myTest test;

    public myProcess getPm() {
        return this.pm;
    }

    public myFile getFs() {
        return this.fs;
    }

    public myDevice getIo() {
        return io;
    }

    public myMemory getMm() {
        return this.mm;
    }

    private myKernel() {
    }

    public static myKernel getInstance() {
        if (instance == null) {
            instance = new myKernel();
        }
        return instance;
    }

    private SysData sysData;

    public void setConfig(SysData sysData2) {
        this.sysData = sysData2;
    }

    public SysData getSysData() {
        return this.sysData;
    }

    ConcurrentLinkedQueue<myInterrupt> queue = new ConcurrentLinkedQueue<>();
    public String terminal_message;
    public Boolean terminal_update;
    public Semaphore terminal_mutex;
    public Boolean System_stop;

    public void receiveInterrupt(myInterrupt interrupt) {
        queue.offer(interrupt);
    }

    private Thread kernelThread;

    public void start() {
        kernelThread = new Thread(this);
        kernelThread.start();
    }

    public void stop() {
        if (kernelThread != null) {
            kernelThread.interrupt();
            kernelThread = null;
        }
    }

    /*
     * 中断处理
     */
    private void interruptHandle() {
        while (true) {
            while (queue.isEmpty())
                ;
            while (!queue.isEmpty()) {
                // System.out.println(queue.size());
                myInterrupt interrupt = queue.poll();
                switch (interrupt.getType()) {
                    // 需要对objects具体化，待完善
                    case TimerInterrupt:
                        update();
                        break;
                    case TimeSilceRunOut:
                        timeout(interrupt.getObjects());
                        break;
                    case IOFinish:
                        finish(interrupt.getObjects());
                        break;
                    case PageFault:
                        page(interrupt.getObjects());
                        break;
                    case SystemCall:
                        systemCall(interrupt.getObjects());
                        break;
                    case FileFinish:
                        finish(interrupt.getObjects());
                        break;
                    case TerminalCall:
                        terminalCall(interrupt.getObjects());
                        break;
                    case SwappedIn:
                        handleSwappedIn();
                        break;
                    case SwappedOut:
                        handleSwappedOut();
                        break;
                    case StopSystem:
                        StopSystem();
                        break;
                    case StartSystem:
                        StartSystem();
                        break;
                    case SinglePause:
                        SinglePause();
                        break;
                    case MountDevice:
                        MountDevice(interrupt.getObjects());
                        break;
                    case Exit:
                    default:
                        break;
                }
            }
            if (!System_stop)
                timer.resume();
        }
    }

    private void page(Object[] objects) {
        mm.page((int) objects[0], (int) objects[1]);
    }

    private void update() {
        pm.update();
        mm.update();
        fs.update();
        io.update();

        test.doTest();
    }

    private void timeout(Object[] objects) {
        pm.schedule();
    }

    private void finish(Object[] objects) {
        int pid = (int) objects[0];
        String[] deviceMap = new String[7];
        deviceMap[0] = "printer";
        deviceMap[1] = "keyboard";
        deviceMap[6] = "device..";
        deviceMap[2] = deviceMap[3] = "file";
        int DeviceNumber = pm.getPCB(pid).waiting_for;
        pm.getPCB(pid).FreeResource(deviceMap[DeviceNumber]);
        this.getSysData().FreeResource(deviceMap[DeviceNumber]);

        if (pm.getPCB(pid).state == P_STATE.WAITING)
            pm.waitToReady(pid);
        else
            pm.SwappedWaitingToSwappedReady(pid);
        pm.getPCB(pid).pc += this.getSysData().InstructionLength;
        if (objects.length > 1) {
            String reader = (String) objects[1];
            if (!reader.equals(""))
                System.out.println("Reader:" + reader);
        }
    }

    /*
     * 系统调用
     */

    private void systemCall(Object[] objects) {
        // 需要对objects具体化，待完善
        SystemCallType type = (SystemCallType) objects[0];
        objects = Arrays.copyOfRange(objects, 1, objects.length);
        switch (type) {
            case ProcessNew:
                create(objects);
                break;
            case ProcessExit:
                release(objects);
                break;
            case Fork:
                Fork(objects);
                break;
            case IORequest:
                request(objects);
                break;
            case FileNew:
                touch(objects);
                break;
            case FileDelete:
                rm(objects);
                break;
            case DirNew:
                mkdir(objects);
                break;
            case DirDelete:
                rmdir(objects);
                break;
            case FileOpen:
                open(objects);
                break;
            case FileClose:
                close(objects);
                break;
            case FileRead:
                read(objects);
                break;
            case FileWrite:
                write(objects);
                break;
            default:
                break;
        }
    }

    private void touch(Object[] objects) {
        fs.touch((String) objects[1], (String) objects[2]);
    }

    private void rm(Object[] objects) {
        int last = ((String) objects[1]).lastIndexOf('/');
        fs.rm(((String) objects[1]).substring(0, last), ((String) objects[1]).substring(last + 1));
    }

    private void mkdir(Object[] objects) {
        fs.mkdir((String) objects[1], (String) objects[2]);
    }

    private void rmdir(Object[] objects) {
        int last = ((String) objects[1]).lastIndexOf('/');
        fs.rmdir(((String) objects[1]).substring(0, last), ((String) objects[1]).substring(last + 1));
    }

    private void open(Object[] objects) {
        int pid = (int) objects[0];
        // 获得文件号
        int fd = fs.open(pid, (String) objects[1]);
        // 将文件号添加到pid进程的打开文件表
        if (fd != -1) {
            pm.addOpenFile(pid, fd);
            pm.getPCB(pid).AllocateResource("file");
        }
    }

    private void close(Object[] objects) {
        int pid = (int) objects[0];
        int fd = fs.getFtable().findFdBypath(pid, (String) objects[1]);
        // 将文件号从pid进程的打开文件表中移除
        pm.removeOpenFile(pid, fd);
        pm.getPCB(pid).FreeResource("file");
        // 更新系统打开文件表
        fs.close(pid, fd);
    }

    private void write(Object[] objects) {
        int pid = (int) objects[0];
        int fd = fs.open(pid, (String) objects[1]);
        if (fd != -1) {
            boolean first = pm.addOpenFile(pid, fd);
            // 第一次打开该文件需要分配资源
            if (first) {
                pm.getPCB(pid).AllocateResource("file");
                this.getSysData().AllocateResource("file");
            }
        }
        String content = objects[2].toString();
        fs.write(pid, fd, content);
    }

    private void read(Object[] objects) {
        int pid = (int) objects[0];
        int fd = fs.open(pid, (String) objects[1]);
        if (fd != -1) {
            boolean first = pm.addOpenFile(pid, fd);
            // 第一次打开该文件需要分配资源
            if (first) {
                pm.getPCB(pid).AllocateResource("file");
                this.getSysData().AllocateResource("file");
            }
        }
        int usage_time = (int) objects[2];
        fs.read(pid, fd, usage_time);
    }

    private void create(Object[] objs) {

        int pid = pm.createPCB(objs);
        if (mm.allocate(pid, pm.getPCB(pid).memory_allocate)) {
            pm.addToLongTermQueue(pid);
        } else {
            pm.deletePCB(pid);
        }

    }

    private void release(Object[] objects) {
        int pid = (int) objects[0];
        pm.deletePCB(pid);
        mm.release(pid);
        System.out.println("Process" + pid + " is Exiting By SystemCall ProcessExit");
    }

    /*
     * fork处理
     */

    public boolean Fork(Object[] objects) {
        int pp_id = (int) objects[0];
        int index = (int) objects[1];

        PCB p = this.pm.getPCB(pp_id);
        if (index <= (p.pc - p.memory_start) / this.getSysData().InstructionLength) {
            return false;
        }
        if (index >= p.bursts.size()) {
            return false;
        }

        int pid = this.pm.ForkPCB(pp_id, index);
        if (mm.allocate(pid, 0)) {
            pm.addToLongTermQueue(pid);
        } else {
            pm.deletePCB(pid);
            return false;
        }

        if (objects.length > 2) {
            String option = (String) objects[2];
            // 当前进程阻塞直至所有子进程执行完
            if (option.equals("Wait")) {
                this.pm.RunningtoWaiting(4);
            } else {
                this.pm.getPCB(pp_id).pc += this.getSysData().InstructionLength;
                if (this.getSysData().CPUstrategy == scheduleStrategy.MLFQ) {
                    this.pm.RunningtoSecondReady();
                } else {
                    this.pm.RunningtoReady();
                }
            }
        } else {
            this.pm.getPCB(pp_id).pc += this.getSysData().InstructionLength;
            if (this.getSysData().CPUstrategy == scheduleStrategy.MLFQ) {
                this.pm.RunningtoSecondReady();
            } else {
                this.pm.RunningtoReady();
            }
        }

        return true;
    }

    private void request(Object[] objects) {
        if ((int) objects[0] == 0) {
            pm.getPCB((int) objects[1]).AllocateResource("printer");
            this.getSysData().AllocateResource("printer");
        } else if ((int) objects[0] == 1) {
            pm.getPCB((int) objects[1]).AllocateResource("keyboard");
            this.getSysData().AllocateResource("keyboard");
        } else if ((int) objects[0] == 6) {
            pm.getPCB((int) objects[1]).AllocateResource("device..");
            this.getSysData().AllocateResource("device..");
        } else {
            return;
        }
        io.request((int) objects[0], (int) objects[1], (int) objects[2]);
    }

    /*
     * 即时响应
     */

    public boolean isPageFault(int pid, int pc) {
        return mm.isPageFault(pid, pc);
    }

    /*
     * 处理中期调度中断
     */
    private void handleSwappedIn() {
        while (true) {
            // 按照中期调度换入算法选择一个要换入的进程（注：为方便处理，二级队列的换入后进入就绪队列）
            int pid = this.pm.Choose_SwappedIn();
            if (pid == -1) {
                return;
            }
            // 进程状态转换
            if (this.pm.getPCB(pid).state == P_STATE.SWAPPED_READY) {
                this.pm.SwappedReadyToReady(pid);
            } else if (this.pm.getPCB(pid).state == P_STATE.SWAPPED_WAITING) {
                this.pm.SwappedWaitingToWaiting(pid);
            } else {
                return;
            }
            // 进程状态恢复(从Swapped Space 读取)，并删除Swapped Space 中对应的记录
            // 重新为进程分配空间
            mm.swapIn(pid, pm.getPCB(pid).pc);
            // 检查内存负载情况，如果负载低于下限，则重复上述步骤
            if (!mm.isLower()) {
                return;
            }
        }
    }

    private void handleSwappedOut() {
        while (true) {
            // 按照中期调度换出算法选择一个要换出的进程(ready(包括二级) 队列/waiting 队列)
            int pid = this.pm.Choose_SwappedOut();
            if (pid == -1) {
                return;
            }
            // 进程状态转换
            if (pm.getPCB(pid).state == P_STATE.READY) {
                pm.ReadyToSwappedReady(pid);
            } else if (pm.getPCB(pid).state == P_STATE.WAITING) {
                pm.WaitingToSwappedWaiting(pid);
            } else {
                return;
            }
            // 释放进程占用的内存空间, 将进程状态写入Swapped Space
            mm.swapOut(pid);
            // 检查内存负载情况，如果负载仍然超过上限，则重复上述步骤
            if (!mm.isUpper()) {
                return;
            }
        }
    }

    // 挂载新设备处理
    private void MountDevice(Object[] objects) {
        this.io.addDevice((String) objects[0], this.io.count++);
        this.getSysData().MountDevice((String) objects[0]);
        this.pm.MountDevice((String) objects[0]);
    }

    /*
     * 处理前端终端响应
     */
    private void terminalCall(Object[] objects) {
        TerminalCallType type = (TerminalCallType) objects[0];
        objects = (Object[]) objects[1];
        switch (type) {
            case TerminalCallType.pwd:
                Terminalfunc.Terminalpwd(this);
                break;
            case TerminalCallType.cd:
                Terminalfunc.Terminalcd((String) objects[0], this);
                break;
            case TerminalCallType.touch:
                Terminalfunc.Terminaltouch((String) objects[0], this);
                break;
            case TerminalCallType.mkdir:
                Terminalfunc.Terminalmkdir((String) objects[0], this);
                break;
            case TerminalCallType.rm:
                Terminalfunc.Terminalrm(objects, this);
                break;
            case TerminalCallType.ls:
                Terminalfunc.Terminalls(objects, this);
                break;
            case TerminalCallType.cat:
                Terminalfunc.Terminalcat(objects, this);
                break;
            default:
                Terminalfunc.TerminalErr(this);
                break;
        }
    }

    /*
     * 处理系统暂停/调试等
     */

    private void StopSystem() {
        System_stop = true;
    }

    private void StartSystem() {
        System_stop = false;
    }

    private void SinglePause() {
        System_stop = false;
        Thread pauseThread = new Thread(() -> {
            try {
                Thread.sleep(this.getSysData().SystemPulse);
                System_stop = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        pauseThread.start();
    }

    @Override
    public void run() {
        timer = new myClock(instance);
        pm = new myProcess(instance);
        mm = new myMemory(instance);
        fs = new myFile(instance);
        io = new myDevice(instance);
        Thread timerThread = new Thread(timer);
        terminal_message = new String();
        terminal_update = false;
        terminal_mutex = new Semaphore(1);
        System_stop = false;

        test = new myTest(instance);

        timerThread.start();

        interruptHandle();
    }
}
