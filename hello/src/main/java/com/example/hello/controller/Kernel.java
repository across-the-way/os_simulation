package com.example.hello.controller;

import com.example.hello.process.*;
import com.example.hello.process.Process;
import com.example.hello.filesystem.*;

import java.util.*;

public class Kernel {
    public FileSystem fileSystem;
    public Process ProcessController;
    SysConfig sysConfig;

    public Kernel() {
        sysConfig = new SysConfig();
        fileSystem = new FileSystem();
        ProcessController = new Process();
    }
}
