package com.example.hello.filesystem;

import java.util.*;

public class FOLDER {
    String name = new String();

    // 目录访问权限
    boolean readable = true;
    boolean writable = true;

    // 该目录下的所有文件夹
    List<FOLDER> childFolders;
    // 该目录下的所有文件
    List<FILE> childFiles;
    // 该目录的父目录
    FOLDER parentFolder;

    public FOLDER(String name) {
        this.name = name;
        this.childFolders = new ArrayList<FOLDER>();
        this.childFiles = new ArrayList<FILE>();
    }

    public int findChildFolderByName(String name) {
        for (int i = 0; i < childFolders.size(); i++) {
            if (name.equals(childFolders.get(i).name))
                return i;
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public List<FOLDER> getChildFolders() {
        return childFolders;
    }

    public void setChildFolders(List<FOLDER> childFolders) {
        this.childFolders = childFolders;
    }

    public List<FILE> getChildFiles() {
        return childFiles;
    }

    public void setChildFiles(List<FILE> childFiles) {
        this.childFiles = childFiles;
    }

    public FOLDER getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FOLDER parentFolder) {
        this.parentFolder = parentFolder;
    }

}
