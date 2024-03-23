package com.example.hello.myFile;

import java.util.*;

public class FileSystem {
    public FOLDER root;
    public FOLDER currentFolder;
    public String workingPath = "/home";

    public FileSystem() {
        this.root = new FOLDER("home");
        this.currentFolder = root;
    }

    public void mkdir(String folderName) {
        FOLDER newFolder = new FOLDER(folderName);
        this.currentFolder.childFolders.add(newFolder);
        newFolder.setParentFolder(this.currentFolder);
    }

    public boolean cd(String path) {

        String prefix = new String();
        if (path.length() > 1)
            prefix = path.substring(0, 2);

        // cd 到根目录
        if (path.equals("/")) {
            this.currentFolder = this.root;
            this.workingPath = "/home";
        } // 回到上级目录
        else if (path.equals("../")) {
            if (this.currentFolder != this.root) {
                int split_index = this.workingPath.indexOf(this.currentFolder.name);
                this.workingPath = this.workingPath.substring(0, split_index - 1);
                this.currentFolder = this.currentFolder.parentFolder;
            }

        } // cd 到当前目录下的一个文件夹
        else {
            if (path.equals("./"))
                return true;
            String[] folders = new String[100];
            String prePath = this.workingPath;
            FOLDER preFolder = this.currentFolder;
            if (prefix.equals("./"))
                folders = path.substring(2).split("/");
            else
                folders = path.split("/");
            for (int i = 0; i < folders.length; i++) {
                int child_index = this.currentFolder.findChildFolderByName(folders[i]);
                if (child_index == -1) {
                    this.currentFolder = preFolder;
                    this.workingPath = prePath;
                    return false;
                } else {
                    this.currentFolder = this.currentFolder.childFolders.get(child_index);
                    this.workingPath += "/" + folders[i];
                }
            }
        }
        return true;
    }

    public List<String> ls() {
        List<String> res = new ArrayList<String>();
        for (int i = 0; i < this.currentFolder.childFolders.size(); i++)
            res.add("FOLDER:" + this.currentFolder.childFolders.get(i).name);

        for (int i = 0; i < this.currentFolder.childFiles.size(); i++)
            res.add("FILE:" + this.currentFolder.childFiles.get(i).name);
        return res;
    }

    public void touch(String fileName) {
        FILE newfFile = new FILE(fileName);
        newfFile.setParentFolder(this.currentFolder);
        this.currentFolder.childFiles.add(newfFile);
    }

    public boolean rm(String option, String name) {
        if (option.equals("file")) {
            for (int i = 0; i < this.currentFolder.childFiles.size(); i++)
                if (this.currentFolder.childFiles.get((i)).name.equals(name)) {
                    this.currentFolder.childFiles.remove(i);
                    return true;
                }
        } else {
            for (int i = 0; i < this.currentFolder.childFolders.size(); i++)
                if (this.currentFolder.childFolders.get((i)).name.equals(name)) {
                    this.currentFolder.childFolders.remove(i);
                    return true;
                }
        }

        return false;
    }
}
