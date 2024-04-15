package com.example.hello.myDevice;

import java.util.*;

public class Device {    
    private String type;
    private boolean isBusy;
    private LinkedList<ioRequest> waitQueue;

    public Device(String type) {
        this.type = type;
        isBusy = false;
        waitQueue = new LinkedList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public LinkedList<ioRequest> getWaitQueue() {
        return waitQueue;
    }

    public void setWaitQueue(LinkedList<ioRequest> waitQueue) {
        this.waitQueue = waitQueue;
    }

    
}
