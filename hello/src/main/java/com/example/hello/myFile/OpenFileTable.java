package com.example.hello.myFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class OpenFileTable {
    //内部类，打开文件表的每一个表象
    private static class OpenFileEntry {
        private int pid;
        private String path;
        private int mode;//0只读，1只写，2读写

        public OpenFileEntry(int pid, String path, int mode) {
            this.pid = pid;
            this.path = path;
            this.mode = mode;
        }

        public int getPid() {
            return pid;
        }

        public String getPath() {
            return path;
        }
    }

    private HashMap<Integer, OpenFileEntry> filetable;//fd到entry的映射
    private HashMap<String, Set<Integer>> filePidTable;//path到文件所有pid的映射

    public OpenFileTable() {
        this.filetable = new HashMap<>();
        this.filePidTable = new HashMap<>();
    }

    public int open(int pid, String path, int mode) {
        //找到文件标识符，更新所有表项
        int fd = getUnusedFd();
        OpenFileEntry entry = new OpenFileEntry(pid, path, mode);
        filetable.put(fd, entry);
        if (!filePidTable.containsKey(path)) {
            filePidTable.put(path, new HashSet<>());
        }
        filePidTable.get(path).add(pid);
        return fd;
    }

    public void close(int pid, int fd) {
        // 将pid从全局打开文件表中文件号条目移除
        // 检查是否还有进程打开该文件
        // 若无从全局打开文件表移除文件号
        if (filetable.containsKey(fd)) {
            OpenFileEntry entry = filetable.get(fd);
            String path = entry.getPath();
            filetable.remove(fd);

            if (filePidTable.containsKey(path)) {
                filePidTable.get(path).remove(pid);
                if (filePidTable.get(path).isEmpty()) {
                    filePidTable.remove(path);
                }
            }
        } else {
            System.out.println("打开文件表close:未找到文件");
        }
    }

    //获取未使用的文件描述符,默认是最小的
    //可能要留标准输入输出和错误的fd
    private int getUnusedFd() {
        int fd = 0;
        while (filetable.containsKey(fd)) {
            fd++;
        }
        return fd;
    }

}
