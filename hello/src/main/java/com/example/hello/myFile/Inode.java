package com.example.hello.myFile;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

public class Inode {
    private String name;
    private int type;// 0为目录，1为文件
    private int imode;// 读写权限，0为只读，1为读写

    // 目录结构
    private HashMap<String, Inode> directoryEntries;// 子目录或子文件的指针表

    // 文件结构
    private LinkedHashMap<Integer, Integer> storage; // <startBlock, blockSize>

    public Inode(String name, int type, int imode) {
        this.name = name;
        this.type = type;
        this.imode = imode;
        directoryEntries = new HashMap<String, Inode>();
        storage = new LinkedHashMap<Integer, Integer>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public void putStorage(int startBlock, int blockSize) {
        storage.put(startBlock, blockSize);
    }

    public boolean isStorageEmpty() {
        return storage.isEmpty();
    }

    public LinkedHashMap<Integer, Integer> getStorage() {
        return storage;
    }

    public int getImode() {
        return imode;
    }

    public int getType() {
        return type;
    }

    public void setImode(int imode) {
        this.imode = imode;
    }
    
}