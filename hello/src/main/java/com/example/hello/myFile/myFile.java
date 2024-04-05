package com.example.hello.myFile;

import com.example.hello.controller.myKernel;
import java.util.*;


public class myFile {
    private myKernel kernel;

    private boolean[] bitmap;//false代表空闲
    private TreeMap<Integer, Integer> spaceTable;//空闲块的【长度,起始位置】
    private OpenFileTable ftable;
    private Inode root;//根目录

    public class queueEntry {
        Inode file;
        int type;  //0为读，1为写
        int startBlock;
        int blockSize;
        int isFinished;

        public queueEntry (Inode file, int type, int startBlock, int blockSize, int isFinished) {
            this.file = file;
            this.type = type;
            this.startBlock = startBlock;
            this.blockSize = blockSize;
            this.isFinished = isFinished;
        }
    }

    private Queue<queueEntry> rwqueue;

    public boolean allocate(Inode file, int fileSize) {
        if (spaceTable.higherKey(fileSize) == null) {
            //磁盘空间不足，触发中断
            return false;
        } else {
            Map.Entry<Integer, Integer> entry = spaceTable.higherEntry(fileSize);
            int newSize = entry.getKey() - fileSize;
            int newSt = entry.getValue() + fileSize;
            //更新空闲空间队列
            spaceTable.remove(entry.getKey());
            spaceTable.put(newSize, newSt);
            //更新bitmap
            Arrays.fill(bitmap, entry.getValue(), entry.getValue() + fileSize, true);
            //更新Inode的storage
            file.putStorage(entry.getKey(), fileSize);
            //加入读写队列
            rwqueue.offer(new queueEntry(file, 1, entry.getValue(), fileSize, 1));
            return true;
        }
    }

    public myFile(myKernel kernel) {
        this.kernel = kernel;
        ftable = new OpenFileTable();
        bitmap = new boolean[1024];
        spaceTable = new TreeMap<>();
        spaceTable.put(1024, 0);
        rwqueue = new LinkedList<queueEntry>();
        Arrays.fill(bitmap, false);
        root = new Inode("root", 0, 1);
    }

    private int move_need = 1;// 假设移动一格磁盘块需要一个操作数
    private int rw_need = 2; // 假设读写一格磁盘块需要两个操作数

    private int ops_max=100; // 一个时钟中断周期内能进行进行的最多操作数
    private int ops_cur; // 当前已经使用的操作数

    private int curMhead;//当前的磁头位置

    public void update() {
        ops_cur = 0;
        // 检查当前是否有进程进行文件读写任务，若无，返回
        // 模拟磁盘进行读写操作
        while(ops_cur<ops_max) {
            if (rwqueue.isEmpty()) {
                System.out.println("读写队列中无文件读写任务");
                break;
            }
            boolean isfinished=disk_read_write();
            // 若有文件读写任务完成，即有任务的剩余磁盘块归零
            if(isfinished)
            {
                // 发送中断系统调用FileFinish
                // 启动磁盘调度，进行下一个文件读写操作
            }
        }
    }

    private boolean disk_read_write() {
        // 获得当前磁头位置
        // 磁头移向磁盘块读写队列中离磁头最近的磁盘块移动
//        queueEntry nextEntry = findNearEntry(curMhead);//找最近
        queueEntry nextEntry=rwqueue.peek();//FCFS
        if(nextEntry == null)
            return false;
        int ops_move = Math.abs(curMhead - nextEntry.startBlock) * move_need;
        int ops_rw = nextEntry.blockSize * rw_need;
        int ops_need = ops_move + ops_rw;
        int ops_remain=ops_max-ops_cur;
        // 移动过程更新操作数
        // 如果磁头能移动到磁盘块
        // 尝试读写操作
        // 若能读写
        // 磁盘块对应的任务的剩余磁盘块减一
        if(ops_remain>=ops_need)
        {
            ops_cur+=ops_need;
            curMhead=nextEntry.startBlock+nextEntry.blockSize;
            queueEntry out=rwqueue.poll();
            return out.isFinished == 1;
        }
        else if(ops_remain>ops_move)
        {
            int rw=(ops_remain-ops_move)/2;
            curMhead=nextEntry.startBlock+rw;
            nextEntry.startBlock+=rw;
            nextEntry.blockSize-=rw;
            ops_cur=ops_max;
            return false;
        }
        else
        {
            //最大操作数不满足移动
            //磁头改变位置
            ops_cur=ops_max;
            if(nextEntry.startBlock>curMhead){
                curMhead+=ops_remain;
            }
            else {
                curMhead-=ops_remain;
            }

            return false;
        }
    }


