package com.example.hello.myMemory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.hello.myMemory.Memory;
import com.example.hello.myMemory.Block;
import com.example.hello.myMemory.Pages;

public abstract class MemoryAllocator {
    protected int total_memory_size;
    protected int free_memory_size;
    public MemoryAllocator(int total_memory_size) {
        this.total_memory_size = total_memory_size;
        this.free_memory_size = total_memory_size;
    }
    abstract Memory findFreeSpace(int size);
    abstract void allocate(int pid, Memory memory);
    abstract void release(int pid);
    abstract void show();
}

class ContiguousAllocator extends MemoryAllocator {
    private List<Block> free_blocks;
    protected Map<Integer, Block> used_memory; // 进程-内存表
    enum AllocationPolicy { FIRST_FIT, NEXT_FIT, BEST_FIT, WORST_FIT }
    private AllocationPolicy allocation_policy = AllocationPolicy.FIRST_FIT;
    private int last_index = 0;//用于NEXT_FIT,记录上一次在free_blocks中查找结束的位置

    public ContiguousAllocator(int total_memory_size) {
        super(total_memory_size);
        free_blocks = new ArrayList<>();
        free_blocks.add(new Block(0, total_memory_size));
        used_memory = new HashMap<Integer, Block>();
    }

    @Override
    Block findFreeSpace(int size) {
        switch (allocation_policy) {
            case FIRST_FIT: return firstFit(size);
            case NEXT_FIT: return nextFit(size);
            case BEST_FIT: return bestFit(size);
            case WORST_FIT: return worstFit(size);
            default: break;
        }
        return null;
    }

    @Override
    void allocate(int pid, Memory memory) {
        used_memory.put(pid, (Block) memory);
    }

    @Override
    void release(int pid) {
        List<Block> new_free_blocks = new ArrayList<>();
        Block block = used_memory.get(pid);
        int start = block.getStart();
        int end = block.getEnd();
        boolean placed = false;

        for (int i = 0; i < free_blocks.size(); i++) {
            Block free_block = free_blocks.get(i);
            if (free_block.getStart() > end + 1) {
                if (!placed) {
                    new_free_blocks.add(new Block(start, end - start + 1));
                    placed = true;
                }
                new_free_blocks.add(free_block);
            } else if (free_block.getEnd() < start - 1) {
                new_free_blocks.add(free_block);
            } else {
                start = Math.min(start, free_block.getStart());
                end = Math.max(end, free_block.getEnd());
            }
        }
        if (!placed) {
            new_free_blocks.add(new Block(start, end - start + 1));
        }
        used_memory.remove(pid);
        free_blocks = new_free_blocks;
    }

    private Block firstFit(int size) {
        Block free_block = null;
        for (int i = 0; i < free_blocks.size(); i++) {
            Block block = free_blocks.get(i);
            if (block.getSize() >= size) {
                free_block = new Block(block.getStart(), size);
                if (block.getSize() == size) { // 恰好被分配完则删除
                    free_blocks.remove(i);
                } else {
                    free_blocks.get(i).reset(block.getStart() + size, block.getSize() - size);
                }
                last_index = i; //用于配合NEXT_FIT
                break;
            }
        }
        return free_block;
    }
    
    private Block nextFit(int size) {
        Block free_block = null;
        while (last_index >= free_blocks.size()) { //防止last_index越界
            last_index--;
        }
        for (int i = last_index; ; i = (i + 1) % free_blocks.size()) {
            Block block = free_blocks.get(i);
            if (block.getSize() >= size) {
                free_block = new Block(block.getStart(), size);
                if (block.getSize() == size) { // 恰好被分配完则删除
                    free_blocks.remove(i);
                } else {
                    free_blocks.get(i).reset(block.getStart() + size, block.getSize() - size);
                }
                last_index = i; //用于配合NEXT_FIT
                break;
            }
            if (i == (last_index - 1) % free_blocks.size()) {
                break; //已经遍历全部free_block则退出
            }
        }
        return free_block;
    }
    
    private Block bestFit(int size) {
        Block free_block = null;
        Block best_block = null;
        int best_index = 0;
        int best_size_diff = total_memory_size + 1;
        for (int i = 0; i < free_blocks.size(); i++) {
            Block block = free_blocks.get(i);
            int size_diff = block.getSize() - size;
            if (size_diff >= 0) {
                if (size_diff < best_size_diff) {
                    best_index = i;
                    best_block = block;
                    best_size_diff = size_diff;
                }
            }
        }
        if (best_block != null) {
            free_block = new Block(best_block.getStart(), size);
            if (best_block.getSize() == size) { //恰好被分配完则删除
                free_blocks.remove(best_index);
            } else {
                best_block.reset(best_block.getStart() + size, best_block.getSize() - size);
            }
            last_index = best_index;
        }
        return free_block;
    }
    
