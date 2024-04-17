package com.example.hello.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.example.hello.myInstrunction.Instruction;
import com.example.hello.myProcess.PCB;
import com.example.hello.myProcess.PCB.P_STATE;
import com.example.hello.myDevice.*;
import com.example.hello.myFile.*;
import java.util.*;

@RestController
@CrossOrigin
public class hellocontroller {

    public SysData sysData;
    public myKernel kernel;

    public hellocontroller() {
        // 系统数据初始化
        this.sysData = new SysData();

        // 运行
        kernel = myKernel.getInstance();
        kernel.setConfig(this.sysData);
        Thread kernelThread = new Thread(kernel);
        kernelThread.start();
    }

    // @PostMapping("testfolder")
    // public String testFolder(@RequestBody filecommand filecom) {
    // if (filecom.instruction.equals("mkdir")) {
    // this.kernel.fileSystem.mkdir(filecom.args);
    // } else if (filecom.instruction.equals("cd")) {
    // boolean res = this.kernel.fileSystem.cd(filecom.args);
    // if (res == false)
    // return "cd not successful";

    // } else if (filecom.instruction.equals("touch")) {
    // this.kernel.fileSystem.touch(filecom.args);
    // } else if (filecom.instruction.equals("rm")) {
    // this.kernel.fileSystem.rm(filecom.option, filecom.args);
    // }

    // return this.sysData.fileSystem.workingPath;
    // return null;
    // }

    // @PostMapping("testls")
    // public List<String> testFolderls() {

    // return this.sysData.fileSystem.ls();
    // }

    @PostMapping("/process")
    public Instruction[] CreateProcess(@RequestBody Instruction[] instructions) {
        // System.out.println(instructions);
        this.kernel
                .receiveInterrupt(new myInterrupt(InterruptType.SystemCall, SystemCallType.ProcessNew, instructions));
        return instructions;
    }

    @GetMapping("/process/status")
    public List<PCB> getProcessStatus() {
        return this.kernel.getPm().getPCBs();
    }

    @PostMapping("/filesystem")
    public List<Inode> getFilesystem(@RequestBody Location location) {
        location.setLocation(location.getLocation().substring(1));
        return this.kernel.getFs().filelist(location.getLocation());
    }// 把这里的string改成List<Inode>应该就可以用了

    @PostMapping("/device")
    public List<Device> getDevice() {
        return this.kernel.getIo().get();
    }

    @PostMapping("/terminal")
    public String CreateProcess(@RequestBody Object[] instruction) {
        TerminalCallType type;
        try {
            type = TerminalCallType.valueOf((String) instruction[0]);
        } catch (java.lang.IllegalArgumentException e) {
            type = TerminalCallType.err;
        }
        instruction = Arrays.copyOfRange(instruction, 1, instruction.length);
        this.kernel
                .receiveInterrupt(new myInterrupt(InterruptType.TerminalCall, type, instruction));
        while (!this.kernel.terminal_update)
            ;
        this.kernel.terminal_update = false;
        String msg = this.kernel.terminal_message;
        this.kernel.terminal_message = null;
        return msg;
    }

    @GetMapping("/memory")
    public List<Object> getMemoryStatus() {
        return this.kernel.getMm().getMemoryStatus();
    }

    @GetMapping("/stop")
    public String pause() {
        this.kernel.receiveInterrupt(new myInterrupt(InterruptType.StopSystem));
        return "stop success";
    }

    @GetMapping("/start")
    public String start() {
        this.kernel.receiveInterrupt(new myInterrupt(InterruptType.StartSystem));
        return "start success";
    }

    @GetMapping("/singlepause")
    public String singlepause() {
        this.kernel.receiveInterrupt(new myInterrupt(InterruptType.SinglePause));
        return "singlepause success";
    }
    

}
