package com.example.hello.controller;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.hello.myDevice.myDevice;
import com.example.hello.myFile.myFile;
import com.example.hello.myMemory.myMemory;
import com.example.hello.myProcess.myProcess;

public class myKernel implements Runnable {


    private static myKernel instance;   //'myKernel'类的唯一实例

    //确保其他类无法直接通过'new'关键字实例化'myKernel'对象
    private myKernel() {    
    }
    //用于获取'myKernel'类的唯一实例
    public static myKernel getInstance() {  

    private static myKernel instance;

    private myClock timer;
    private myProcess pm;
    private myMemory mm;
    private myFile fs;
    private myDevice io;

    private myKernel() {
    }

    public static myKernel getInstance() {
        if (instance == null) {
            instance = new myKernel();
        }
        return instance;
    }


    
    private SysData sysData;    //用于进程安全判断，银行家算法

    //设置系统配置数据
    public void setConfig(SysData sysData2) {
        this.sysData = sysData2;
    }
    
    ConcurrentLinkedQueue<myInterrupt> queue = new ConcurrentLinkedQueue<>();   //线程安全，可以被多个线程同时访问
    //将中断添加到中断队列
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

    private myClock timer;
    private myProcess pm;
    private myMemory mm;
    private myFile fs;
    private myDevice io;

    //中断处理
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
                    case TimeSilceRunOut:       //可删除，以上面的时钟代替时间片
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
                        //对于操作界面引起的中断，接受并做出处理
                    case Exit:
                        //处理操作系统结束引起的中断
                    default:
                        break;
                }
            }
            timer.resume();
        }
    }
    //缺页中断，使用页面置换算法，参数1：进程pid, 参数2：pc
    private void page(Object[] objects) {
        mm.page(0, 0);  //参数由进程提供
    }
    //更新组件状态
    private void update() {
        pm.update();
        mm.update();
        fs.update();
        io.update();
    }
    //时间片用完，重新进行CPU调度
    private void timeout(Object[] objects) {
        pm.schedule();
    }

    //IO操作完成，重新进入就绪队列


    private void finish(Object[] objects) {
        pm.waitToReady((int) objects[0]);
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
    
    //FileNew   参数1：FileNew, 参数2：进程pid, 参数3：文件路径，参数4：文件名
    private void touch(Object[] objects) {
        fs.touch(null, null);
    }

    //FileDelete    参数1：FileDelete,  参数2：进程pid, 参数3：文件路径， 参数4：文件名
    private void rm(Object[] objects) {
        fs.rm(null, null);
    }

    //DirNew    参数1：DirNew, 参数2：进程pid, 参数3：父目录路径，参数4：目录名
    private void mkdir(Object[] objects) {
        fs.mkdir(null, null);
    }
    
    //DirDelete     参数1：DirDelete, 参数2：进程pid, 参数3：目录路径， 参数4：目录名

    private void rmdir(Object[] objects) {
        fs.rmdir(null, null);
    }

    //FileOpen  参数1：FileOpen, 参数2：进程pid, 参数3：文件路径，参数4：文件名
    private void open(Object[] objects) {
        int pid = 0;
        // 获得文件号
        int fd = fs.open(pid, null);
        // 将文件号添加到pid进程的打开文件表
        if (fd != -1) {
            pm.addOpenFile(pid, fd);
        }
    }

    //FileClose  参数1：FileClose, 参数2：进程pid, 参数3：文件路径，参数4：文件名
    private void close(Object[] objects) {
        int pid = 0;
        int fd = -1;
        // 将文件号从pid进程的打开文件表中移除
        pm.removeOpenFile(pid, fd);
        // 更新系统打开文件表
        fs.close(0, fd);
    }

    //FileRead, 参数1：FileRead, 参数2：进程pid, 参数3：文件号， 参数4：读取时间
    private void write(Object[] objects) {
        int pid = 0;
        int fd = -1;
        int usage_size = 0;
        fs.write(pid, fd, usage_size);
    }

    //FileWrite, 参数1：FileWrite, 参数2：进程pid, 参数3：文件号， 参数4：读取时间
    private void read(Object[] objects) {
        int pid = 0;
        int fd = -1;
        int usage_time = 0;
        fs.read(pid, fd, usage_time);
    }
    //ProcessNew,根据中断发出方与接收方进行调整参数
    private void create(Object[] objs) {
        int pid = pm.createPCB(objs);
        if (mm.allocate(pid, 0)) {
            pm.addToLongTermQueue(pid);
        } else {
            pm.deletePCB(pid);
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

    //ProcessExit     参数1：ProcessExit，参数2：进程pid
    private void release(Object[] objects) {
        int pid = (int) objects[1];
        pm.deletePCB(pid);
        mm.release(pid);
        pm.schedule();
        System.out.println("Process" + pid + " is Exiting By SystemCall ProcessExit");
    }

    //IORequest     参数1：IORequest, 参数2：进程pid, 参数3：使用时间
    private void request(Object[] objects) {        
        io.request();       //进程中已完成设备队列的添加，可作为一个无操作的函数
    }

    /*
     * 即时响应
     */

    //进程用于判断是否缺页
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
