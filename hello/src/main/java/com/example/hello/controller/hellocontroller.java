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

    public Kernel kernel;
    public SysConfig config;

    public hellocontroller() {
        this.kernel = new Kernel();
        this.config = new SysConfig();
        // new CPU(kernel, config).start();
    }

    @GetMapping("/hello")
    public String hello() {
        for (int i = 0; i < 10; i++) {
            PCB p = new PCB();
            p.setMemory_allocate(30);
            p.setMemory_start(0);
            p.setP_id(1000 + i);
            p.setPriority(3);

            p.setState(P_STATE.ready);

            this.kernel.allProcess.ProcessList.add(p);
        }
        return "hello";
    }

    @GetMapping("get")
    public List<PCB> getMethodName() {
        return this.kernel.allProcess.ProcessList;
    }

    @PostMapping("testfolder")
    public String testFolder(String instruction, String option, String args) {
        if (instruction.equals("mkdir")) {
            this.kernel.fileSystem.mkdir(args);
        } else if (instruction.equals("cd")) {
            boolean res = this.kernel.fileSystem.cd(args);
            if (res == false)
                return "cd not successful";

        } else if (instruction.equals("touch")) {
            this.kernel.fileSystem.touch(args);
        } else if (instruction.equals("rm")) {
            this.kernel.fileSystem.rm(option, args);
        }

        return this.kernel.fileSystem.workingPath;
    }

    @PostMapping("testls")
    public List<String> testFolderls() {

        return this.kernel.fileSystem.ls();
    }

}
