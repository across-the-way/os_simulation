package com.example.hello.myMemory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Memory {
    protected int size;
    public Memory(int size) {
        this.size = size;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
}

class Block extends Memory {
    private int start;
    public Block(int start, int size) {
        super(size);
        this.start = start; // 起始地址
    }
    public void reset(int start, int size) {
        setStart(start);
        setSize(size);
    }
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getEnd() {
        return start + size - 1; // 结束地址
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block: ").append(getStart()).append(" - ").append(getEnd());
        return sb.toString();
    }
}

class Pages extends Memory {
    private Set<Integer> pages;
    public Pages(int size) {
        super(size);
        this.pages = new HashSet<>();
    }
    public void addPage(int page_num) {
        pages.add(page_num);
    }
    public void removePage(int page_num) {
        pages.remove(Integer.valueOf(page_num));
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pages: ").append(pages);
        return sb.toString();
    }
}