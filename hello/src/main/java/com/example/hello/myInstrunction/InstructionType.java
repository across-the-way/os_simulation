package com.example.hello.myInstrunction;

public enum InstructionType {
    // 内存管理
    Memory, // 参数：内存大小
    AccessMemory, // 参数：内存地址

    // 进程管理
    Priority, // 参数：进程优先级
    Calculate, // 参数：计算时间
    Fork, // 参数：子进程的入口
    Exit, // 参数：无
    Wait, // 参数：无
    CondNew, // 参数：信号量名，初始值
    CondWait, // 参数：信号量名
    CondSignal, // 参数：信号量名

    // 文件系统
    CreateDir, // 参数：父目录路径，目录名
    CreateFile, // 参数：目录路径，文件名
    DeleteDir, // 参数：目录路径
    DeleteFile, // 参数：文件路径
    WriteFile, // 参数：文件路径，写入大小，内容
    ReadFile, // 参数：文件路径，读取时间
    OpenFile, // 参数：文件路径
    CloseFile, // 参数：文件路径

    // 设备管理
    Printer, // 参数：使用时间
    Keyboard, // 参数：使用时间
    Device // 参数：使用时间
}
