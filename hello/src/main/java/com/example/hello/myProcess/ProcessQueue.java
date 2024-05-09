package com.example.hello.myProcess;

import java.util.ArrayList;
import java.util.List;

public class ProcessQueue {

    // waiting queue里的进程等待I/O设备,实际I/O设备有多种,故每种设备应该有独自的waiting队列
    // 具体做法是在设备管理器中加入一个Map,key可以是一个设备对象,或者标识一台设备的id;value是设备对应队列的index
    // 例如 deviceMap[Device1] = 2 表示Device1设备对应的waiting队列为 waiting_queues[2]

    /*
     * 映射表：
     * 0 --> Printer
     * 1 --> Keyboard
     * 2 --> FileRead
     * 3 --> FileWrite
     * 4 --> ForkWait
     * 5 --> Semaphore
     * 6 --> Otherdevice
     */
    public List<List<Integer>> Waiting_Queues;

    public List<Integer> Ready_Queue;
    public List<TTLItem> Second_Queue;
    // public List<PCB> Job_Pool;

    // 中期调度队列
    public List<TTLItem> Swapped_Ready_Queue;
    public List<TTLItem> Swapped_Waiting_Queue;

    public ProcessQueue() {
        this.Waiting_Queues = new ArrayList<List<Integer>>();
        for (int i = 0; i < 10; i++) {
            List<Integer> p = new ArrayList<Integer>();
            this.Waiting_Queues.add(p);
        }

        this.Ready_Queue = new ArrayList<>();
        this.Second_Queue = new ArrayList<>();
        this.Swapped_Ready_Queue = new ArrayList<>();
        this.Swapped_Waiting_Queue = new ArrayList<>();
    }

    public void RemoveProcess(int pid) {
        for (int i = 0; i < Waiting_Queues.size(); i++) {
            for (int j = 0; j < Waiting_Queues.get(i).size(); j++) {
                if (Waiting_Queues.get(i).get(j) == pid) {
                    Waiting_Queues.get(i).remove(j);
                    return;
                }
            }
        }

        for (int i = 0; i < Ready_Queue.size(); i++) {
            if (Ready_Queue.get(i) == pid) {
                Ready_Queue.remove(i);
                return;
            }
        }

        for (int i = 0; i < Second_Queue.size(); i++) {
            if (Second_Queue.get(i).getPid() == pid) {
                Second_Queue.remove(i);
                return;
            }
        }

        for (int i = 0; i < Swapped_Ready_Queue.size(); i++) {
            if (Swapped_Ready_Queue.get(i).getPid() == pid) {
                Swapped_Ready_Queue.remove(i);
                return;
            }
        }

        for (int i = 0; i < Swapped_Waiting_Queue.size(); i++) {
            if (Swapped_Waiting_Queue.get(i).getPid() == pid) {
                Swapped_Waiting_Queue.remove(i);
                return;
            }
        }
    }

    public void RemoveProcessFromSwappedReady(int pid) {
        for (int i = 0; i < Swapped_Ready_Queue.size(); i++) {
            if (Swapped_Ready_Queue.get(i).getPid() == pid) {
                Swapped_Ready_Queue.remove(i);
                return;
            }
        }
    }

    public void RemoveProcessFromSwappedWaiting(int pid) {
        for (int i = 0; i < Swapped_Waiting_Queue.size(); i++) {
            if (Swapped_Waiting_Queue.get(i).getPid() == pid) {
                Swapped_Waiting_Queue.remove(i);
                return;
            }
        }
    }

    public void RemoveProcessFromSecondReady(int pid) {
        for (int i = 0; i < Second_Queue.size(); i++) {
            if (Second_Queue.get(i).getPid() == pid) {
                Second_Queue.remove(i);
                return;
            }
        }
    }
}

class TTLItem {
    private int pid;
    private int TTL;

    public TTLItem(int pid) {
        this.pid = pid;
        this.TTL = 0;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int ttl) {
        this.TTL = ttl;
    }
}