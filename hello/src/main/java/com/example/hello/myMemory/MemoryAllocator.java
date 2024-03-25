package com.example.hello.myMemory;

import java.util.ArrayList;
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
}

class ContiguousAllocator extends MemoryAllocator {
    private List<Block> free_blocks;
    protected Map<Integer, Block> used_memory; // 进程-内存表
    enum AllocationPolicy { FIRST_FIT, NEXT_FIT, BEST_FIT, WORST_FIT }
    private AllocationPolicy allocation_policy = AllocationPolicy.FIRST_FIT;

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
        Block block = used_memory.get(pid);
        // 这个实现不好
        for (int i = 0; i < free_blocks.size(); i++) {
            Block free_block = free_blocks.get(i);
            if (free_block.getStart() == block.getEnd() + 1) {
                free_blocks.get(i).reset(block.getStart(), block.getSize() + free_block.getSize());
                int last = i - 1;
                if (last >= 0 && free_blocks.get(last).getEnd() + 1 == free_blocks.get(i).getStart()) {
                    free_blocks.get(i).reset(free_blocks.get(last).getStart(), free_blocks.get(last).getSize() + free_blocks.get(i).getSize());
                    free_blocks.remove(last);
                }
                break;
            } else if (free_block.getEnd() + 1 == block.getStart()) {
                free_blocks.get(i).reset(free_block.getStart(), free_block.getSize() + block.getSize());
                int next = i + 1;
                if (next < free_blocks.size() && free_blocks.get(next).getStart() == free_blocks.get(i).getEnd() + 1) {
                    free_blocks.get(i).reset(free_blocks.get(i).getStart(), free_blocks.get(i).getSize() + free_blocks.get(next).getSize());
                    free_blocks.remove(next);
                }
                break;
            } else if (free_block.getEnd() < block.getStart()) {
                free_blocks.add(i, block);
                break;
            } else if (free_block.getStart() > block.getEnd()) {
                free_blocks.add(i, block);
                break;
            }
        }
        used_memory.remove(pid);
    }

    private Block firstFit(int size) {
        Block free_block = null;
        for (int i = 0; i < free_blocks.size(); i++) {
            Block block = free_blocks.get(i);
            if (block.getSize() >= size) {
                free_block = new Block(block.getStart(), size);
                free_blocks.get(i).reset(block.getStart() + size, block.getSize() - size);
                break;
            }
        }
        return free_block;
    }
    
    // private Block nextFit(int size) {
        
    // }
    
    // private Block bestFit(int size) {
        
    // }
    
    // private Block worstFit(int size) {
        
    // }
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