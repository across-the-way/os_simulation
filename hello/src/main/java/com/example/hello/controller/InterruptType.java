package com.example.hello.controller;

public enum InterruptType {
    TimerInterrupt,
    PageFault,
    TimeSilceRunOut,
    IOFinish,
    FileFinish,
    TerminalCall,
    SystemCall,
    SwappedIn,
    SwappedOut,
    StopSystem,
    StartSystem,
    SinglePause,
    Exit,
}
