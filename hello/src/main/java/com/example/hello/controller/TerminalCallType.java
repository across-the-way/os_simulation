package com.example.hello.controller;

public enum TerminalCallType {

    // 打印当前路径
    pwd,

    // 切换当前路径
    cd,

    // 创建一个文件
    touch,

    // 创建一个文件夹
    mkdir,

    // 删除一个文件
    rm,

    // 输出当前文件内容
    cat,

    // 其他消息
    err
}
