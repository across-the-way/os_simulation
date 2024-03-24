package com.example.hello.myFile;

public class FILE {
    String name = new String();
    String content = new String();

    // 文件访问权限
    boolean readable = true;
    boolean writable = true;

    // 父文件夹
    FOLDER parentFolder;

    public FILE(String name) {
        this.name = name;
        this.content = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public FOLDER getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FOLDER parentFolder) {
        this.parentFolder = parentFolder;
    }

}
