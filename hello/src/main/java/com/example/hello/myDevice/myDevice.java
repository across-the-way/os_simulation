package com.example.hello.myDevice;

import com.example.hello.controller.myKernel;
import com.example.hello.myInterrupt.InterruptType;
import com.example.hello.myInterrupt.myInterrupt;

import java.util.*;

public class myDevice {
    private myKernel kernel;
    private LinkedList<Device> devices;
    public int count = 0;

    public myDevice(myKernel kernel) {
        this.kernel = kernel;
        devices = new LinkedList<>();

        for (int i = 0; i < this.kernel.getSysData().Keyboard_Number; i++)
            devices.add(new Device("keyboard", count++));
        for (int i = 0; i < this.kernel.getSysData().Printer_Number; i++)
            devices.add(new Device("printer", count++));
        for (int i = 0; i < this.kernel.getSysData().OtherDevice_Number; i++)
            devices.add(new Device("device..", count++));
    }

    public void update() {
        // 检查当前是否有IO设备队列中是否有任务，若无，返回
        boolean busy_flag = false;
        for (Device device : devices) {
            if (device.isBusy())
                busy_flag = true;
        }
        if (!busy_flag)
            return;
        // 更新所有被进程占用的IO设备
        for (Device device : devices) {
            if (device.isBusy()) {
                int remain_IOburst = device.getWaitQueue().getFirst().getIotime()
                        - this.kernel.getSysData().SystemPulse;
                if (remain_IOburst > 0) {
                    device.getWaitQueue().getFirst().setIotime(remain_IOburst);
                    System.out.println(
                            device.getType() + " is occupied by process" + device.getWaitQueue().getFirst().getPid()
                                    + ", remain burst :" + remain_IOburst
                                    + "ms!");
                } else {
                    System.out.println(
                            device.getType() + "burst for process" + device.getWaitQueue().getFirst().getPid()
                                    + " finishes!");
                    int pid = device.getWaitQueue().pop().getPid();
                    this.kernel.receiveInterrupt(new myInterrupt(InterruptType.IOFinish, pid));
                    if (device.getWaitQueue().isEmpty())
                        device.setBusy(false);
                }
            } else {
                if (!device.getWaitQueue().isEmpty()) {
                    device.setBusy(true);
                }
            }
        }
        // 若进程释放设备，即IO完成
        // 移出进程
        // 发送中断IOFinish
        // 调度下一个进程
    }

    public void request(int type, int pid, int usage_time) {
        // 往设备device对应的调度队列添加任务
        String changedType;
        int minQueueSize = Integer.MAX_VALUE;
        Device minDevice = null;
        if (type == 0) {
            changedType = "printer";
        } else {
            changedType = "keyboard";
        }
        for (Device device : devices) {
            int queueSize = device.getWaitQueue().size();
            if (device.getType().equals(changedType) && queueSize < minQueueSize) {
                minDevice = device;
                minQueueSize = queueSize;
            }
        }
        if (minDevice != null) {
            minDevice.getWaitQueue().add(new ioRequest(pid, usage_time * this.kernel.getSysData().SystemPulse));
            minDevice.setBusy(true);
        }
    }

    public void addDevice(String type, int deviceid) {
        devices.add(new Device(type, deviceid));
    }

    public boolean deleteDevice(int num) {
        if (devices.get(num - 1).getWaitQueue().size() == 0) {
            devices.remove(num - 1);
            return true;
        }
        return false;
    }

    public List<Device> get() {
        return this.devices;
    }
}