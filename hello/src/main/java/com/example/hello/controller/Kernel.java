package com.example.hello.controller;

import com.example.hello.process.*;
import com.example.hello.instrunction.*;
import com.example.hello.filesystem.*;

import java.util.*;

public class Kernel {
    public List<PCB> p_list;
    public ProcessQueue queue;
    public FileSystem fileSystem;

    public Kernel() {
        p_list = new ArrayList<>();
        queue = new ProcessQueue();
        fileSystem = new FileSystem();
    }

    public ProcessQueue getQueue() {
        return queue;
    }

    public void setQueue(ProcessQueue queue) {
        this.queue = queue;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public List<PCB> getP_list() {
        return p_list;
    }

    public void setP_list(List<PCB> p_list) {
        this.p_list = p_list;
    }

    // 创建进程
    boolean createProcess(List<Instruction> instructions) {
        // 1.创建PCB,根据指令填入PCB的对应字段(state,pid,priority,memory_allocate,resource_map,need)

        // 2.根据进程占用内存大小采用memory manager分配内存,填充memory_start字段
        // 2.1根据调用分配内存api的结果,如果分配失败,则进程创建失败,函数返回false

        // 3.根据进程need资源和当前资源情况判断分配资源给该进程是否会导致死锁
        // 3.1 如果无法找出一个安全队列,则进程创建失败,函数返回false

        // 4.将创建的PCB加入ready队列

        return true;
    }

    // 结束进程
    void terminatedProcess() {
        // 1. memory manager 释放 selected process 占用的内存,并合并碎片(如果可能的话)

        // 2. 从ready队列中dispatch一个进程,更新ready队列和selected process
    }

    // 挂起当前进程
    void suspendProcess() {
        // 1. 保留现场，即更新pc为当前执行完的指令的下一条

        // 2. 将该进程根据 如果是timeout(原来还有interupt软中断,这里不考虑) 加入ready队列 / I/O
        // interupt(外中断)加入waiting队列
    }

    // 恢复挂起的进程(这里指waiting队列中的进程 I/O 完成)
    void restoreProcess(int pid) {
        // 1. 将该进程 从 waiting队列移除，加入 ready队列 等待CPU调度
    }

}
