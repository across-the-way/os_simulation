package com.example.hello.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.example.hello.controller.filecommand;
import com.example.hello.myProcess.PCB;
import com.example.hello.myProcess.PCB.P_STATE;

import java.util.*;

@RestController
@CrossOrigin
public class hellocontroller {

    public SysData sysData;

    public hellocontroller() {
        // 系统数据初始化
        this.sysData = new SysData();

        // 运行
        myKernel kernel = myKernel.getInstance();
        kernel.setConfig(this.sysData);
        Thread kernelThread = new Thread(kernel);
        kernelThread.start();
    }

    // @PostMapping("testfolder")
    // public String testFolder(@RequestBody filecommand filecom) {
    //     if (filecom.instruction.equals("mkdir")) {
    //         this.kernel.fileSystem.mkdir(filecom.args);
    //     } else if (filecom.instruction.equals("cd")) {
    //         boolean res = this.kernel.fileSystem.cd(filecom.args);
    //         if (res == false)
    //             return "cd not successful";

    //     } else if (filecom.instruction.equals("touch")) {
    //         this.kernel.fileSystem.touch(filecom.args);
    //     } else if (filecom.instruction.equals("rm")) {
    //         this.kernel.fileSystem.rm(filecom.option, filecom.args);
    //     }

    //     return this.sysData.fileSystem.workingPath;
    //     return null;
    // }

    // @PostMapping("testls")
    // public List<String> testFolderls() {

    //     return this.sysData.fileSystem.ls();
    // }

}
