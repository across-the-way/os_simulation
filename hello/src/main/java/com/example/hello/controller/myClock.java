package com.example.hello.controller;

public class myClock implements Runnable {
    private myKernel kernel;
    private int pulseCount;
    private boolean paused = false;

    public myClock(myKernel kernel) {
        this.kernel = kernel;
        this.pulseCount = 0;
    }

    private void sendInterrupt(InterruptType interrupt) {
        kernel.receiveInterrupt(new myInterrupt(interrupt));
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notify();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(100); // 模拟时钟脉冲间隔
                System.out.println("时钟发出第 " + pulseCount + " 次脉冲中断信号。");
                pulseCount++;

                //System.out.println("时钟暂停");
                pause();

                sendInterrupt(InterruptType.TimerInterrupt);
                synchronized (this) {
                    while (paused) {
                        wait();
                    }
                }
                //System.out.println("时钟恢复");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
