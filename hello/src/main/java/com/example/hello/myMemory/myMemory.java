package com.example.hello.myMemory;

import java.util.ArrayList;
import java.util.List;

import com.example.hello.controller.myKernel;

public class myMemory {
    private myKernel kernel;
    private allocateStrategy strategy;
    private int memory_size; // 假设内存大小为4096字节，一条指令为4字节
    private MemoryAllocator allocator;
    private int MidtermCounter;

    public myMemory(myKernel kernel) {
        this.kernel = kernel;
        this.strategy = allocateStrategy.LRU;
        this.memory_size = 4096;
        this.allocator = new DemandPageAllocator(memory_size, 32);
    }

    public myMemory(myKernel kernel, allocateStrategy strategy, int memory_size) {
        this.kernel = kernel;
        this.strategy = strategy;
        this.memory_size = memory_size;
        switch (strategy) {
            case FirstFit:
                this.allocator = new ContiguousAllocator(memory_size, allocateStrategy.FirstFit);
                break;
            case NextFit:
                this.allocator = new ContiguousAllocator(memory_size, allocateStrategy.NextFit);
                break;
            case BestFit:
                this.allocator = new ContiguousAllocator(memory_size, allocateStrategy.BestFit);
                break;
            case WorstFit:
                this.allocator = new ContiguousAllocator(memory_size, allocateStrategy.WorstFit);
                break;
            default:
                System.out.println("Please use correct construction method");
                break;
        }
    }

    public myMemory(myKernel kernel, allocateStrategy strategy, int memory_size, int page_size) {
        this.kernel = kernel;
        this.strategy = strategy;
        this.memory_size = memory_size;
        switch (strategy) {
            case Page:
                this.allocator = new PageAllocator(memory_size, page_size);
                break;
            case LRU:
            case FIFO:
                this.allocator = new DemandPageAllocator(memory_size, page_size);
                break;
            default:
                System.out.println("Please use correct construction method");
                break;
        }
    }

    // 按照分配方法分配内存
    public boolean allocate(int pid, int size) {
        // 寻找内存中满足要求空闲空间
        Memory memory = allocator.findFreeSpace(size);
        // 若能找到，
        if (memory != null) {
            allocator.allocate(pid, memory);
            return true;
        }
        return false;
    }

    public void release(int pid) {
        allocator.release(pid);
    }

    // 检查pid当前pc是否会触发pagefault
    public boolean isPageFault(int pid, int pc) {
        if (strategy != allocateStrategy.LRU || strategy != allocateStrategy.FIFO)
            return false;
        return ((DemandPageAllocator) allocator).isPageFault(pid, pc);
    }

    public void update() {
        // 暂时不知道干什么
        MidtermCounter = MidtermCounter % this.kernel.getSysData().MidTermScale + 1;

        if (MidtermCounter == this.kernel.getSysData().MidTermScale) {
            // 检查当前内存负载情况,
                // 如果太大了，发送SWAPPEDOUT中断
                // 如果太小了，发送SWAPPEDIN中断
        }
    }

    public void page(int pid, int pc) {
        // 不需要实现了，但还是需要调用，消耗一个时钟中断
        return;
    }

    public List<Object> getMemoryStatus() {
        List<Object> list = new ArrayList<>();
        list.add(strategy.toString());
        switch (strategy) {
            case FirstFit:
            case NextFit:
            case BestFit:
            case WorstFit:
                list.add(((ContiguousAllocator) allocator).free_blocks);
                list.add(((ContiguousAllocator) allocator).used_memory);
                break;
            case Page:
                list.add(((PageAllocator) allocator).page_table);
                break;
            case LRU:
            case FIFO:
                list.add(((DemandPageAllocator) allocator).page_table);
                break;
        }
        return list;
    }
}
