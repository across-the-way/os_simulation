package com.example.hello.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
public class hellocontroller {

    @GetMapping("/hello")
    public String hello() {
        return "hello world!";
    }
}
