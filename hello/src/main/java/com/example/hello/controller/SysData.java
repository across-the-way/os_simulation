package com.example.hello.controller;

import com.example.hello.process.*;
import com.example.hello.process.Process;
import com.example.hello.filesystem.*;

import java.util.*;
import java.util.concurrent.Semaphore;

public class SysData {
    public enum OSState {
        kernel, user
    }

    public FileSystem fileSystem;
    public Process ProcessController;
    SysConfig sysConfig;
    Semaphore sem_os;
    OSState Osmode;

    public SysData() {
        sysConfig = new SysConfig();
        fileSystem = new FileSystem();
        ProcessController = new Process();
        sem_os = new Semaphore(1);
        Osmode = OSState.kernel;
    }
}
