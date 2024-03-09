package com.example.hello.controller;

import com.example.hello.process.*;
import com.example.hello.instrunction.*;
import java.util.*;

public class Kernel {
    public List<PCB> p_list;
    public ProcessQueue queue;

    public Kernel(){
        p_list = new ArrayList<>();
        queue =new ProcessQueue();
    }

    public List<PCB> getP_list() {
        return p_list;
    }

    public void setP_list(List<PCB> p_list) {
        this.p_list = p_list;
    }

    public void createProcess(List<Instruction> instructions){
        
    }
}
