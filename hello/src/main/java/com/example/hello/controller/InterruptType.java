package com.example.hello.controller;

//中断类型
public enum InterruptType {
    TimerInterrupt,
    PageFault,
    TimeSilceRunOut,
    IOFinish,
    FileFinish,
    GUICall,
    SystemCall,
    Exit,
}
