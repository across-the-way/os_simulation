package com.example.hello.controller;

public class myInterrupt {
    InterruptType type;     //中断类型
    int param;              //参数个数
    Object[] objects;       //中断参数   

    //不带参数的构造方法
    public myInterrupt(InterruptType type) {
        this.type = type;
    }
    //可变参数的构造方法
    public myInterrupt(InterruptType type, Object... objects) {
        this.type = type;
        this.objects = objects;
        this.param = objects.length;
    }
    //返回中断的类型
    public InterruptType getType() {
        return type;
    }
    //根据索引获取中断所带参数数组中的元素
    public Object getObject(int index) {
        if (index >= param) return null;
        return objects[index];
    }
    //获取中断的参数数组
    public Object[] getObjects() {
        return objects;
    }
}
