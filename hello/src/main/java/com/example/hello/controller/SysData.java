package com.example.hello.controller;

import com.example.hello.myFile.*;
import com.example.hello.myProcess.*;

import java.util.*;
import java.util.concurrent.Semaphore;

public class SysData {
    //系统所处状态
    public enum OSState {
        kernel, user
    }
    SysConfig sysConfig;
    OSState Osmode;

    public SysData() {
        sysConfig = new SysConfig();
        Osmode = OSState.kernel;
    }
}
