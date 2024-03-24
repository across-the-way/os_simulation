package com.example.hello.myFile;

import com.example.hello.controller.myKernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class myFile {
    private myKernel kernel;


    private boolean[] bitmap;//false代表空闲
    private HashMap<Integer,OpenFile> filetable;
    private Inode root;//根目录


    public myFile(myKernel kernel) {
        this.kernel = kernel;
        for(int i=0;i<bitmap.length;i++)
        {
            bitmap[i]=false;
        }
        filetable=new HashMap<Integer, OpenFile>();
        root=new Inode("root",0,1,null);
    }

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
    //以下parent_name是文件绝对地址
    public void touch(String parent_name, String file_name) {
        // 寻找父目录

        // 创建文件
    }

    public void rm(String parent_name, String file_name) {
        // 寻找父目录

        // 删除文件

    }

    public void mkdir(String parent_name, String dir_name) {
        // 寻找父目录

        // 创建目录
    }

    public void rmdir(String parent_name, String dir_name) {
        // 寻找父目录

        // 删除目录
        // 删除所有
    }

    public int open(int pid, String path) {
        int fd = -1;
        // 检查path对应的文件，确定文件号
        // 不在全局的打开文件表中，新建，将文件路径和pid添加到全局的打开文件表
        // 从全局的打开文件表中获得文件号
        return fd;
    }

    public void close(int pid, int fd) {
        // 将pid从全局打开文件表中文件号条目移除
        // 检查是否还有进程打开该文件
        // 若无从全局打开文件表移除文件号
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

}
class OpenFile
{
    private String filename;
    private int opencount;
    private int start;
    private int size;
    private int mode;//0只读，1读写，2创建，3添加

}