package com.example.hello.controller;

import org.apache.catalina.webresources.TomcatJarInputStream;

import com.example.hello.controller.SysData.OSState;

public class Kernel extends Thread {
    private SysData sysData;

    public Kernel(SysData sysData) {
        this.sysData = sysData;
    }

    @Override
    public void run() {
        // kernel负责监听
        while (true) {
            try {
                this.sysData.sem_os.acquire();
                // critical section
                if (this.sysData.Osmode == OSState.user) {
                    this.sysData.sem_os.release();
                    continue;
                }
                this.sysData.Osmode = OSState.kernel;
                
                // 处理中断队列
                System.out.println("Kernel running!");
                Thread.sleep(2000);
                
                this.sysData.Osmode = OSState.user;

                this.sysData.sem_os.release();

            } catch (InterruptedException e) {
                ;
            }
        }
    }
}
