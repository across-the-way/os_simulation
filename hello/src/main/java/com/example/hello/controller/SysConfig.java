package com.example.hello.controller;

import java.util.*;

public class SysConfig {
    public enum Dispatch {
        FCFS, SJF, Priority_NonPreemptive, Priority_Preemptive, HRRN, Multilevel
    }

    // CPU调度算法
    Dispatch policy;
    // 多级队列调度第一级的时间片
    int Multilevel_TimeSlice;

    // 系统主频
    int Main_Frequency;

    // 系统中所有设备的数目
    int DeviceNumber;

    // 系统可用资源InitAvailable
    Map<String, Integer> InitAvailable;
}
