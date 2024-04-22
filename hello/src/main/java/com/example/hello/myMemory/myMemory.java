package com.example.hello.myMemory;

import com.example.hello.controller.InterruptType;
import com.example.hello.controller.myInterrupt;
import com.example.hello.controller.myKernel;

public class myMemory {
    private myKernel kernel;
    private allocateStrategy strategy;
    private int memory_size; // 假设内存大小为4096字节，一条指令为4字节
    private MemoryAllocator allocator;
    private int MidtermCounter;
    private int midterm_lower_bound;
    private int midterm_higher_bound;

    public myMemory(myKernel kernel) {
        this.kernel = kernel;
        this.strategy = allocateStrategy.LRU;
        this.memory_size = 32;
        this.allocator = new DemandPageAllocator(memory_size, 8, allocateStrategy.LRU);
        this.MidtermCounter = 0;
        this.midterm_lower_bound = (memory_size / 32) * 8;
        this.midterm_higher_bound = (memory_size / 32) * 24;
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
        this.MidtermCounter = 0;
        this.midterm_lower_bound = (memory_size / 64) * 1;
        this.midterm_higher_bound = (memory_size / 32) * 31;
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
                this.allocator = new DemandPageAllocator(memory_size, page_size, strategy);
                break;
            default:
                System.out.println("Please use correct construction method");
                break;
        }
        this.MidtermCounter = 0;
        this.midterm_lower_bound = (memory_size / 64) * 1;
        this.midterm_higher_bound = (memory_size / 32) * 31;
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

    public void swapIn(int pid, int pc) {
        if (strategy == allocateStrategy.LRU || strategy == allocateStrategy.FIFO) {
            ((DemandPageAllocator) allocator).swapIn(pid, pc);
        }
    }

    public void swapOut(int pid) {
        if (strategy == allocateStrategy.LRU || strategy == allocateStrategy.FIFO) {
            ((DemandPageAllocator) allocator).swapOut(pid);
        }
    }

    // 检查pid当前pc是否会触发pagefault
    public boolean isPageFault(int pid, int pc) {
        if (strategy != allocateStrategy.LRU && strategy != allocateStrategy.FIFO)
            return false;
        return ((DemandPageAllocator) allocator).isPageFault(pid, pc);
    }

    public void update() {
        // 中期调度
        // MidtermCounter = MidtermCounter % this.kernel.getSysData().MidTermScale + 1;
        // if(MidtermCounter == this.kernel.getSysData().MidTermscale) {
        // //检查当前内存负载情况，
        // //如果太大了，发送SWAPPEDOUT中断
        // //如果太小了，发送SWAPPEDIN中断
        // }
        MidtermCounter = (MidtermCounter + 1) % 20;
        if (MidtermCounter == 0) {
            if (isUpper()) {
                this.sendInterrupt(InterruptType.SwappedOut);
            }
            if (isLower()) {
                this.sendInterrupt(InterruptType.SwappedIn);
            }
        }
    }

    public boolean isUpper() {
        return (memory_size - allocator.free_memory_size) >= midterm_higher_bound;
    }

    public boolean isLower() {
        return (memory_size - allocator.free_memory_size) <= midterm_lower_bound;
    }

    // 向kernel中央模块发送中断请求
    private void sendInterrupt(InterruptType interrupt, Object... objs) {
        kernel.receiveInterrupt(new myInterrupt(interrupt, objs));
    }

    public void page(int pid, int pc) {
        // 不需要实现了，但还是需要调用，消耗一个时钟中断
        System.out.println("Pid: " + pid + " PC: " + pc + " page fault");
        return;
    }

    public MemoryStatus getMemoryStatus() {
        MemoryStatus memoryStatus = new MemoryStatus(strategy.toString());

        switch (strategy) {
            case FirstFit:
            case NextFit:
            case BestFit:
            case WorstFit:
                memoryStatus.addDetail("free_blocks", ((ContiguousAllocator) allocator).free_blocks);
                memoryStatus.addDetail("used_memory", ((ContiguousAllocator) allocator).used_memory);
                break;
            case Page:
                memoryStatus.addDetail("page_table", ((PageAllocator) allocator).page_table);
                break;
            case LRU:
            case FIFO:
                memoryStatus.addDetail("free_pages", ((DemandPageAllocator) allocator).page_table.free_pages);
                memoryStatus.addDetail("lru_cache", ((DemandPageAllocator) allocator).page_table.cache.cache);
                memoryStatus.addDetail("used_pages", ((DemandPageAllocator) allocator).page_table.used_pages);
                memoryStatus.addDetail("swapped_page_count", ((DemandPageAllocator) allocator).page_table.swap_partition.page_count);
                memoryStatus.addDetail("swapped_used_count", ((DemandPageAllocator) allocator).page_table.swap_partition.used_count);
                break;
        }
        return memoryStatus;
    }
}
