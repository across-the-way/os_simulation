package com.example.hello.myProcess;

import java.util.ArrayList;
import java.util.List;

public class mySemaphore {
    public int resource;
    public String name;
    public List<Item> Process;
    public int ttl;

    public mySemaphore(String name, int resource) {
        this.name = name;
        this.resource = resource;
        this.Process = new ArrayList<Item>();
        this.ttl = 120;
    }

    // 模拟对pid的进程对该信号量的wait操作
    public boolean wait(int pid) {
        if (findItemById(pid) == null) {
            this.resource--;
            if (this.resource >= 0) {
                this.Process.add(new Item(pid, 2));
                return true;
            } else {
                this.Process.add(new Item(pid, 1));
                return false;
            }
        } else {
            // 已经获取该信号量的不再占用更多资源
            return true;
        }

    }

    // 模拟对pid的进程对该信号量的signal操作
    public boolean signal(int pid) {
        Item item = findItemById(pid);
        if (item == null || item.label == 1) {
            return false;
        }

        this.Process.remove(item);
        this.resource++;
        return true;
    }

    // 时钟周期片到了唤醒一个进程
    public int WakeProcess() {

        // 将队列的第一个元素移到队尾
        for (int counter = 0; counter < this.Process.size(); counter++) {
            Item item = this.Process.removeFirst();
            this.Process.add(item);

            if (item.label == 2) {
                // 2说明当前pid的进程不在Waiting队列,而是在正常执行
                continue;
            }
            this.resource--;
            // 将选中进程的标签更新
            item.label = 2;
            return item.pid;
        }

        // 队列为空或者没有进程处于Waiting队列
        return -1;
    }

    public boolean isClear() {
        return this.Process.isEmpty() && this.ttl < 100;
    }

    public Item findItemById(int pid) {
        for (Item item : this.Process) {
            if (item.pid == pid) {
                return item;
            }
        }
        return null;
    }

}

class Item {
    /*
     * Label = 1 ==> 该进程wait的时候资源不够而陷入等待队列
     * Label = 2 ==> 该进程wait的时候资源足够,但没有运行到signal的状态
     */

    public int pid;
    public int label;

    public Item(int pid, int label) {
        this.pid = pid;
        this.label = label;
    }

}
