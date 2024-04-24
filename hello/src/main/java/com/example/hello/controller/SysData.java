package com.example.hello.controller;

import com.example.hello.myFile.*;
import com.example.hello.myMemory.*;
import com.example.hello.myProcess.*;

import java.util.*;
import java.util.concurrent.Semaphore;

public class SysData {
    public enum OSState {
        kernel, user
    }

    // public FileSystem fileSystem;
    // public Process ProcessController;
    SysConfig sysConfig;
    OSState Osmode;

    // 系统时钟脉冲设置
    public int SystemPulse;

    // 系统统一指令长度为4Byte
    public int InstructionLength;

    // 多级队列调度时二级队列的最大存在时间限制
    public int Second_Queue_Threshold;

    // 长期调度时间片
    public int LongTermScale;

    // 长期调度的上下阈值
    public int LongTerm_CeilThreshold;
    public int LongTerm_FloorThreshold;

    // 中期调度时间片
    public int MidTermScale;

    // 中期调度的上下阈值
    public double MidTerm_CeilThreshold;
    public double MidTerm_FloorThreshold;

    // 调度算法
    public scheduleStrategy CPUstrategy;

    // 内存分配算法
    public allocateStrategy MMstrategy;

    // 内存大小
    public int Memory_Size;

    // 页大小
    public int Page_Size;

    public SysData() {
        sysConfig = new SysConfig();
        // fileSystem = new FileSystem();
        // ProcessController = new Process();
        Osmode = OSState.kernel;

        // 单位毫秒
        SystemPulse = 100;

        InstructionLength = 4;

        Second_Queue_Threshold = 1000;

        LongTermScale = 100;
        MidTermScale = 10;

        CPUstrategy = scheduleStrategy.MLFQ;

        LongTerm_CeilThreshold = 20;
        LongTerm_FloorThreshold = 3;

        MidTerm_CeilThreshold = 0.8;
        MidTerm_FloorThreshold = 0.3;

        MMstrategy = allocateStrategy.LRU;

        Memory_Size = 4096;
        Page_Size = 256;
    }
}
