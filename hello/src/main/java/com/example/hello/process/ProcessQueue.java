package com.example.hello.process;

import java.util.*;

public class ProcessQueue {

    // waiting queue里的进程等待I/O设备,实际I/O设备有多种,故每种设备应该有独自的waiting队列
    public List<PCB> Waiting_Queue;

    public List<PCB> Ready_Queue;
    public List<PCB> Second_Queue;
    // public List<PCB> Job_Pool;

    public ProcessQueue() {
        this.Waiting_Queue = new ArrayList<>();
        this.Ready_Queue = new ArrayList<>();
    }

    public List<PCB> getWaiting_Queue() {
        return Waiting_Queue;
    }

    public void setWaiting_Queue(List<PCB> waiting_Queue) {
        Waiting_Queue = waiting_Queue;
    }

    public List<PCB> getReady_Queue() {
        return Ready_Queue;
    }

    public void setReady_Queue(List<PCB> ready_Queue) {
        Ready_Queue = ready_Queue;
    }

}
