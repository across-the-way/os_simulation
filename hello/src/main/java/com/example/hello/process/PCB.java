package com.example.hello.process;

import java.util.*;

public class PCB {
    public enum P_STATE {
        ready, running, waiting, terminated, swapped_ready
    }

    // 进程状态
    public P_STATE state;

    // 进程Pid
    public int p_id;

    // 进程优先级、进程指向调度队列的指针(等后续完善)
    public int priority;

    // 进程资源分配(设备资源需求,文件资源需求,CPU时间和IO时间需求序列)
    public Map<String, Integer> maxresourceMap;
    public Map<String, Integer> allocateresourceMap;
    public Map<String, Integer> need; // 这里的需求序列应采用有序map,遍历顺序和程序需求序列一致 LinkedHashMap

    // 内存分配，包括起始地址和分配空间(连续分配)
    public int memory_allocate;
    public int memory_start;

    public P_STATE getState() {
        return state;
    }

    public void setState(P_STATE state) {
        this.state = state;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Map<String, Integer> getNeed() {
        return need;
    }

    public void setNeed(Map<String, Integer> need) {
        this.need = need;
    }

    public int getMemory_allocate() {
        return memory_allocate;
    }

    public void setMemory_allocate(int memory_allocate) {
        this.memory_allocate = memory_allocate;
    }

    public Map<String, Integer> getMaxresourceMap() {
        return maxresourceMap;
    }

    public void setMaxresourceMap(Map<String, Integer> maxresourceMap) {
        this.maxresourceMap = maxresourceMap;
    }

    public Map<String, Integer> getHoldresourceMap() {
        return allocateresourceMap;
    }

    public void setHoldresourceMap(Map<String, Integer> holdresourceMap) {
        this.allocateresourceMap = holdresourceMap;
    }

    public int getMemory_start() {
        return memory_start;
    }

    public void setMemory_start(int memory_start) {
        this.memory_start = memory_start;
    }
}
