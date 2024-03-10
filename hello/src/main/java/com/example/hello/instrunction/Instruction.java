package com.example.hello.instrunction;

public class Instruction {
    public enum Type {
        C, K, P, R, W, M, Y, S, Q
    }

    public Type type;
    public Args args;
    public int CPI;
    public Instruction() {
 
    }
}
