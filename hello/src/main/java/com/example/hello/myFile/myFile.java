package com.example.hello.myFile;

import com.example.hello.controller.myKernel;
import com.example.hello.myInterrupt.InterruptType;
import com.example.hello.myInterrupt.myInterrupt;

import java.util.*;

public class myFile {
    private myKernel kernel;
    private boolean[] bitmap;// false代表空闲
    private HashMap<Integer, Integer> spaceTable;// 空闲块的【起始位置,长度】
    private OpenFileTable ftable;
    private Inode root;// 根目录
    private String curPath;
    private Block block[];

    public String getCurPath() {
        return curPath;
    }

    public OpenFileTable getFtable() {
        return ftable;
    }

    public void setFtable(OpenFileTable ftable) {
        this.ftable = ftable;
    }

    public void setCurPath(String curPath) {
        this.curPath = curPath;
    }

    public class queueEntry {
        Inode file;
        int type; // 0为读，1为写
        int startBlock;
        int blockSize;
        int isFinished;

        public queueEntry(Inode file, int type, int startBlock, int blockSize, int isFinished) {
            this.file = file;
            this.type = type;
            this.startBlock = startBlock;
            this.blockSize = blockSize;
            this.isFinished = isFinished;
        }
    }

    private Queue<queueEntry> rwqueue;

    public boolean allocate(Inode file, int fileSize, int pid) {
        for (int key : spaceTable.keySet()) {
            int freeSize = spaceTable.get(key);
            if (freeSize >= fileSize) {
                // 更新空闲空间队列
                freeSize -= fileSize;
                spaceTable.remove(key);
                spaceTable.put(key + fileSize, freeSize);
                // 更新bitmap
                Arrays.fill(bitmap, key, key + fileSize, true);
                // 更新Inode的storage
                file.putStorage(key, fileSize);
                // 加入读写队列
                rwqueue.offer(new queueEntry(file, 1, key, fileSize, pid));
                return true;
            }
        }
        return false;
    }

    public int allocate1(Inode file, int fileSize, int pid) {
        for (int key : spaceTable.keySet()) {
            int freeSize = spaceTable.get(key);
            if (freeSize >= fileSize) {
                // 更新空闲空间队列
                freeSize -= fileSize;
                spaceTable.remove(key);
                spaceTable.put(key + fileSize, freeSize);
                // 更新bitmap
                Arrays.fill(bitmap, key, key + fileSize, true);
                // 更新Inode的storage
                file.putStorage(key, fileSize);
                // 加入读写队列
                rwqueue.offer(new queueEntry(file, 1, key, fileSize, pid));
                return key;
            }
        }
        return -1;
    }

    public myFile(myKernel kernel) {
        this.kernel = kernel;
        curPath = "filesystem";
        ftable = new OpenFileTable();

        bitmap = new boolean[1024];
        spaceTable = new HashMap<>();
        spaceTable.put(0, 1024);
        block = new Block[1024];
        for (int i = 0; i < 1024; i++) {
            block[i] = new Block();
        }
        rwqueue = new LinkedList<queueEntry>();
        Arrays.fill(bitmap, false);
        root = new Inode("filesystem", 0, 1);
        root.insertFileInDir("dev", new Inode("dev", 0, 1));
        root.insertFileInDir("taotao", new Inode("taotao", 0, 0));
        root.insertFileInDir("bupt", new Inode("bupt", 0, 1));
        root.insertFileInDir("lib", new Inode("lib", 0, 1));
        root.insertFileInDir("dada.txt", new Inode("dada.txt", 1, 1));
    }

    private int move_need = 1;// 假设移动一格磁盘块需要一个操作数
    private int rw_need = 2; // 假设读写一格磁盘块需要两个操作数

    private int ops_max = 100; // 一个时钟中断周期内能进行进行的最多操作数
    private int ops_cur; // 当前已经使用的操作数

    private int curMhead;// 当前的磁头位置

    public void update() {
        ops_cur = 0;
        // 检查当前是否有进程进行文件读写任务，若无，返回
        // 模拟磁盘进行读写操作
        while (ops_cur < ops_max) {
            if (rwqueue.isEmpty()) {
                break;
            }
            int isfinished = disk_read_write();
            // 若有文件读写任务完成，即有任务的剩余磁盘块归零
            if (isfinished != -1) {
                sendInterrupt(InterruptType.FileFinish, isfinished);
                // 发送中断系统调用FileFinish
                // 启动磁盘调度，进行下一个文件读写操作
            }
        }
    }

    private void sendInterrupt(InterruptType interrupt, Object... objs) {
        kernel.receiveInterrupt(new myInterrupt(interrupt, objs));
    }

