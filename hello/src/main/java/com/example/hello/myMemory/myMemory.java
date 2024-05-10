package com.example.hello.myMemory;

import com.example.hello.controller.myKernel;
import com.example.hello.myInterrupt.InterruptType;
import com.example.hello.myInterrupt.myInterrupt;

public class myMemory {
    private final myKernel kernel;
    private final allocateStrategy strategy;
    private final int memory_size; // 假设内存大小为4096字节，一条指令为4字节
    private MemoryAllocator allocator;
    private int MidtermCounter;
    private final int midterm_lower_bound;
    private final int midterm_higher_bound;

    public myMemory(myKernel kernel) {
        this.kernel = kernel;
        this.strategy = kernel.getSysData().MMstrategy;
        this.memory_size = kernel.getSysData().Memory_Size;
        int page_size = kernel.getSysData().Page_Size;
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
        this.midterm_lower_bound = (int) (memory_size * kernel.getSysData().MidTerm_FloorThreshold);
        this.midterm_higher_bound = (int) (memory_size * kernel.getSysData().MidTerm_CeilThreshold);
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

    
    public void accessMemory(int pid, int virtual_address) {
        if (strategy != allocateStrategy.LRU && strategy != allocateStrategy.FIFO)
            return;
        if (virtual_address >= ((DemandPageAllocator) allocator).page_table.used_pages.get(pid).size) {
            System.out.println(virtual_address + " is invalid memory address for process " + pid);
            return;
        }
        ((DemandPageAllocator) allocator).isPageFault(pid, virtual_address);
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
        System.out.println("free memory size : " + allocator.free_memory_size);
        // 中期调度
        if (strategy != allocateStrategy.LRU && strategy != allocateStrategy.FIFO)
            return;
        MidtermCounter = (MidtermCounter + 1) % kernel.getSysData().MidTermScale;
        if (MidtermCounter == 0) {
            //检查当前内存负载情况，
            if (isUpper()) {
                //如果太大了，发送SWAPPEDOUT中断
                this.sendInterrupt(InterruptType.SwappedOut);
            }
            if (isLower()) {
                //如果太小了，发送SWAPPEDIN中断
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
    }

    public int[] getPhysicalMemory(int pid, int logical_address) {
        int physical_address = -1, frame_number = -1;
        int page_number = -1, page_offset = -1;
        switch (strategy) {
            case FirstFit:
            case NextFit:
            case BestFit:
            case WorstFit:
                physical_address = ((ContiguousAllocator) allocator).used_memory.get(pid).getStart() + logical_address;
                break;
            case Page:
                page_number = logical_address / kernel.getSysData().Page_Size;
                page_offset = logical_address % kernel.getSysData().Page_Size;
                frame_number = ((PageAllocator) allocator).page_table.used_pages.get(pid).getPages().get(page_number);
                physical_address = frame_number * kernel.getSysData().Page_Size + page_offset;
                break;
            case LRU:
            case FIFO:
                page_number = logical_address / kernel.getSysData().Page_Size;
                page_offset = logical_address % kernel.getSysData().Page_Size;
                frame_number = ((DemandPageAllocator) allocator).page_table.getPhysicalPage(pid, page_number);
                if (frame_number != -1) {
                    physical_address = frame_number * kernel.getSysData().Page_Size + page_offset;
                }
                break;
        }
        return new int[]{physical_address, page_number, frame_number};
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
                memoryStatus.addDetail("free_pages", ((PageAllocator) allocator).page_table.free_pages.toString());
                memoryStatus.addDetail("used_pages", ((PageAllocator) allocator).page_table.used_pages);
                memoryStatus.addDetail("page_size", kernel.getSysData().Page_Size);
                break;
            case LRU:
            case FIFO:
                memoryStatus.addDetail("free_pages", ((DemandPageAllocator) allocator).page_table.free_pages.toString());
                memoryStatus.addDetail("lru_cache", ((DemandPageAllocator) allocator).page_table.cache.cache);
                memoryStatus.addDetail("used_pages", ((DemandPageAllocator) allocator).page_table.used_pages.entrySet());
                memoryStatus.addDetail("swapped_page_count", ((DemandPageAllocator) allocator).page_table.swap_partition.page_count);
                memoryStatus.addDetail("swapped_used_count", ((DemandPageAllocator) allocator).page_table.swap_partition.used_count);
                memoryStatus.addDetail("pages", ((DemandPageAllocator) allocator).pages);
                memoryStatus.addDetail("faults", ((DemandPageAllocator) allocator).faults);
                memoryStatus.addDetail("page_size", kernel.getSysData().Page_Size);
                break;
        }
        return memoryStatus;
    }
}
