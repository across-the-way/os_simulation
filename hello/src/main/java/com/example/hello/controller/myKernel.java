package com.example.hello.controller;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.hello.myDevice.myDevice;
import com.example.hello.myFile.myFile;
import com.example.hello.myMemory.myMemory;
import com.example.hello.myProcess.myProcess;

public class myKernel implements Runnable {
    private static myKernel instance;
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
    
    ConcurrentLinkedQueue<myInterrupt> queue = new ConcurrentLinkedQueue<>();
    public void receiveInterrupt(myInterrupt interrupt) {
        queue.offer(interrupt);
    }

    private myClock timer = new myClock(instance);
    private myProcess pm = new myProcess(instance);
    private myMemory mm = new myMemory(instance);
    private myFile fs = new myFile(instance);
    private myDevice io = new myDevice(instance);
    /*
     * 中断处理
     */
    private void interruptHandle() {
        while (true) {
            while (queue.isEmpty());
            while (!queue.isEmpty()) {
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
    }

    /*
     * 系统调用
     */

    private void systemCall(Object[] objects) {
        // 需要对objects具体化，待完善
        SystemCallType type = (SystemCallType) objects[0];
        switch (type) {
            case ProcessNew:
                create(objects);
                break;
            case ProcessExit:
                release(objects);
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
            case FileFinish:
                finish(objects);
                break;
        }
    }

    private void touch(Object[] objects) {
        fs.touch(null, null);
    }

    private void rm(Object[] objects) {
        fs.rm(null, null);
    }

    private void mkdir(Object[] objects) {
        fs.mkdir(null, null);
    }
    
    private void rmdir(Object[] objects) {
        fs.rmdir(null, null);
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
        int pid = 0;
        int fd = -1;
        int usage_size = 0;
        fs.write(pid, fd, usage_size);
    }
    private void read(Object[] objects) {
        int pid = 0;
        int fd = -1;
        int usage_time = 0;
        fs.read(pid, fd, usage_time);
    }
    private void create(Object[] objs) {

        int pid = pm.createPCB();
        if (mm.allocate(pid, 0)) {
            pm.addToLongTermQueue(pid);
        } else {
            pm.deletePCB(pid);
        }

    }

    private void release(Object[] objects) {
        int pid = (int) objects[0];
        pm.deletePCB(pid);
        mm.release(pid);
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
        Thread timerThread = new Thread(timer);
        timerThread.start();

        interruptHandle();
    }
}
