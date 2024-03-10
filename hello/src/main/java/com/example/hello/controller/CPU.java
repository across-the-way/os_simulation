package com.example.hello.controller;

public class CPU extends Thread {
    Kernel kernel;
    SysConfig config;

    public CPU(Kernel kernel, SysConfig config) {
        this.kernel = kernel;
        this.config = config;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(2000);
                // CPU调度选择一个进程
                // 遍历选择进程的指令序列(CPU时间,I/O时间,读写文件)
                // 同时在每条指令执行完轮询interrupt外部中断
                // 如果检查到当前interrupt缓冲区存在中断请求,则保存现场后执行中断向量表中的对应中断处理函数
                // 如果当前指令不是CPU时间,则保存现场,挂起当前执行进程,进行CPU调度
                    // 在处理中断的过程中,CPU进入kernel mode 处理完成后 回到 user mode
            } catch (InterruptedException e) {
                ;
            }

        }

    }

    // CPU 调度
    void dispatch_process() {
        switch (this.config.policy) {
            case SysConfig.Dispatch.FCFS:
                FCFS();
            case SysConfig.Dispatch.SJF:
                SJF();
            case SysConfig.Dispatch.Priority_NonPreemptive:
                Priority_NonPreemptive();
            case SysConfig.Dispatch.Priority_Preemptive:
                Priority_Preemptive();
            case SysConfig.Dispatch.HRRN:
                HRRN();
            case SysConfig.Dispatch.Multilevel:
                Multilevel();
        }
    }

    // 采用FCFS调度进程
    void FCFS() {

    }

    // 采用SJF调度进程(非抢占式)
    void SJF() {

    }

    // 采用非抢占式优先级调度
    void Priority_NonPreemptive() {

    }

    // 采用抢占式优先级调度
    void Priority_Preemptive() {

    }

    // 采用高响应比优先调度
    void HRRN() {

    }

    /*
     * 采用多级队列调度(第一级采用非抢占优先级调度,第二级采用FCFS调度,
     * 第一级执行时间超过Sysconfig的timeslice则加入第二级队列,
     * 只有第一级队列为空的时候才能调用第二级队列的进程)
     */
    void Multilevel() {

    }
}
