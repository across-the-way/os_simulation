package com.example.hello.controller;

import com.example.hello.myFile.*;
import com.example.hello.myProcess.*;
import com.example.hello.myMemory.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class SysData {
    //系统所处状态
    public enum OSState {
        kernel, user
    }
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

    // 调度算法
    public scheduleStrategy CPUstrategy;

    // 内存大小
    public int memory_size;

    // 页大小
    public int page_size;

    // 分配策略
    public allocateStrategy strategy;

    public SysData() {
        sysConfig = new SysConfig();
        Osmode = OSState.kernel;

        // 单位毫秒
        SystemPulse = 100;

        InstructionLength = 4;

        Second_Queue_Threshold = 1000;

        LongTermScale = 100;

        CPUstrategy = scheduleStrategy.MLFQ;

        LongTerm_CeilThreshold = 20;
        LongTerm_FloorThreshold = 3;

        memory_size = 100;
        strategy = allocateStrategy.FirstFit;
        page_size = 64;
    }
}
