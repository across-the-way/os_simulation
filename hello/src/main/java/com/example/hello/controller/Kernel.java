package com.example.hello.controller;

import com.example.hello.process.*;
import com.example.hello.process.Process;
import com.example.hello.instrunction.*;
import com.example.hello.filesystem.*;

import java.util.*;

public class Kernel {
    public ProcessQueue queue;
    public FileSystem fileSystem;
    public Process allProcess;

    public Kernel() {
        queue = new ProcessQueue();
        fileSystem = new FileSystem();
        allProcess = new Process();
    }
}
