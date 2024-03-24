package com.example.hello.myFile;

import java.util.HashMap;

public class Inode {
    private String name;
    private int type;//0为目录，1为文件
    private int imode;//读写权限，0为只读，1为读写
    private Inode fatherdir;

    //目录结构
    private HashMap<String, Inode> directoryEntries;//子目录或子文件的指针表

    //文件结构
    private int startBlock;
    private int blocksize;

    public Inode(String name, int type, int imode,Inode fatherdir) {
        this.name = name;
        this.type = type;
        this.imode = imode;
        this.fatherdir=fatherdir;
        if (type == 0) {
            directoryEntries = new HashMap<String, Inode>();
        } else {
            startBlock = -1;//未初始化
            blocksize = 0;
        }
    }

    public Inode getFatherdir() {
        return fatherdir;
    }
}
class Ext4{
    //如果不用连续分区就用Ext4的存储结构
}