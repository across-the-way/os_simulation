package com.example.hello.myFile;

public class Block {
    public char[] data;
    public Block() {
        data = new char[4];//一个块定义四个字节
    }
    @Override
    public String toString() {
        return new String(data);
    }

}
