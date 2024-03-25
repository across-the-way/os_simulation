package com.example.hello.myProcess;

import java.util.*;

import com.example.hello.myInstrunction.*;

public class PCB {
    public enum P_STATE {
        NEW, READY, RUNNING, WAITING, TERMINATED, SWAPPED_READY
    }

    // 进程状态
    public P_STATE state;

    // 进程Pid / 父进程Pid / 所有子进程Pid
    public int p_id;
    public int pp_id;
    public List<Integer> c_id;

    // 进程优先级、进程指向调度队列的指针(等后续完善)
    public int priority;

    // 进程资源分配(设备资源需求,文件资源需求,CPU时间和IO时间需求序列)
    public Map<String, Integer> maxresourceMap;
    public Map<String, Integer> allocateresourceMap;

    public List<Instruction> bursts; // 这里的需求序列应采用有序map,遍历顺序和程序需求序列一致 LinkedHashMap

    // program counter
    public int pc;
    Instruction ir;

    /*
     * 等待时间,用于HRRN算法,不一定是int类型
     * 指的是进程在ready队列中的等待时间
     */
    public int waiting_time;
    // 上一次进入ready队列的时间
    public int lastready_time;

    // 内存分配，包括起始地址和分配空间(连续分配)
    public int memory_allocate;
    public int memory_start;

    // 进程等待的设备队列索引
    public int waiting_for;

    // 文件打开表
    public List<Integer> FileTable;

    // 创建init进程
    public PCB() {
        state = P_STATE.NEW;
        pc = 0;
        pp_id = -1;
        c_id = new ArrayList<>();
    }

    public PCB(Object[] oinstructions) {
        Instruction[] instructions = (Instruction[]) oinstructions[0];
        state = P_STATE.NEW;
        pc = 0;
        waiting_time = 0;

        // -1表示没有等待的设备
        waiting_for = -1;

        maxresourceMap = new HashMap<>();
        allocateresourceMap = new HashMap<>();
        bursts = new ArrayList<Instruction>();
        c_id = new ArrayList<Integer>();
        FileTable = new ArrayList<Integer>();

        for (Instruction instruction : instructions) {
            switch (instruction.getType()) {
                case InstructionType.Memory: {
                    memory_allocate = (int) instruction.getArguments()[0];
                    break;
                }
                case InstructionType.Priority: {
                    priority = (int) instruction.getArguments()[0];
                    break;
                }
                // case Instruction.Type.S: {
                // maxresourceMap.put(instruction.args.resource_name,
                // instruction.args.resource_number);
                // allocateresourceMap.put(instruction.args.resource_name, 0);
                // break;
                // }
                default: {
                    bursts.add(instruction);
                    break;
                }

            }
        }
    }

    public int getPp_id() {
        return pp_id;
    }

    public void setPp_id(int pp_id) {
        this.pp_id = pp_id;
    }

    public Map<String, Integer> getAllocateresourceMap() {
        return allocateresourceMap;
    }

    public void setAllocateresourceMap(Map<String, Integer> allocateresourceMap) {
        this.allocateresourceMap = allocateresourceMap;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getWaiting_time() {
        return waiting_time;
    }

    public void setWaiting_time(int waiting_time) {
        this.waiting_time = waiting_time;
    }

    public List<Integer> getC_id() {
        return c_id;
    }

    public void setC_id(List<Integer> c_id) {
        this.c_id = c_id;
    }

    public int getLastready_time() {
        return lastready_time;
    }

    public void setLastready_time(int lastready_time) {
        this.lastready_time = lastready_time;
    }

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

    public List<Instruction> getBursts() {
        return bursts;
    }

    public void setBursts(List<Instruction> bursts) {
        this.bursts = bursts;
    }
}