package com.example.hello.myMemory;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    private List<Integer> pages;
    public Pages(int size) {
        super(size);
        this.pages = new ArrayList<>();
    }
    public void addPage(int page_num) {
        pages.add(page_num);
    }
    public void removePage(int page_num) {
        pages.remove(Integer.valueOf(page_num));
    }
    public List<Integer> getPages() {
        return pages;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pages: ").append(pages);
        return sb.toString();
    }
}

class DemandPages extends Memory {
    private Map<Integer, Pair<Boolean, Integer>> demandPages;
    private int page_count_virtual;
    public DemandPages(int size) {
        super(size);
        this.demandPages = new HashMap<>();
        this.page_count_virtual = 0;
    }
    public void addDemandPage(int page_num_virtual) {
        demandPages.put(page_num_virtual, Pair.of(false, 0));
        page_count_virtual++;
    }
    public void addDemandPage(int page_num_virtual, int page_num_physical) {
        demandPages.put(page_num_virtual, Pair.of(true, page_num_physical));
    }
    public void swapPageIn(int page_num_virtual, int page_num_physical) {
        if (!demandPages.get(page_num_virtual).getLeft()) {
            addDemandPage(page_num_virtual, page_num_physical);
            page_count_virtual--;
        }
    }
    public void swapPageOut(int page_num_virtual) {
        if (demandPages.get(page_num_virtual).getLeft()) {
            demandPages.put(page_num_virtual, Pair.of(false, 0));
            page_count_virtual++;
        }
    }
    public List<Pair<Integer, Integer>> getAllPhysicalPages() {
        List<Pair<Integer, Integer>> physicalPages = new ArrayList<>();
        for (Entry<Integer, Pair<Boolean, Integer>> entry : demandPages.entrySet()) {
            if (entry.getValue().getLeft()) {
                physicalPages.add(Pair.of(entry.getKey(), entry.getValue().getRight()));
            }
        }
        return physicalPages;
    }
    public int getVirtualPageCount() {
        return page_count_virtual;
    }
    public int getPhysicalPage(int page_count_virtual) {
        Pair<Boolean, Integer> pair = demandPages.get(page_count_virtual);
        if (pair.getLeft()) {
            return pair.getRight();
        }
        return -1; // 表示没有对应的物理页
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Demand Pages: ").append(demandPages);
        return sb.toString();
    }
}