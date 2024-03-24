package com.example.hello.myProcess;

import java.util.List;
import java.util.Map;

import com.example.hello.deadlock.*;
import com.example.hello.myInstrunction.Instruction;

public class Process {
    // 用pid对应唯一的PCB
    public Map<Integer, PCB> ProcessMap;
    public ProcessQueue queue;
    public List<PCB> ProcessList;
    public PCB Selected_Process;
    private Map<String, Integer> available;
    private int id;

    // 创建进程
    public boolean createProcess(List<Instruction> instructions) {
        // // 1.创建PCB,根据指令填入PCB的对应字段(state,pid,priority,memory_allocate,resource_map,bursts)
        // PCB p = new PCB(instructions);

        // // 2.根据进程占用内存大小采用memory manager分配内存,填充memory_start字段
        // // 2.1根据调用分配内存api的结果,如果分配失败,则进程创建失败,函数返回false

        // // 3.根据进程need资源和当前资源情况判断分配资源给该进程是否会导致死锁
        // // 3.1 如果无法找出一个安全队列,则进程创建失败,函数返回false
        // Map<String, Integer> need = Banker.CalNeed(p.maxresourceMap, p.allocateresourceMap);
        // Map<String, Integer> newavailable = Banker.CalNeed(available, need);
        // Banker banker = new Banker(ProcessList, available);
        // if (banker.isSafe() == false)
        //     return false;
        // else
        //     available = newavailable;

        // // 4.将创建的PCB加入ready队列,更新当前运行的PCB的c_pid，创建的PCB 的pp_id
        // p.p_id = id++;
        // p.pp_id = Selected_Process.p_id;
        // Selected_Process.c_id.add(p.p_id);
        // ProcessMap.put(p.p_id, p);
        // queue.Ready_Queue.add(p);

        return true;
    }

    // 结束进程
    public void terminatedProcess(int pid) {
        // 1. memory manager 释放对应进程及其子进程占用的内存(递归释放),并合并碎片(如果可能的话)

        // 2. 更新pid进程相应的队列

        // 3. 从ready队列中dispatch一个进程,更新ready队列和selected process(可通过中断通知CPU)
    }

    // 挂起当前进程
    public void suspendProcess() {
        // 1. 保留现场，即更新pc为当前执行完的指令的下一条

        // 2. 将该进程根据 如果是timeout(原来还有interupt软中断,这里不考虑) 加入ready队列 / I/O
        // interupt(外中断)加入waiting队列

        // 3. 更新当前Process的 waiting_time
    }

    // 恢复挂起的进程(这里指waiting队列中的进程 I/O 完成)
    public void restoreProcess(int pid) {
        // 1. 将该进程 从 waiting队列移除，加入 ready队列 等待CPU调度
        // 2. 更新当前Process的 lastready_time
    }

}
