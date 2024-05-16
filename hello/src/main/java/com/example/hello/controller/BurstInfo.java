package com.example.hello.controller;

import com.example.hello.myInstrunction.Instruction;
import com.example.hello.myInstrunction.InstructionType;
import com.example.hello.myProcess.PCB;

import java.util.ArrayList;
import java.util.List;

public class BurstInfo {
    private Instruction instruction;
    private int logic_address;
    private int physical_address;
    private int page_number;
    private int frame_number;

    public BurstInfo(PCB p, int logic_address, myKernel kernel) {
        int index = (logic_address - p.memory_start) / kernel.getSysData().InstructionLength;
        this.logic_address = logic_address;
        if (index >= p.bursts.size()) {
            this.instruction = new Instruction(InstructionType.Exit);
        } else {
            this.instruction = new Instruction(p.bursts.get(index).getType(), p.bursts.get(index).getArguments());
            // 调用内存模块初始化物理地址,页号，帧号
            int[] res = kernel.getMm().getPhysicalMemory(p.p_id, logic_address);
            physical_address = res[0];
            page_number = res[1];
            frame_number = res[2];
        }
    }

    public static List<BurstInfo> getBurstInfos(PCB p, myKernel kernel) {
        if (p == null) return null;
        int pc = p.pc;
        List<BurstInfo> burstInfos = new ArrayList<>();
        int InstructionLength = kernel.getSysData().InstructionLength;
        while (pc < p.memory_start + p.bursts.size() * InstructionLength) {
            burstInfos.add(new BurstInfo(p, pc, kernel));
            pc += InstructionLength;
        }
        return burstInfos;
    }

    public static List<BurstInfo> getBurstInfos(int frame_number, myKernel kernel) {
        int[] node = kernel.getMm().getPidNode(frame_number);
        int pid = node[1];
        if (pid == -1) return null;
        PCB p = kernel.getPm().getPCB(pid);
        int InstructionLength = kernel.getSysData().InstructionLength;
        int page_size = kernel.getSysData().Page_Size;
        int start = node[2] * page_size;
        int process_length = p.memory_start + p.bursts.size() * InstructionLength;
        List<BurstInfo> burstInfos = new ArrayList<>();
        for (int cur = start; cur < start + page_size; cur += InstructionLength) {
            BurstInfo tmp = new BurstInfo(p, cur, kernel);
            if (cur >= process_length) {
                tmp.setFrame_number(frame_number);
                tmp.setPage_number(node[2]);
                tmp.setPhysical_address(cur - start + frame_number*page_size);
                tmp.setLogic_address(cur);
            }
            burstInfos.add(tmp);
        }
        return burstInfos;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public int getLogic_address() {
        return logic_address;
    }

    public void setLogic_address(int logic_address) {
        this.logic_address = logic_address;
    }

    public int getPhysical_address() {
        return physical_address;
    }

    public void setPhysical_address(int physical_address) {
        this.physical_address = physical_address;
    }

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }

    public int getFrame_number() {
        return frame_number;
    }

    public void setFrame_number(int frame_number) {
        this.frame_number = frame_number;
    }

}
