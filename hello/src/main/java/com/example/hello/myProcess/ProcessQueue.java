package com.example.hello.myProcess;
import java.util.*;
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
     */
    public List<List<Integer>> Waiting_Queues;

    public List<Integer> Ready_Queue;
    public List<Integer> Second_Queue;
    // public List<PCB> Job_Pool;

    public ProcessQueue(int DeviceNumber) {
        this.Waiting_Queues = new ArrayList<>(DeviceNumber);
        this.Ready_Queue = new ArrayList<>();
        this.Second_Queue = new ArrayList<>();
    }

    //进程删除，从等待队列，就绪队列，二级队列某一存在的队列里进行删除
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
            if (Second_Queue.get(i) == pid) {
                Second_Queue.remove(i);
                return;
            }
        }
    }
}
