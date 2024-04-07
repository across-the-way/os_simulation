package com.example.hello.controller;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.hello.myDevice.myDevice;
import com.example.hello.myFile.myFile;
import com.example.hello.myMemory.myMemory;
import com.example.hello.myProcess.PCB;
import com.example.hello.myProcess.myProcess;
import com.example.hello.myProcess.scheduleStrategy;

public class myKernel implements Runnable {
    private static myKernel instance;

    private myClock timer;
    private myProcess pm;
    private myMemory mm;
    private myFile fs;
    private myDevice io;

    public myProcess getPm() {
        return this.pm;
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

    public void receiveInterrupt(myInterrupt interrupt) {
        queue.offer(interrupt);
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
                    case GUICall:
                    case Exit:
                    default:
                        break;
                }
            }
            timer.resume();
        }
    }

    private void page(Object[] objects) {
        mm.page(0, 0);
    }

    private void update() {
        pm.update();
        mm.update();
        fs.update();
        io.update();
    }

    private void timeout(Object[] objects) {
        pm.schedule();
    }

    private void finish(Object[] objects) {
        pm.waitToReady((int) objects[0]);
        pm.getPCB((int)objects[0]).pc+=this.getSysData().InstructionLength;
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
        }
    }

    private void touch(Object[] objects) {
        fs.touch((String) objects[1], (String) objects[2]);
    }

    private void rm(Object[] objects) {
        int last=((String)objects[1]).lastIndexOf('/');
        fs.rm(((String)objects[1]).substring(0,last),((String)objects[1]).substring(last+1));
    }

    private void mkdir(Object[] objects) {
        fs.mkdir((String)objects[1], (String)objects[2]);
    }

    private void rmdir(Object[] objects) {
        int last=((String)objects[1]).lastIndexOf('/');
        fs.rmdir(((String)objects[1]).substring(0,last),((String)objects[1]).substring(last+1));
    }

    private void open(Object[] objects) {
        int pid = 0;
        // 获得文件号
        int fd = fs.open(pid, null);
        // 将文件号添加到pid进程的打开文件表
        if (fd != -1) {
            pm.addOpenFile(pid, fd);
        }
    }

    private void close(Object[] objects) {
        int pid = 0;
        int fd = -1;
        // 将文件号从pid进程的打开文件表中移除
        pm.removeOpenFile(pid, fd);
        // 更新系统打开文件表
        fs.close(0, fd);
    }

    private void write(Object[] objects) {
        int pid = (int) objects[0];
        int fd = fs.open(pid, (String) objects[1]);
        int usage_size = (int) objects[2];
        fs.write(pid, fd, usage_size);
    }

    private void read(Object[] objects) {
        int pid = (int) objects[0];
        int fd = fs.open(pid, (String) objects[1]);
        int usage_time = (int)objects[2];
        fs.read(pid, fd, usage_time);
    }

    private void create(Object[] objs) {

        int pid = pm.createPCB(objs);
        pm.addToLongTermQueue(pid);
        // if (mm.allocate(pid, 0)) {
        // pm.addToLongTermQueue(pid);
        // } else {
        // pm.deletePCB(pid);
        // }

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
        // if (mm.allocate(pid, 0)) {
        // pm.addToLongTermQueue(pid);
        // } else {
        // pm.deletePCB(pid);
        // return false;
        // }
        pm.addToLongTermQueue(pid);

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
        io.request((int) objects[0], (int) objects[1], (int) objects[2]);
    }

    /*
     * 即时响应
     */

    public boolean isPageFault(int pid, int pc) {
        return mm.isPageFault(pid, pc);
    }

    @Override
    public void run() {
        timer = new myClock(instance);
        pm = new myProcess(instance);
        mm = new myMemory(instance);
        fs = new myFile(instance);
        io = new myDevice(instance);
        Thread timerThread = new Thread(timer);
        timerThread.start();

        interruptHandle();
    }
}
