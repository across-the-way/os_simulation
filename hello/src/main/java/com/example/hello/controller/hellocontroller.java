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

    public hellocontroller() {
        this.kernel = new Kernel();
    }

    @GetMapping("/hello")
    public String hello() {
        for (int i = 0; i < 10; i++) {
            PCB p = new PCB();
            p.setMemory_allocate(30);
            p.setMemory_start(0);
            p.setNeed(new LinkedHashMap<>());
            p.setP_id(1000 + i);
            p.setPriority(3);
            p.setResourceMap(new HashMap<>());

            p.setState(P_STATE.ready);

            p.need.put("printer", 30);
            this.kernel.p_list.add(p);
        }
        return "hello";
    }

    @GetMapping("get")
    public List<PCB> getMethodName() {
        return this.kernel.p_list;
    }

    @GetMapping("getqueue")
    public List<PCB> getqueue() {
        return this.kernel.queue.Waiting_Queue;
    }

    @PostMapping("/testtaotao")
    public int postMethodName1(int a, int b) {
        // TODO: process POST request

        return a + b;
    }

    @PostMapping("/demo")
    public String postMethodName(String username, String password) {
        System.out.println("hello" + username);
        return "helloworld";
    }

}
