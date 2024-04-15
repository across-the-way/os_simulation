package com.example.hello.controller;

import java.nio.file.FileSystem;

public class Terminalfunc {
    public static void Terminalpwd(myKernel kernel) {
        kernel.terminal_message = "/" + kernel.getFs().getCurPath();
        kernel.terminal_update = true;
    }

    public static void Terminalcd(String arg, myKernel kernel) {
        if (arg.equals("..")) {
            if (!kernel.getFs().getCurPath().equals("filesystem")) {
                int last = kernel.getFs().getCurPath().lastIndexOf("/");
                kernel.getFs().setCurPath(kernel.getFs().getCurPath().substring(0, last));
                kernel.terminal_message = "ok";
            } else {
                kernel.terminal_message = "已经在根目录,无法返回上级目录";
            }
        } else if (kernel.getFs().findInode(kernel.getFs().getCurPath() + "/" + arg) == null) {
            kernel.terminal_message = "文件夹不存在";
        } else if (kernel.getFs().findInode(kernel.getFs().getCurPath() + "/" + arg).getType() == 0) {
            kernel.getFs().setCurPath(kernel.getFs().getCurPath() + "/" + arg);
            kernel.terminal_message = "ok";
        } else {
            kernel.terminal_message = "该名称为文件名称,无法打开";
        }
        kernel.terminal_update = true;
    }

    public static void Terminaltouch(String arg, myKernel kernel) {
        if (kernel.getFs().touch(kernel.getFs().getCurPath(), arg)) {
            kernel.terminal_message = "ok";
        } else {
            kernel.terminal_message = "文件名重复,创建文件失败";
        }
        kernel.terminal_update = true;
    }

    public static void Terminalmkdir(String arg, myKernel kernel) {
        if (kernel.getFs().mkdir(kernel.getFs().getCurPath(), arg)) {
            kernel.terminal_message = "ok";
        } else {
            kernel.terminal_message = "目录名重复,创建目录失败";
        }
        kernel.terminal_update = true;
    }

    public static void Terminalrm(Object[] objects) {

    }

    public static void Terminalcat(Object[] objects) {

    }

    public static void Terminalls(Object[] objects, myKernel kernel) {
        if (objects.length != 0)
            kernel.terminal_message = kernel.getFs().ls(kernel.getFs().getCurPath(), (String) objects[0]);
        else
            kernel.terminal_message = kernel.getFs().ls(kernel.getFs().getCurPath(), null);
        kernel.terminal_update = true;
    }

    public static void TerminalErr(myKernel kernel) {
        kernel.terminal_message = "err";
        kernel.terminal_update = true;
    }
}
