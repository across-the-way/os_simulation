package com.example.hello.myInterrupt;

public class myInterrupt {
    InterruptType type;
    int param;
    Object[] objects;

    public myInterrupt(InterruptType type) {
        this.type = type;
    }

    public myInterrupt(InterruptType type, Object... objects) {
        this.type = type;
        this.objects = objects;
        this.param = objects.length;
    }

    public InterruptType getType() {
        return type;
    }

    public Object getObject(int index) {
        if (index >= param) return null;
        return objects[index];
    }

    public Object[] getObjects() {
        return objects;
    }
}
