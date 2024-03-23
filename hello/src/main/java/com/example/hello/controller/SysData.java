package com.example.hello.controller;

import com.example.hello.myFile.*;
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

    public SysData() {
        sysConfig = new SysConfig();
        // fileSystem = new FileSystem();
        // ProcessController = new Process();
        Osmode = OSState.kernel;
    }
}
