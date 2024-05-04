package com.example.hello.controller;

import com.example.hello.myInstrunction.Instruction;
import com.example.hello.myProcess.PCB;

import java.util.ArrayList;
import java.util.List;

public class BurstInfo {
    private Instruction instruction;
    private int logic_address;
    private int physical_address;
    private int page_number;
    private int frame_number;

    public BurstInfo(PCB p, int logic_address, int InstructionLength) {
        int index = (logic_address - p.memory_start) / InstructionLength;
        this.instruction = new Instruction(p.bursts.get(index).getType(), p.bursts.get(index).getArguments());
        this.logic_address = logic_address;

        // 调用内存模块初始化物理地址,页号，帧号
    }

    public static List<BurstInfo> getBurstInfos(PCB p, int InstructionLength) {
        int pc = p.pc;
        List<BurstInfo> burstInfos = new ArrayList<>();
        while (pc < p.memory_start + p.bursts.size() * InstructionLength) {
            burstInfos.add(new BurstInfo(p, pc, InstructionLength));
            pc += InstructionLength;
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
