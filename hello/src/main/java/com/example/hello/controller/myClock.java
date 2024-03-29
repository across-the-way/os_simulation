package com.example.hello.controller;

public class myClock implements Runnable {
    private myKernel kernel;
    private int pulseCount;     //记录时钟脉冲次数
    private boolean paused = false;     //时钟所处状态

    public myClock(myKernel kernel) {
        this.kernel = kernel;
        this.pulseCount = 0;
    }
    //发送中断到内核
    private void sendInterrupt(InterruptType interrupt) {
        kernel.receiveInterrupt(new myInterrupt(interrupt));
    }
    //时钟暂停
    public synchronized void pause() {
        paused = true;
    }
    //恢复时钟
    public synchronized void resume() {
        paused = false;
        notify();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(this.kernel.getSysData().SystemPulse); // 模拟时钟脉冲间隔
                // System.out.println("时钟发出第 " + pulseCount + " 次脉冲中断信号。");
                pulseCount++;

                // System.out.println("时钟暂停");
                pause();

                sendInterrupt(InterruptType.TimerInterrupt);
                synchronized (this) {
                    while (paused) {
                        wait();
                    }
                }
                // System.out.println("时钟恢复");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