    private Inode findInode(String path) {
        String[] fileName = path.split("/");
        Inode curInode = root;
        if (!fileName[0].equals("root")) {
            //路径错误
            return null;
        }
        for (int i = 1; i < fileName.length; i++) {
            curInode = curInode.findChild(fileName[i]);
            if (curInode == null) {
                //路径错误
                return null;
            }
        }
        return curInode;
    }

    // 更新空闲空间表，如果能合并则合并空闲空间
    public void updateSpaceTable(int start, int size) {
        int end = start + size;
        int mergeStart = -1, mergeEnd = -1;
        int newStart = start, newSize = size;
        if (start != 0 && !bitmap[start - 1]) {
            mergeEnd = start;
        }
        if (end != 1024 && !bitmap[end]) {
            mergeStart = end;
        }
        if (mergeStart == -1 && mergeEnd == -1) {
            return;
        }
        for (int freeSize : spaceTable.keySet()) {
            if (spaceTable.get(freeSize) == mergeStart) {
                newSize += freeSize;
            }
            if (spaceTable.get(freeSize) + freeSize == mergeEnd) {
                newSize += freeSize;
                newStart -= freeSize;
            }
        }
    }

    public void freeUp(Inode curInode) {
        if (curInode.getType() == 1) {
            if (curInode.isStorageEmpty())
                return;
            Iterator<Map.Entry<Integer, Integer>> it = curInode.getStorage().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> curSpace = it.next();
                Arrays.fill(bitmap, curSpace.getKey(), curSpace.getKey() + curSpace.getValue(), false);
                //更新spaceTable
                updateSpaceTable(curSpace.getKey(), curSpace.getValue());
            }
        } else if (curInode.getType() == 0) {
            HashMap<String, Inode> curDir = curInode.getDirectoryEntries();
            for (Inode i : curDir.values()) {
                freeUp(i);
            }
        }
    }

    //以下parent_name是文件绝对地址
    public void touch(String parent_name, String file_name) {
        if (parent_name.isEmpty()) {
            //路径为空触发中断
            return;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);
        // 创建文件
        if (pInode == null) {
            //路径错误触发中断
            return;
        }
        Inode newInode = new Inode(file_name, 1, 1);
        pInode.insertFileInDir(file_name, newInode);
    }

    public void rm(String parent_name, String file_name) {
        if (parent_name.isEmpty()) {
            //路径为空触发中断
            return;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);
        // 删除文件
        if (pInode == null) {
            //路径错误触发中断
            return;
        }
        pInode.deleteFileInDir(file_name);
        // 释放文件磁盘空间
        freeUp(pInode.findChild(file_name));
    }

    public void mkdir(String parent_name, String dir_name) {
        if (parent_name.isEmpty()) {
            //路径为空触发中断
            return;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);
        // 创建目录
        if (pInode == null) {
            //路径错误触发中断
            return;
        }
        Inode newInode = new Inode(dir_name, 0, 0);
        pInode.insertFileInDir(dir_name, newInode);
    }

    public void rmdir(String parent_name, String dir_name) {
        if (parent_name.isEmpty()) {
            //路径为空触发中断
            return;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);

        if (pInode == null) {
            //路径错误触发中断
            return;
        }
        // 递归释放文件夹中所有文件的磁盘空间
        freeUp(pInode.findChild(dir_name));
        // 删除目录
        pInode.deleteFileInDir(dir_name);
        // 删除所有
    }

    public int open(int pid, String path) {
        //先默认都是读写模式打开
        return ftable.open(pid, path, 2);
    }

    public void close(int pid, int fd) {
        ftable.close(pid, fd);
    }

    public void write(int pid, int fd, int usage_size) {
        String path = ftable.findInodeByFd(fd);
        Inode file = findInode(path);
        if (file != null) {
            allocate(file, usage_size);
        }

        // 加入文件读写待完成表

        // 为文件分配空闲磁盘块

        // 将所有用到的空闲磁盘块，加入磁盘块读写队列
    }

    public void read(int pid, int fd, int usage_size) {
        String path = ftable.findInodeByFd(fd);
        Inode file = findInode(path);
        if (file == null)
            return;
        // 默认从头读
        // 加入文件读写待完成表
        // 将文件对应的所有磁盘块，加入磁盘块读写队列
        Iterator<Map.Entry<Integer, Integer>> it = file.getStorage().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> curSpace = it.next();
            if (usage_size <= curSpace.getValue()) {
                rwqueue.offer(new queueEntry(file, 0, curSpace.getKey(), curSpace.getKey() + usage_size, 1));
                break;
            } else {
                usage_size -= curSpace.getValue();
                rwqueue.offer(new queueEntry(file, 0, curSpace.getKey(), curSpace.getKey() + curSpace.getValue(), 0));
            }
        }
    }
}