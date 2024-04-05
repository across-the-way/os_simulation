package com.example.hello.controller;

import java.util.*;

public class SysConfig {
    public enum Dispatch {
        FCFS, SJF, Priority_NonPreemptive, Priority_Preemptive, HRRN, Multilevel
    }
    Dispatch policy;    // CPU调度算法
    int Multilevel_TimeSlice;   // 多级队列调度第一级的时间片
    int Main_Frequency;     // 系统主频
    int DeviceNumber;   // 系统中所有设备的数目
    Map<String, Integer> InitAvailable;        // 系统可用资源InitAvailable
}
