package com.example.hello.myDevice;

import com.example.hello.controller.myKernel;

public class myDevice {
    private myKernel kernel;

    public myDevice(myKernel kernel) {
        this.kernel = kernel;
    }

    public void update() {
        // 检查当前是否有IO设备被进程占用，若无，返回

        // 更新所有被进程占用的IO设备
            // 若进程释放设备，即IO完成
                // 移出进程
                // 发送中断IOFinish
                // 调度下一个进程
    }

    public void request(int device, int pid, int usage_time) {
        // 往设备device对应的调度队列添加任务
    }
    
}