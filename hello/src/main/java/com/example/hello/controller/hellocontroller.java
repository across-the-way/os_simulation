package com.example.hello.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.hello.process.PCB;
import com.example.hello.process.PCB.P_STATE;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@RestController
@CrossOrigin
public class hellocontroller {

    public SysData sysData;

    public hellocontroller() {
        // 系统数据初始化
        this.sysData = new SysData();

        // CPU开始运行,Kernel监听中断
        new CPU(sysData).start();
        new Kernel(sysData).start();
    }

    @PostMapping("testfolder")
    public String testFolder(String instruction, String option, String args) {
        if (instruction.equals("mkdir")) {
            this.sysData.fileSystem.mkdir(args);
        } else if (instruction.equals("cd")) {
            boolean res = this.sysData.fileSystem.cd(args);
            if (res == false)
                return "cd not successful";

        } else if (instruction.equals("touch")) {
            this.sysData.fileSystem.touch(args);
        } else if (instruction.equals("rm")) {
            this.sysData.fileSystem.rm(option, args);
        }

        return this.sysData.fileSystem.workingPath;
    }

    @PostMapping("testls")
    public List<String> testFolderls() {

        return this.sysData.fileSystem.ls();
    }

}
