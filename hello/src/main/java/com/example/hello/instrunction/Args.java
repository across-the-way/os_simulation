package com.example.hello.instrunction;

public class Args {
    // C指令的参数
    public int cpu_time;

    // K指令/P指令
    public int io_time;

    // R指令/W指令
    public String file_name;
    public int file_size;

    // M指令
    public int process_size;

    // Y指令
    public int priority_number;

    // S指令
    public String resource_name;
    public int resource_number;

    public Args() {

    }

    public int getCpu_time() {
        return cpu_time;
    }

    public void setCpu_time(int cpu_time) {
        this.cpu_time = cpu_time;
    }

    public int getIo_time() {
        return io_time;
    }

    public void setIo_time(int io_time) {
        this.io_time = io_time;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public int getProcess_size() {
        return process_size;
    }

    public void setProcess_size(int process_size) {
        this.process_size = process_size;
    }

    public int getPriority_number() {
        return priority_number;
    }

    public void setPriority_number(int priority_number) {
        this.priority_number = priority_number;
    }

    public String getResource_name() {
        return resource_name;
    }

    public void setResource_name(String resource_name) {
        this.resource_name = resource_name;
    }

    public int getResource_number() {
        return resource_number;
    }

    public void setResource_number(int resource_number) {
        this.resource_number = resource_number;
    }

}
