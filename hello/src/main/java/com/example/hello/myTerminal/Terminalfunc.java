package com.example.hello.myTerminal;

import java.nio.file.FileSystem;

import com.example.hello.controller.myKernel;
import com.example.hello.myInterrupt.InterruptType;
import com.example.hello.myInterrupt.SystemCallType;
import com.example.hello.myInterrupt.myInterrupt;

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
    
    public static void Terminalchmod(Object[] objects, myKernel kernel) {
        if (objects.length != 1) {
            kernel.terminal_message = "参数不符合要求!";
            kernel.terminal_update = true;
            return;
        }
        String curpath = kernel.getFs().getCurPath();
        String fileName = (String) objects[0];
        String content = kernel.getFs().changeImode(curpath, fileName);
        kernel.terminal_message = content;
        kernel.terminal_update = true;
    }

    public static void Terminalrm(Object[] objects, myKernel kernel) {
        if (objects.length == 2) {
            if (((String) objects[0]).equals("-r")) {
                if (kernel.getFs().rmdir(kernel.getFs().getCurPath(), (String) objects[1])) {
                    kernel.terminal_message = "ok";
                } else {
                    kernel.terminal_message = "文件夹删除失败";
                }
            }
        } else if (objects.length == 1) {
            if (kernel.getFs().rm(kernel.getFs().getCurPath(), (String) objects[0])) {
                kernel.terminal_message = "ok";
            } else {
                kernel.terminal_message = "文件删除失败";
            }
        }
        kernel.terminal_update = true;
    }

    public static void Terminalcat(Object[] objects, myKernel kernel) {
        if (objects.length != 1) {
            kernel.terminal_message = "参数不符合要求!";
            kernel.terminal_update = true;
            return;
        }

        String curpath = kernel.getFs().getCurPath();
        String fileName = (String) objects[0];
        String content = kernel.getFs().cat(curpath, fileName);

        // if (content == null) {
        // kernel.terminal_message = "没有该文件/访问的是文件夹/文件内容为空";
        // } else {
        // kernel.terminal_message = "文件内容： " + content;
        // }
        kernel.terminal_message = content;
        kernel.terminal_update = true;

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
