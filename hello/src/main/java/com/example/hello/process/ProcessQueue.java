package com.example.hello.process;

import java.util.*;

public class ProcessQueue {

    // waiting queue里的进程等待I/O设备,实际I/O设备有多种,故每种设备应该有独自的waiting队列
    // 具体做法是在设备管理器中加入一个Map,key可以是一个设备对象,或者标识一台设备的id;value是设备对应队列的index
    // 例如 deviceMap[Device1] = 2 表示Device1设备对应的waiting队列为 waiting_queues[2]
    public List<List<PCB>> Waiting_Queues;

    public List<PCB> Ready_Queue;
    public List<PCB> Second_Queue;
    // public List<PCB> Job_Pool;

    public ProcessQueue(int DeviceNumber) {
        this.Waiting_Queues = new ArrayList<>(DeviceNumber);
        this.Ready_Queue = new ArrayList<>();
        this.Second_Queue = new ArrayList<>();
    }
}