    private int disk_read_write() {
        // 获得当前磁头位置
        // 磁头移向磁盘块读写队列中离磁头最近的磁盘块移动
        // queueEntry nextEntry = findNearEntry(curMhead);//找最近
        queueEntry nextEntry = rwqueue.peek();// FCFS
        if (nextEntry == null)
            return -1;
        int ops_move = Math.abs(curMhead - nextEntry.startBlock) * move_need;
        int ops_rw = nextEntry.blockSize * rw_need;
        int ops_need = ops_move + ops_rw;
        int ops_remain = ops_max - ops_cur;
        // 移动过程更新操作数
        // 如果磁头能移动到磁盘块
        // 尝试读写操作
        // 若能读写
        // 磁盘块对应的任务的剩余磁盘块减一
        if (ops_remain >= ops_need) {
            ops_cur += ops_need;
            curMhead = nextEntry.startBlock + nextEntry.blockSize;
            queueEntry out = rwqueue.poll();
            return out.isFinished;
        } else if (ops_remain > ops_move) {
            int rw = (ops_remain - ops_move) / 2;
            curMhead = nextEntry.startBlock + rw;
            nextEntry.startBlock += rw;
            nextEntry.blockSize -= rw;
            ops_cur = ops_max;
            return -1;
        } else {
            // 最大操作数不满足移动
            // 磁头改变位置
            ops_cur = ops_max;
            if (nextEntry.startBlock > curMhead) {
                curMhead += ops_remain;
            } else {
                curMhead -= ops_remain;
            }

            return -1;
        }
    }

    // public Inode getInode()
    public Inode findInode(String path) {
        String[] fileName = path.split("/");
        Inode curInode = root;
        if (!fileName[0].equals("filesystem")) {
            // 路径错误
            return null;
        }
        for (int i = 1; i < fileName.length; i++) {
            curInode = curInode.findChild(fileName[i]);
            if (curInode == null) {
                // 路径错误
                return null;
            }
        }
        return curInode;
    }

    // 更新空闲空间表，如果能合并则合并空闲空间
    public void updateSpaceTable(int start, int size) {
        int end = start + size;
        int mergeStart = -1, mergeEnd = -1;
        int newStart = start, newSize = size;
        if (start != 0 && !bitmap[start - 1]) {
            mergeEnd = start;
        }
        if (end != 1024 && !bitmap[end]) {
            mergeStart = end;
        }
        if (mergeStart == -1 && mergeEnd == -1) {
            spaceTable.put(newStart, newSize);
            return;
        }
        if (spaceTable.get(mergeStart) != null) {
            newSize += spaceTable.get(mergeStart);
            spaceTable.remove(mergeStart);
        }
        for (int key : spaceTable.keySet()) {
            if (key + spaceTable.get(key) == mergeEnd) {
                newSize += spaceTable.get(key);
                newStart -= spaceTable.get(key);
                spaceTable.remove(key);
                break;
            }
        }
        spaceTable.put(newStart, newSize);
    }

