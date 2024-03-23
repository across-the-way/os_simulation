package com.example.hello.myProcess;

import java.util.Collection;
import java.util.Map;

import com.example.hello.controller.InterruptType;
import com.example.hello.controller.myInterrupt;
import com.example.hello.controller.myKernel;

public class myProcess {
    private myKernel kernel;

    public myProcess(myKernel kernel) {
        this.kernel = kernel;
    }

    private void sendInterrupt(InterruptType interrupt) {
        kernel.receiveInterrupt(new myInterrupt(interrupt));
    }

    private scheduleStrategy strategy;
    private int next_pid;
    private int current_pid;

    Map<Integer, PCB> ppmap;
    Collection<Integer> long_term_queue;

    public void update() {
        // 检查当前是否有进程在运行
            // 若无，从长期调度队列中调度出一个进程，尝试切换为就绪态
                // 若无或无法，返回
                // 若可，从就绪队列中调度出一个进程，尝试切换为运行态
                    // 若无法，返回
        // 模拟当前进程运行
        processRunning();
        
        // 检查当前进程是否已经完成
            //若完成
                //发送中断系统调用ProcessExit
                //启动进程调度（短期和长期）,切换进程
                //返回
        
        // 检查当前进程时间片是否已经超时
            //若超时
                //发送中断TimeSilceRunOut
                //返回
    }

    private void processRunning() {
        // 更新当前进程的剩余时间和当前时间片剩余时间

        // 若PC值超限，发送中断PageFault
        // 调用kernel.isPageFault

        // 读取PC更新IR，并更新PC
        
        // 根据IR当前指令，推进当前进程
            // 若指令为计算，更新当前指令剩余时间
            // 若指令为文件读写或IO读写
                // 转换当前进程状态从running为waiting，移入等待队列
                // 发送中断系统调用FileRead、FileWrite、IORequest
                // 启动进程调度（短期和长期）,切换进程
            // 若指令为文件或目录操作
                // 发送中断系统调用FileNew、FileDelete、DirNew、DirDelete
            // 若指令为fork
                // 发送中断系统调用ProcessNew，或者。。。
            // 若指令为cond系列操作
                // CondNew为进程和子进程们添加信号量
                //
                //
            // 若指令为wait，且合法（有子进程）
                // 转换当前进程状态从running为waiting，移入等待队列
                // 启动进程调度（短期和长期）,切换进程
            // 若指令非法
                 // 发送中断系统调用ProcessExit
    }

    public int createPCB() {
        // 新建一个PCB
            // 分配PID为next_pid
            // 初始化PCB的各个字段
        // 加入pid-PCB映射表
        return next_pid++;
    }

    public void deletePCB(int pid) {
        // 将PCB从pid-PCB映射表中删除

        // 将其子进程转交其父进程或0进程

        // 若父进程调用过wait，移入就绪队列
    }

    public void addToLongTermQueue(int pid) {
        // pid入长期调度队列
    }
    
    public int getFromLongTermQueue() {
        // 检查后，按照长期调度策略返回长期调度队列的队首
        return 1;
    }

    public void schedule() {
        // 仅和RR相关，自行设计
    }

    public boolean newToReady(int pid) {
        
        return false;
    }

    public boolean readyToRunning(int pid) {
        return false;
    }

    // 还有若干状态转换方法，略

    public void waitToReady(int i) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'waitToReady'");
    }

    public void addOpenFile(int pid, int fd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addOpenFile'");
    }

    public void removeOpenFile(int pid, int fd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeOpenFile'");
    }
}
