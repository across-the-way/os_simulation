package com.example.hello.myDevice;

public class ioRequest {
    private int pid;
    private int iotime;
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public int getIotime() {
        return iotime;
    }
    public void setIotime(int iotime) {
        this.iotime = iotime;
    }
    public ioRequest(int pid,int iotime)
    {
        this.pid=pid;
        this.iotime=iotime;
    }
}
