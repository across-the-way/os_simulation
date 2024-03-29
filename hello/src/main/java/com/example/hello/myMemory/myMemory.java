package com.example.hello.myMemory;

import com.example.hello.controller.myKernel;

public class myMemory {
    private myKernel kernel;
    private allocateStrategy strategy = allocateStrategy.FirstFit;
    private int memory_size = 4096; // 假设内存大小为4096字节，一条指令为4字节

    public myMemory(myKernel kernel) {
        this.kernel = kernel;
    }

    public boolean allocate(int pid, int size) {
        // 按照分配方法尝试分配

        // 寻找内存中满足要求空闲空间

        // 若能找到，
        
        // 将空闲空间标记为占用，并将pid和对应内存区域绑定
        
        // 按需调页方法默认分配成功

        return false;
    }

    public void release(int pid) {
        // 将pid和对应内存区域解绑

        // 将对应内存区域置为空闲
            // 按分配方法不同对整个内存进行不同调整
    }

    public boolean isPageFault(int pid, int pc) {
        // 检查pid当前pc是否会触发pagefault
        // 若分配方法不是按需调页
        return false;
        // 检查
    }

    public void update() {
        // 暂时不知道干什么
    }

    public void page(int pid, int pc) {
        // 仅用于需调页方法

        // 将pc对应的页从交换空间调入内存

        // 按照调页方法寻找被换出的页

        pageSwap(pid, pc); // 参数乱填的
    }

    private void pageSwap(int old_page, int new_page) {
        // 页交换的机制
    }
    
}