    private Block worstFit(int size) {
        Block free_block = null;
        Block worst_block = null;
        int worst_index = 0;
        int max_size = -1;
        for (int i = 0; i < free_blocks.size(); i++) {
            Block block = free_blocks.get(i);
            if (block.getSize() > max_size) {
                worst_index = i;
                worst_block = block;
                max_size = worst_block.getSize();
            }
        }
        if (worst_block != null) {
            free_block = new Block(worst_block.getStart(), size);
            if (worst_block.getSize() == size) { //恰好分配完则删除
                free_blocks.remove(worst_index);
            } else {
                worst_block.reset(worst_block.getStart() + size, worst_block.getSize() - size);
            }
            last_index = worst_index;
        }
        return free_block;
    }
    private void show() {
        System.out.println("Free Blocks:");
        for (Block block : free_blocks) {
            System.out.print(block + " ");
        }
        System.out.println("\nUsed Memory:");
        for (Map.Entry<Integer, Block> entry : used_memory.entrySet()) {
            System.out.println("PID: " + entry.getKey() + ", Memory: " + entry.getValue());
        }
    }
    public static void main(String[] args) {
        ContiguousAllocator allocator = new ContiguousAllocator(100);
        allocator.show();
        allocator.allocate(0, allocator.findFreeSpace(10));
        allocator.show();
        allocator.allocate(1, allocator.findFreeSpace(20));
        allocator.show();
        allocator.allocate(2, allocator.findFreeSpace(30));
        allocator.show();
        allocator.release(1);
        allocator.show();
        allocator.release(2);
        allocator.show();
    }
}

class PageAllocator extends MemoryAllocator{
    class PageTable {
        Map<Integer, Pages> used_pages;
        BitSet free_pages;
        public PageTable(int page_count) {
            used_pages = new HashMap<>();
            free_pages = new BitSet(page_count);
            free_pages.set(0, page_count, true);
        }
        public Pages findFreePages(int page_num) {
            Pages pages = new Pages(page_num * page_size);
            int cnt = 0;
            for (int i = 0; i < free_pages.size(); i++) {
                if (free_pages.get(i)) {
                    pages.addPage(i);
                    cnt++;
                    if (cnt == page_num) {
                        return pages;
                    }
                }
            }
            return null;
        }
        public void allocate(int pid, Pages pages) {
            used_pages.put(pid, pages);
            for (int page : pages.getPages()) {
                free_pages.clear(page);
            }
        }
        public void release(int pid) {
            Pages pages = used_pages.remove(pid);
            if (pages != null) {
                for (int page : pages.getPages()) {
                    free_pages.set(page);
                }
            }
        }
        @Override
        public String toString() {
            return "PageTable: " + used_pages.toString()
                    + "\nFree pages: " + free_pages.toString();
        }
    }
    private int page_size;
    private int page_count;
    private PageTable page_table;
    public PageAllocator(int total_memory_size, int page_size) {
        super(total_memory_size);
        this.page_size = page_size;
        this.page_count = total_memory_size / page_size;
        this.page_table = new PageTable(this.page_count);
    }
    @Override
    Pages findFreeSpace(int size) {
        int page_num = (int) Math.ceil((double) size / page_size);
        return page_table.findFreePages(page_num);
    }
    @Override
    void allocate(int pid, Memory memory) {
        page_table.allocate(pid, (Pages)memory);
    }
    @Override
    void release(int pid) {
        page_table.release(pid);
    }
    void show() {
        System.out.println(page_table);
    }
    public static void main(String[] args) {
        PageAllocator allocator = new PageAllocator(1024, 64);
        Pages pages1 = allocator.findFreeSpace(64);
        allocator.allocate(1, pages1);
        allocator.show();
        Pages pages2 = allocator.findFreeSpace(164);
        allocator.allocate(2, pages2);
        allocator.show();
        allocator.release(1);
        allocator.show();
        Pages pages3 = allocator.findFreeSpace(256);
        allocator.allocate(3, pages3);
        allocator.show();
    }
}