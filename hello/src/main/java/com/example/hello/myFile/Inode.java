package com.example.hello.myFile;

import javax.swing.*;
import java.util.HashMap;

public class Inode {
    private String name;
    private int type;//0为目录，1为文件
    private int imode;//读写权限，0为只读，1为读写


    //目录结构
    private HashMap<String, Inode> directoryEntries;//子目录或子文件的指针表

    //文件结构
    private int startBlock;
    private int blockSize;

    public Inode(String name, int type, int imode) {
        this.name = name;
        this.type = type;
        this.imode = imode;
        if (type == 0) {
            directoryEntries = new HashMap<String, Inode>();
        } else {
            startBlock = -1;//未初始化
            blockSize = 0;
        }
    }

    public Inode findChild(String name) {
        return directoryEntries.get(name);
    }

    public HashMap<String, Inode> getDirectoryEntries() {
        return directoryEntries;
    }

    public void insertFileInDir(String name, Inode child) {
        directoryEntries.put(name, child);
    }

    public void deleteFileInDir(String name) {
        directoryEntries.remove(name);
    }

    public int getStartBlock() {
        return startBlock;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getImode() {
        return imode;
    }
}
class Ext4{
    //如果不用连续分区就用Ext4的存储结构
}