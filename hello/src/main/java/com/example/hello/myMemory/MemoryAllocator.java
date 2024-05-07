package com.example.hello.myMemory;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

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
    public List<Block> free_blocks;
    public Map<Integer, Block> used_memory; // 进程-内存表
    private allocateStrategy allocation_policy = allocateStrategy.FirstFit;
    private int last_index = 0;//用于NEXT_FIT,记录上一次在free_blocks中查找结束的位置

    public ContiguousAllocator(int total_memory_size, allocateStrategy strategy) {
        super(total_memory_size);
        free_blocks = new ArrayList<>();
        free_blocks.add(new Block(0, total_memory_size));
        used_memory = new HashMap<Integer, Block>();
        switch (strategy) {
            case FirstFit:
                allocation_policy = allocateStrategy.FirstFit;
                break;
            case NextFit:
                allocation_policy = allocateStrategy.NextFit;
                break;
            case BestFit:
                allocation_policy = allocateStrategy.BestFit;
                break;
            case WorstFit:
                allocation_policy = allocateStrategy.WorstFit;
                break;
            default:
                System.out.println("Unknown allocation strategy");
                break;
        }
    }

    @Override
    Block findFreeSpace(int size) {
        switch (allocation_policy) {
            case FirstFit: return firstFit(size);
            case NextFit: return nextFit(size);
            case BestFit: return bestFit(size);
            case WorstFit: return worstFit(size);
            default: break;
        }
        return null;
    }

    @Override
    void allocate(int pid, Memory memory) {
        used_memory.put(pid, (Block) memory);
        free_memory_size -= memory.getSize();
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
        free_memory_size += block.getSize();
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
        ContiguousAllocator allocator = new ContiguousAllocator(100, allocateStrategy.FirstFit);
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
    private int page_capacity;
    public PageTable page_table;
    public PageAllocator(int total_memory_size, int page_size) {
        super(total_memory_size);
        this.page_size = page_size;
        this.page_capacity = total_memory_size / page_size;
        this.page_table = new PageTable(this.page_capacity);
    }
    @Override
    Pages findFreeSpace(int size) {
        int page_num = (int) Math.ceil((double) size / page_size);
        return page_table.findFreePages(page_num);
    }
    @Override
    void allocate(int pid, Memory memory) {
        page_table.allocate(pid, (Pages)memory);
        free_memory_size -= memory.getSize();
    }
    @Override
    void release(int pid) {
        free_memory_size += page_table.used_pages.get(pid).getSize();
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

class DemandPageAllocator extends MemoryAllocator {
    abstract class Cache {
        Cache (int capacity) {
            this.count = 0;
            this.capacity = capacity;
            head = new Node();
            tail = new Node();
            head.next = tail;
            tail.prev = head;
        }
        class Node {
            public int page_num_physical;
            public int pid;
            public int page_num_virtual;
            public transient LRUCache.Node prev, next;
            Node() {}
            Node(int page_num_physical, int pid, int page_num_virtual) {
                this.page_num_physical = page_num_physical;
                this.pid = pid;
                this.page_num_virtual = page_num_virtual;
            }
            public void set(int page_num_physical, int pid, int page_num_virtual) {
                this.page_num_physical = page_num_physical;
                this.pid = pid;
                this.page_num_virtual = page_num_virtual;
            }
        }
        int count;
        int capacity;
        Node head, tail;
        Map<Integer, Node> cache = new HashMap<Integer, Node>();

        public abstract void visit(int page_num);

        public void put(int page_num, int pid, int page_num_virtual) {
            Node node = cache.get(page_num);
            if (node == null) {
                node = new Node(page_num, pid, page_num_virtual);
                cache.put(page_num, node);
                addToHead(node);
                count++;
            }
            else {
                cache.get(page_num).set(page_num, pid, page_num_virtual);
                moveToHead(node);
            }
        }
        public void remove(int page_num) {
            Node node = cache.get(page_num);
            if (node != null) {
                removeNode(node);
                cache.remove(page_num);
                count--;
            }
        }
        public int get(int page_num) {
            Node node = cache.get(page_num);
            if (node == null) {
                return -1;
            }
            moveToHead(node);
            return node.pid;
        }
        public int getTailPagePhysical() {
            if (tail.prev == head) return -1;
            return tail.prev.page_num_physical;
        }
        public int getTailPid() {
            if (tail.prev == head) return -1;
            return tail.prev.pid;
        }
        public int getTailPageVirtual() {
            if (tail.prev == head) return -1;
            return tail.prev.page_num_virtual;
        }

        protected void addToHead(Node node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }
        protected void addToEnd(Node node) {
            node.prev = tail.prev;
            node.next = tail;
            tail.prev.next = node;
            tail.prev = node;
        }
        protected void removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        protected void moveToHead(Node node) {
            removeNode(node);
            addToHead(node);
        }
        protected void moveToEnd(Node node) {
            removeNode(node);
            addToEnd(node);
        }
        protected Node removeTail() {
            Node res = tail.prev;
            removeNode(res);
            return res;
        }
    }
    class FIFOCache extends Cache {
        FIFOCache(int capacity) {
            super(capacity);
        }
        public void visit(int page_num) {
            return;
        }
    }
    class LRUCache extends Cache {
        LRUCache(int capacity) {
            super(capacity);
        }
        public void visit(int page_num) {
            Node node = cache.get(page_num);
            if (node == null) {
                return;
            }
            moveToHead(node);
            return;
        }
    }
    class SwapPartition {
        int page_count;
        int used_count;

        public SwapPartition(int page_count) {
            this.page_count = page_count;
            this.used_count = 0;
        }
        public boolean swapIn() {
            if (used_count < page_count) {
                used_count++;
                return true;
            }
            return false;
        }
        public boolean swapOut() {
            if (used_count > 0) {
                used_count--;
                return true;
            }
            return false;
        }
        public void remove() {
            if (used_count > 0) {
                used_count--;
            }
        }
        public boolean isCompatible(int count) {
            return used_count + count <= page_count;
        }
    }
    class PageTable {
        Map<Integer, DemandPages> used_pages;
        BitSet free_pages;
        SwapPartition swap_partition;
        Cache cache;

        public PageTable(int page_capacity, allocateStrategy strategy) {
            used_pages = new HashMap<>();
            free_pages = new BitSet(page_capacity);
            free_pages.set(0, page_capacity);
            swap_partition = new SwapPartition(page_capacity * 4);
            if (strategy == allocateStrategy.LRU) cache = new LRUCache(page_capacity);
            else cache = new FIFOCache(page_capacity);
        }

        public int findFreePage() {
            for (int i = 0; i < free_pages.size(); i++) {
                if (free_pages.get(i)) {
                    return i;
                }
            }
            return -1;
        }

        public DemandPages findFreePages(int page_count) {
            DemandPages pages = new DemandPages(page_count * page_size);
            int cnt = 0;
            for (int i = 0; i < free_pages.size(); i++) {
                if (free_pages.get(i)) {
                    pages.addDemandPage(cnt, i);
                    cnt++;
                    if (cnt == page_count) {
                        return pages;
                    }
                }
            }
            if (swap_partition.isCompatible(page_count - cnt)) {
                for (int i = cnt; i < page_count; i++) {
                    swap_partition.swapIn();
                    pages.addDemandPage(i);
                }
                return pages;
            }
            return null;
        }
        
        public void allocate(int pid, DemandPages pages) {
            used_pages.put(pid, pages);
            for (Pair<Integer, Integer> virtual_physical_page : pages.getAllPhysicalPages()) {
                int physical_page = virtual_physical_page.getRight();
                free_pages.clear(physical_page);
                cache.put(physical_page, pid, virtual_physical_page.getLeft());
                free_memory_size -= page_size;
            }
        }

        public void release(int pid) {
            DemandPages pages = used_pages.remove(pid);
            if (pages != null) {
                for (Pair<Integer, Integer> virtual_physical_page : pages.getAllPhysicalPages()) {
                    int physical_page = virtual_physical_page.getRight();
                    free_pages.set(physical_page);
                    cache.remove(physical_page);
                    free_memory_size += page_size;
                }
                for (int i = 0; i < pages.getVirtualPageCount(); i++) {
                    swap_partition.remove();
                }
            }
        }

        public void swapOut(int pid) {
            DemandPages pages = used_pages.get(pid);
            if (pages != null) {
                for (Pair<Integer, Integer> virtual_physical_page : pages.getAllPhysicalPages()) {
                    int physical_page = virtual_physical_page.getRight();
                    free_pages.set(physical_page);
                    cache.remove(physical_page);
                    swap_partition.swapIn();
                    used_pages.get(pid).swapPageOut(virtual_physical_page.getLeft());
                    free_memory_size += page_size;
                }
            }
        }
    
        public boolean isPageFault(int pid, int page_num) {
            int page_num_physical = getPhysicalPage(pid, page_num);
            if (page_num_physical == -1) {
                page_num_physical = findFreePage();
                if (page_num_physical == -1) {
                    page_num_physical = cache.getTailPagePhysical();
                    if (cache.getTailPid() != -1) {
                        used_pages.get(cache.getTailPid()).swapPageOut(cache.getTailPageVirtual());
                    }
                } else {
                    free_pages.clear(page_num_physical);
                    free_memory_size -= page_size;
                    swap_partition.swapOut();
                }
                used_pages.get(pid).swapPageIn(page_num, page_num_physical);
                cache.put(page_num_physical, pid, page_num);
                faults++;
                return true;
            }
            cache.visit(page_num_physical);
            return false;
        }

        public int getPhysicalPage(int pid, int page_num_virtual) {
            return used_pages.get(pid).getPhysicalPage(page_num_virtual);
        }
    }
    
    private int page_size;
    private int page_capacity;
    public PageTable page_table;

    public int faults;
    public int pages;

    public DemandPageAllocator(int total_memory_size, int page_size, allocateStrategy strategy) {
        super(total_memory_size);
        this.page_size = page_size;
        this.page_capacity = total_memory_size / page_size;
        switch (strategy) {
            case LRU:
                this.page_table = new PageTable(page_capacity, strategy);
                break;
            case FIFO:
                this.page_table = new PageTable(page_capacity, strategy);
                break;
            default:
                break;
        }
        this.faults = 0;
        this.pages = 0;
    }

    @Override
    DemandPages findFreeSpace(int size) {
        int page_num = (int) Math.ceil((double) size / page_size);
        return page_table.findFreePages(page_num);
    }

    @Override
    void allocate(int pid, Memory memory) {
        page_table.allocate(pid, (DemandPages)memory);
    }

    @Override
    void release(int pid) {
        page_table.release(pid);
    }

    public void swapIn(int pid, int pc) {
        isPageFault(pid, pc);
    }

    public void swapOut(int pid) {
        page_table.swapOut(pid);
    }

    boolean isPageFault(int pid, int pc) {
        int page_num = pc / page_size;
        pages++;
        return page_table.isPageFault(pid, page_num);
    }

}