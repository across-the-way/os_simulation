package com.example.hello.controller;

import com.example.hello.SysConfig.SysData;
import com.example.hello.myDevice.Device;
import com.example.hello.myFile.Inode;
import com.example.hello.myInstrunction.Instruction;
import com.example.hello.myInterrupt.InterruptType;
import com.example.hello.myInterrupt.SystemCallType;
import com.example.hello.myInterrupt.myInterrupt;
import com.example.hello.myMemory.MemoryStatus;
import com.example.hello.myMemory.allocateStrategy;
import com.example.hello.myProcess.PCB;
import com.example.hello.myProcess.ProcessQueue;
import com.example.hello.myProcess.scheduleStrategy;
import com.example.hello.myTerminal.TerminalCallType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        kernel.start();
    }

    public void initialize(String configFilePath) {
        // 系统数据初始化
        this.sysData = new SysData(configFilePath);

        // 运行
        kernel = myKernel.getInstance();
        kernel.setConfig(this.sysData);
        kernel.start();
    }

    @GetMapping("/restart")
    public Boolean restartSystem() {
        kernel.stop();
        kernel.start();
        return true;
    }

    @GetMapping("/resetMemoryStrategy")
    public Boolean rebootSystem(@RequestParam allocateStrategy strategy) {
        kernel.stop();
        this.sysData.MMstrategy = strategy;
        kernel.start();
        return true;
    }

    @GetMapping("/resetCPUStrategy")
    public Boolean rebootSystem(@RequestParam scheduleStrategy strategy) {
        kernel.stop();
        this.sysData.CPUstrategy = strategy;
        kernel.start();
        return true;
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

    @PostMapping("/api/process")
    public Instruction[] CreateProcess(@RequestBody Instruction[] instructions) {
        if ((instructions.length - 2) * kernel.getSysData().InstructionLength <= (int)instructions[0].getArguments()[0]) {
            // System.out.println(instructions);
            this.kernel
                    .receiveInterrupt(
                            new myInterrupt(InterruptType.SystemCall, SystemCallType.ProcessNew, instructions));
        }
        return instructions;
    }

    @GetMapping("/api/process/status")
    public List<PCB> getProcessStatus() {
        return this.kernel.getPm().getPCBs();
    }

    @GetMapping("/api/process/queue")
    public ProcessQueue getProcessQueue() {
        return this.kernel.getPm().getQueue();
    }

    @PostMapping("/api/filesystem")
    public List<Inode> getFilesystem(@RequestBody Location location) {
        location.setLocation(location.getLocation().substring(1));
        return this.kernel.getFs().filelist(location.getLocation());
    }

    @GetMapping("/api/device")
    public List<Device> getDevice() {
        return this.kernel.getIo().get();
    }

    @PostMapping("/device/add")
    public String addDevice(@RequestBody String name) {
        this.kernel.receiveInterrupt(new myInterrupt(InterruptType.MountDevice, name));
        return "hello! " + name;
    }

    @GetMapping("/device/delete")
    public boolean deleteDevice(@RequestParam int id) {
        Device device = this.kernel.getIo().getDeviceByID(id);
        // 没有设备，返回删除设备失败
        if (device == null) {
            return false;
        } else {
            // 设备忙，返回删除设备失败
            if (device.isBusy()) {
                return false;
            }
            // 设备空闲，释放设备占用资源，释放后端设备对象
            else {
                this.kernel.getIo().deleteDevice(device);
                this.kernel.getSysData().ReleaseDevice(device.getType());
                return true;
            }
        }
    }

    @PostMapping("/api/terminal")
    public String CreateProcess(@RequestBody Object[] instruction) throws InterruptedException {
        TerminalCallType type;
        try {
            type = TerminalCallType.valueOf((String) instruction[0]);
        } catch (java.lang.IllegalArgumentException e) {
            type = TerminalCallType.err;
        }
        instruction = Arrays.copyOfRange(instruction, 1, instruction.length);

        this.kernel.terminal_mutex.acquire();

        this.kernel.terminal_update = false;
        this.kernel
                .receiveInterrupt(new myInterrupt(InterruptType.TerminalCall, type, instruction));
        Thread.sleep(this.kernel.getSysData().SystemPulse + 50);
        while (!this.kernel.terminal_update)
            ;
        this.kernel.terminal_update = false;
        String msg = this.kernel.terminal_message;
        this.kernel.terminal_message = null;

        this.kernel.terminal_mutex.release();
        return msg;
    }

    @GetMapping("/api/memory")
    public MemoryStatus getMemoryStatus() {
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

    @GetMapping("/BurstInfo")
    public List<BurstInfo> getBurstInfo(@RequestParam int pid) {
        if (kernel.getPm().getPCB(pid) == null) return null;
        return BurstInfo.getBurstInfos(kernel.getPm().getPCB(pid), kernel);
    }

    @GetMapping("/FrameInfo")
    public List<BurstInfo> getFrameInfo(@RequestParam int frame_num) {
        return BurstInfo.getBurstInfos(frame_num, kernel);
    }

    @GetMapping("/ProcessResource")
    public Map<String, Integer> getProcessResource(@RequestParam int pid) {
        return kernel.getPm().getResourceMap(pid);
    }
}
