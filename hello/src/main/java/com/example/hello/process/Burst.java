package com.example.hello.process;

public class Burst {
    public String type;
    public int time;

    public boolean isCPUburst() {
        return this.type.equals("cpu");
    }
}
