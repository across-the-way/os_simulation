package com.example.hello.myDevice;

import java.util.*;

public class Device {
    private String type;
    private boolean isBusy;
    private LinkedList<ioRequest> waitQueue;
    private int device_id;

    public Device(String type, int deviceid) {
        this.type = type;
        isBusy = false;
        waitQueue = new LinkedList<>();
        device_id = deviceid;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
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