    public void freeUp(Inode curInode) {
        if (curInode.getType() == 1) {
            if (curInode.isStorageEmpty())
                return;
            Iterator<Map.Entry<Integer, Integer>> it = curInode.getStorage().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> curSpace = it.next();
                Arrays.fill(bitmap, curSpace.getKey(), curSpace.getKey() + curSpace.getValue(), false);
                // 更新spaceTable
                updateSpaceTable(curSpace.getKey(), curSpace.getValue());
            }
        } else if (curInode.getType() == 0) {
            HashMap<String, Inode> curDir = curInode.getDirectoryEntries();
            for (Inode i : curDir.values()) {
                freeUp(i);
            }
        }
    }

    // 以下parent_name是文件绝对地址
    public boolean touch(String parent_name, String file_name) {
        if (parent_name.isEmpty()) {
            // 路径为空
            return false;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);
        // 创建文件
        if (pInode == null) {
            // 路径错误
            return false;
        }
        if (checkRenameError(parent_name, file_name, 1)) {
            return false;
        }
        Inode newInode = new Inode(file_name, 1, 1);
        pInode.insertFileInDir(file_name, newInode);
        return true;
    }

    public boolean checkRenameError(String parent_name, String file_name, int type) {
        List<Inode> check = filelist(parent_name);
        for (Inode i : check) {
            if (i.getType() == type && i.getName().equals(file_name)) {
                return true;
            }
        }
        return false;
    }

    public boolean rm(String parent_name, String file_name) {
        if (parent_name.isEmpty()) {
            // 路径为空触发中断
            System.out.println("rm:路径为空");
            return false;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);
        // 删除文件
        if (pInode == null || pInode.findChild(file_name) == null) {
            // 路径错误触发中断
            System.out.println("rm:路径错误");
            return false;
        }
        if (ftable.isExist(parent_name + "/" + file_name)) {
            // 打开文件表中存在此文件，不能删除
            System.out.println("rm:打开文件表中存在此文件，不能删除");
            return false;
        }
        // 释放文件磁盘空间
        freeUp(pInode.findChild(file_name));
        pInode.deleteFileInDir(file_name);
        return true;
    }

    public boolean mkdir(String parent_name, String dir_name) {
        if (parent_name.isEmpty()) {
            // 路径为空
            System.out.println("路径为空");
            return false;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);
        // 创建目录
        if (pInode == null) {
            // 路径错误
            return false;
        }
        if (checkRenameError(parent_name, dir_name, 0)) {
            return false;
        }
        Inode newInode = new Inode(dir_name, 0, 1);
        pInode.insertFileInDir(dir_name, newInode);
        return true;
    }

    public boolean rmdir(String parent_name, String dir_name) {
        if (parent_name.isEmpty()) {
            // 路径为空触发中断
            return false;
        }
        // 寻找父目录
        Inode pInode = findInode(parent_name);

        if (pInode == null) {
            // 路径错误触发中断
            return false;
        }
        // 递归释放文件夹中所有文件的磁盘空间
        freeUp(pInode.findChild(dir_name));
        // 删除目录
        pInode.deleteFileInDir(dir_name);
        // 删除所有
        return true;
    }

    public int open(int pid, String path) {
        // 先默认都是读写模式打开
        return ftable.open(pid, path, 2);
    }

    public void close(int pid, int fd) {
        ftable.close(pid, fd);
    }

    public void write(int pid, int fd, int usage_size) {
        String path = ftable.findInodeByFd(fd);
        Inode file = findInode(path);
        if (file != null) {
            allocate(file, usage_size, pid);
        }
    }

    public void write1(int pid, int fd, String s) {
        String path = ftable.findInodeByFd(fd);
        Inode file = findInode(path);
        int usage_size = (s.length() + 3) / 4;
        int startBlock;
        char[] data;
        if (file != null) {
            startBlock = allocate1(file, usage_size, pid);
            data = s.toCharArray();
            int i = 0, curBlock = startBlock;
            for (char c : data) {
                if (i == 4) {
                    i = 0;
                    curBlock++;
                }
                block[curBlock].data[i] = c;
                i++;
            }
        }
    }

    public void read(int pid, int fd, int usage_size) {
        String path = ftable.findInodeByFd(fd);
        Inode file = findInode(path);
        if (file == null)
            return;
        // 默认从头读
        // 加入文件读写待完成表
        // 将文件对应的所有磁盘块，加入磁盘块读写队列
        Iterator<Map.Entry<Integer, Integer>> it = file.getStorage().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> curSpace = it.next();
            if (usage_size <= curSpace.getValue()) {
                rwqueue.offer(new queueEntry(file, 0, curSpace.getKey(), usage_size, pid));
                break;
            } else {
                usage_size -= curSpace.getValue();
                rwqueue.offer(new queueEntry(file, 0, curSpace.getKey(), curSpace.getValue(), -1));
            }
        }
    }

    public List<Inode> filelist(String path) {
        List<Inode> fl = new LinkedList<>();
        Inode curInode = findInode(path);
        Collection<Inode> values = curInode.getDirectoryEntries().values();
        for (Inode value : values) {
            fl.add(value);
        }
        return fl;
    }

    public String ls(String path, String option) {
        String res = "";
        List<Inode> fList = filelist(path);
        if (fList.size() == 0) {
            return "该目录下没有任何文件和文件夹";
        }
        int blank_size1 = 15;
        int blank_size2 = 13;
        if (option == null || option.equals("-l")) {
            int count = 0;
            for (Inode node : fList) {
                // 文件
                if (node.getType() == 1) {
                    res += "File-" + node.getName();
                    if (option != null) {
                        res += "-" + node.getImode();
                        for (int i = 0; i < blank_size1 - node.getName().length() - 2; i++) {
                            res += " ";
                        }
                    } else {
                        for (int i = 0; i < blank_size1 - node.getName().length(); i++) {
                            res += " ";
                        }
                    }

                } else {
                    res += "Folder-" + node.getName();
                    if (option != null) {
                        res += "-" + node.getImode();
                        for (int i = 0; i < blank_size2 - node.getName().length() - 2; i++) {
                            res += " ";
                        }
                    } else {
                        for (int i = 0; i < blank_size2 - node.getName().length(); i++) {
                            res += " ";
                        }
                    }
                }
                count++;
                if (count % 3 == 0) {
                    res += "\n";
                }
            }
        } else {
            return "未定义该选项,请检查该命令是否正确\n指令格式: ls (-l)";
        }
        return res;
    }

    public String cat(String path, String filename) {
        String res = "";
        Inode inode = findInode(path).getDirectoryEntries().get(filename);
        if (inode == null || inode.getType() == 0 || inode.getStorage().size() == 0)
            return res;
        for (Map.Entry<Integer, Integer> entry : inode.getStorage().entrySet()) {
            int start = entry.getKey();
            int bsize = entry.getValue();
            for (int i = start; i < start + bsize; i++) {
                for (int j = 0; j < 4; j++) {
                    res += block[i].data[j];
                }
            }
        }
        return res;
    }
}