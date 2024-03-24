package com.example.hello.myFile;

import com.example.hello.controller.myKernel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;


public class myFile {
    private myKernel kernel;


    private boolean[] bitmap;//false代表空闲
    private OpenFileTable ftable;
    private Inode root;//根目录


    public myFile(myKernel kernel) {
        this.kernel = kernel;
        ftable = new OpenFileTable();
        bitmap = new boolean[1024];
        Arrays.fill(bitmap, false);
        root = new Inode("root", 0, 1);
    }

    private Queue<String> rwqueue;
    private int move_need = 1;// 假设移动一格磁盘块需要一个操作数
    private int rw_need = 2; // 假设读写一格磁盘块需要两个操作数

    private int ops_max; // 一个时钟中断周期内能进行进行的最多操作数
    private int ops_cur; // 当前已经使用的操作数

    public void update() {
        ops_cur = 0;

        // 检查当前是否有进程进行文件读写任务，若无，返回

        // 模拟磁盘进行读写操作
        disk_read_write();

        // 若有文件读写任务完成，即有任务的剩余磁盘块归零
        // 发送中断系统调用FileFinish
        // 启动磁盘调度，进行下一个文件读写操作
    }

    private void disk_read_write() {
        // 获得当前磁头位置

        // 磁头移向磁盘块读写队列中离磁头最近的磁盘块移动
        // 移动过程更新操作数
        // 如果磁头能移动到磁盘块
        // 尝试读写操作
        // 若能读写
        // 磁盘块对应的任务的剩余磁盘块减一
    }


    private Inode findInode(String path) {
        String[] fileName = path.split("/");
        Inode curInode = root;
        for (int i = 0; i < fileName.length; i++) {
            curInode = curInode.findChild(fileName[i]);
        }
        return curInode;
    }

    public void freeUp(Inode curInode) {
        if (curInode.getImode() == 1) {
            if (curInode.getStartBlock() == -1)
                return;
            for (int i = curInode.getStartBlock(); i < curInode.getBlockSize(); i++) {
                bitmap[i] = false;
            }
        } else if (curInode.getImode() == 0) {
            HashMap<String, Inode> curDir = curInode.getDirectoryEntries();
            for (Inode i : curDir.values()) {
                freeUp(i);
            }
        }
    }
    //以下parent_name是文件绝对地址
    public void touch(String parent_name, String file_name) {
        // 寻找父目录
        Inode fatherDir = findPath(parent_name);
        Inode curInode = findInode(parent_name);
        // 创建文件
        if (fatherDir != null) {
            fatherDir.getDirectoryEntries().put(file_name, new Inode(file_name, 1, 1));
        }
        Inode newInode = new Inode(file_name, 1, 1, curInode);
        curInode.insertFileInDir(file_name, newInode);
    }

    public void rm(String parent_name, String file_name) {
        // 寻找父目录
        Inode pInode = findInode(parent_name);
        Inode fatherDir = findPath(parent_name);
        // 删除文件
        pInode.deleteFileInDir(file_name);
        // 释放文件磁盘空间
        freeUp(pInode.findChild(file_name));

        //找打开文件表中是否有该文件的记录
        //若没有，则
        if (fatherDir != null) {
            if (fatherDir.getDirectoryEntries().containsKey(file_name)) {
                fatherDir.getDirectoryEntries().remove(file_name);
                System.out.println("删除文件: " + file_name);
                //删除磁盘中的记录
            } else {
                System.out.println("文件未找到: " + file_name);
            }
        }
    }

    public void mkdir(String parent_name, String dir_name) {
        // 寻找父目录
        Inode curInode = findInode(parent_name);
        Inode fatherDir = findPath(parent_name);
        // 创建目录
        Inode newInode = new Inode(dir_name, 0, 0, curInode);
        curInode.insertFileInDir(dir_name, newInode);

        if (fatherDir != null) {
            fatherDir.getDirectoryEntries().put(dir_name, new Inode(dir_name, 1, 1));
        }
    }

    public void rmdir(String parent_name, String dir_name) {
        // 寻找父目录
        Inode pInode = findInode(parent_name);


        // 归释放文件夹中所有文件的磁盘空间
        freeUp(pInode.findChild(dir_name));
        Inode fatherDir = findPath(parent_name);
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
        // 加入文件读写待完成表

        // 为文件分配空闲磁盘块

        // 将所有用到的空闲磁盘块，加入磁盘块读写队列
    }

    public void read(int pid, int fd, int usage_size) {
        // 加入文件读写待完成表

        // 将文件对应的所有磁盘块，加入磁盘块读写队列
    }

    public Inode findPath(String path) {
        if (path == null || path.isEmpty()) {
            System.out.println("输入路径为空");
            return null;
        }

        String[] paths = path.split("/");
        Inode currentptr;

        if (!paths[0].equals("root")) {
            System.out.println("输入路径错误");
            return null;
        } else {
            currentptr = root;
            for (int i = 1; i < paths.length; i++) {
                Inode nextInode = currentptr.getDirectoryEntries().get(paths[i]);
//                if (nextInode == null) {
//                    // 创建该名字的目录
//                    nextInode = new Inode(paths[i], 0, 1);
//                    currentptr.getDirectoryEntries().put(paths[i], nextInode);
//                }
                currentptr = nextInode;
            }
        }
        return currentptr;
    }

}