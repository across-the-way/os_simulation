package com.example.hello.interrupt;

public class Interrupt{
    public enum InterruptionCode {
        ILLEGAL_INSTRUCTION,        //0：非法指令中断
        CLOCK,                      //1：时钟中断
        IO_PRINTER,                 //2：IO（打印机）中断
        IO_KEYBOARD,                //3：IO（键盘）中断
        DISK_READ,                  //4：磁盘中断——读文件
        DISK_WRITE,                 //5：磁盘终端——写文件
        CREATE_PROCESS,             //6：创建进程
        TERMINATE_PROCESS,          //7：终止进程
        PAGE_FAULT                  //8：缺页中断
    }
    public void interrupt(int interruptNumber) {
        switch (interruptNumber) {
            case InterruptionCode.ILLEGAL_INSTRUCTION:
                handleInterruptII();
                break;
            case InterruptionCode.CLOCK:
                handleInterruptCLK();
                break;
            case InterruptionCode.IO_PRINTER:
                handleInterruptIOP();
                break;
            case InterruptionCode.IO_KEYBOARD:
                handleInterruptIOK();
                break;
            case InterruptionCode.DISK_READ:
                handleInterruptDR();
                break;
            case InterruptionCode.DISK_WRITE:
                handleInterruptDW();
                break;
            case InterruptionCode.CREATE_PROCESS:
                handleInterruptCP();
                break;
            case InterruptionCode.TERMINATE_PROCESS:
                handleInterruptTP();
                break;
            case InterruptionCode.PAGE_FAULT:
                handleInterruptPF();
                break;

            // 添加更多中断处理分支
            default:
                System.out.println("Unknown interrupt: " + interruptNumber);
        }
    }

    private void handleInterruptII() {
        // 处理中断号为0的中断，具体内容见中断向量表
    }

    private void handleInterruptCLK() {
        // 处理中断1
    }
    private void handleInterruptIOP() {
        // 处理中断2
    }
    private void handleInterruptIOK() {
        // 处理中断2
    }
    private void handleInterruptDR() {
        // 处理中断2
    }
    private void handleInterruptDW() {
        // 处理中断2
    }
    private void handleInterruptCP() {
        // 处理中断2
    }
    private void handleInterruptTP() {
        // 处理中断2
    }
    private void handleInterruptPF() {
        // 处理中断2
    }
}

