package com.example.hello.myProcess;
// package com.example.hello.controller;

// import com.example.hello.myProcess.*;
// import com.example.hello.controller.SysConfig.Dispatch;
// import com.example.hello.controller.SysData.OSState;
// import com.example.hello.myInstrunction.Instruction;
// import com.example.hello.myInstrunction.Instruction.Type;

// public class CPU extends Thread {
//     SysData sysData;

//     int LastDispatch_Time;
//     int Current_Time;

//     public CPU(SysData sysData) {
//         this.sysData = sysData;
//     }

//     @Override
//     public void run() {
//         while (true) {
//             try {
//                 this.sysData.sem_os.acquire();
//                 // critical section
//                 if (this.sysData.Osmode == OSState.kernel) {
//                     this.sysData.sem_os.release();
//                     continue;
//                 }

//                 System.out.println("CPU running!");
//                 // CPU调度
//                 this.dispatch_process();
//                 PCB p = this.sysData.ProcessController.Selected_Process;

//                 boolean interupt_flag = false;
//                 // 模拟CPU取指令,执行指令的过程
//                 for (int i = p.pc; i < p.bursts.size() && interupt_flag == false; i++) {
//                     Instruction instruction = p.bursts.get(i);
//                     if (instruction.type == Type.C) {
//                         // 模拟执行CPU指令
//                         if (this.sysData.sysConfig.policy != Dispatch.Multilevel)
//                             Thread.sleep(instruction.args.cpu_time);
//                         else if (instruction.args.cpu_time > this.sysData.sysConfig.Multilevel_TimeSlice) {
//                             Thread.sleep(this.sysData.sysConfig.Multilevel_TimeSlice);
//                             // 产生时钟超时中断
//                         } else {
//                             Thread.sleep(instruction.args.cpu_time);
//                         }
//                     }
//                     // 模拟I/O指令
//                     else if (instruction.type == Type.K) {
//                         // 产生I/O中断
//                     }
//                     // ...其他指令

//                     /*
//                      * if(当前的中断队列不为空){
//                      * 保存现场,陷入kernel mode
//                      * this.sysData.Osmode = OSState.kernel;
//                      * this.sysData.sem_os.realease();
//                      * 第一版暂时处理为忙等待,后续可改成处理内存清理等任务
//                      * interupt_flag =true;
//                      * }
//                      */
//                 }

//                 if (interupt_flag == true) {
//                     continue;
//                 }

//                 this.sysData.sem_os.release();
//             }
//             // CPU调度选择一个进程
//             // 遍历选择进程的指令序列(CPU时间,I/O时间,读写文件)
//             // 同时在每条指令执行完轮询interrupt外部中断
//             // 如果检查到当前interrupt缓冲区存在中断请求,则保存现场后执行中断向量表中的对应中断处理函数
//             // 如果当前指令不是CPU时间,则保存现场,挂起当前执行进程,进行CPU调度
//             // 在处理中断的过程中,CPU进入sysData mode 处理完成后 回到 user mode
//             catch (InterruptedException e) {
//                 ;
//             }

//         }

//     }

//     // CPU 调度
//     void dispatch_process() {
//         switch (this.sysData.sysConfig.policy) {
//             case SysConfig.Dispatch.FCFS:
//                 FCFS();
//             case SysConfig.Dispatch.SJF:
//                 SJF();
//             case SysConfig.Dispatch.Priority_NonPreemptive:
//                 Priority_NonPreemptive();
//             case SysConfig.Dispatch.Priority_Preemptive:
//                 Priority_Preemptive(1);
//             case SysConfig.Dispatch.HRRN:
//                 HRRN();
//             case SysConfig.Dispatch.Multilevel:
//                 Multilevel(1);
//         }
//     }

//     // 采用FCFS调度进程
//     void FCFS() {
//         int size = this.sysData.ProcessController.queue.Ready_Queue.size();
//         // 如果waiting队列不为空,则将第一个进程选为下一个running进程
//         if (size > 0) {

//             this.sysData.ProcessController.Selected_Process = this.sysData.ProcessController.queue.Ready_Queue
//                     .getFirst();
//             this.sysData.ProcessController.queue.Ready_Queue.removeFirst();
//         }
//     }

//     <T extends Comparable<T>> int findIndexByMinValue(T[] array) {
//         int res = 0;
//         for (int i = 0; i < array.length; i++) {
//             if (array[i].compareTo(array[res]) < 0)
//                 res = i;
//         }
//         return res;
//     }

//     <T extends Comparable<T>> int findIndexByMaxValue(T[] array) {
//         int res = 0;
//         for (int i = 0; i < array.length; i++) {
//             if (array[i].compareTo(array[res]) > 0)
//                 res = i;
//         }
//         return res;
//     }

//     // 采用SJF调度进程(非抢占式)
//     void SJF() {
//         int size = this.sysData.ProcessController.queue.Ready_Queue.size();
//         // 如果waiting队列不为空,则以SJF策略选择下一个running进程
//         if (size > 0) {
//             // 计算ready队列中每个进程的剩余CPU执行时间
//             Integer[] remain_CPUburst = new Integer[size];
//             for (int i = 0; i < size; i++) {
//                 int cpu_burst = 0;
//                 PCB p = this.sysData.ProcessController.ProcessList.get(i);
//                 for (int j = p.pc; j < p.bursts.size(); j++) {
//                     if (p.bursts.get(j).type == Instruction.Type.C) {
//                         cpu_burst += p.bursts.get(j).args.cpu_time;
//                     }
//                 }
//                 remain_CPUburst[i] = cpu_burst;
//             }

//             // 找到CPUburst最小的PCB,更新queue
//             int SJF_index = findIndexByMinValue(remain_CPUburst);
//             this.sysData.ProcessController.Selected_Process = this.sysData.ProcessController.queue.Ready_Queue
//                     .get(SJF_index);
//             this.sysData.ProcessController.queue.Ready_Queue.remove(SJF_index);
//         }
//     }

//     // 采用非抢占式优先级调度
//     void Priority_NonPreemptive() {
//         int size = this.sysData.ProcessController.queue.Ready_Queue.size();
//         // 如果waiting队列不为空,则以SJF策略选择下一个running进程
//         if (size > 0) {
//             // 将所有process的priority复制到一个数组
//             Integer[] processpriority = new Integer[size];
//             for (int i = 0; i < size; i++) {
//                 processpriority[i] = this.sysData.ProcessController.ProcessList.get(i).priority;
//             }

//             // 找到priority最小的PCB,更新queue
//             int Pri_Npree_index = findIndexByMinValue(processpriority);
//             this.sysData.ProcessController.Selected_Process = this.sysData.ProcessController.queue.Ready_Queue
//                     .get(Pri_Npree_index);
//             this.sysData.ProcessController.queue.Ready_Queue.remove(Pri_Npree_index);
//         }
//     }

//     // 采用抢占式优先级调度(相比于非抢占式调度,抢占式调度在有新的进程加入ready队列时也会触发算法)
//     /*
//      * 第一种情况,type=1表示CPU执行完一个进程后的常规调度
//      * 第二种情况,type=2 可能是创建进程或者有进程从waiting队列移动到ready队列时触发
//      */
//     void Priority_Preemptive(int type) {
//         if (type == 1) {
//             Priority_NonPreemptive();
//         } else {
//             // 发生抢占
//             if (this.sysData.ProcessController.queue.Ready_Queue
//                     .getLast().priority < this.sysData.ProcessController.Selected_Process.priority) {
//                 // 挂起当前执行的进程(保存现场)
//                 this.sysData.ProcessController.suspendProcess();

//                 // 将当前被抢占的进程加入ready队列
//                 this.sysData.ProcessController.queue.Ready_Queue.add(this.sysData.ProcessController.Selected_Process);
//                 // 将抢占的进程设为当前执行的进程
//                 this.sysData.ProcessController.Selected_Process = this.sysData.ProcessController.queue.Ready_Queue
//                         .get(this.sysData.ProcessController.queue.Ready_Queue.size() - 2);
//                 // 将抢占的进程从ready队列中移除
//                 this.sysData.ProcessController.queue.Ready_Queue
//                         .remove(this.sysData.ProcessController.queue.Ready_Queue.size() - 2);
//             }

//         }

//     }

//     // 采用高响应比优先调度
//     void HRRN() {
//         int size = this.sysData.ProcessController.queue.Ready_Queue.size();
//         // 如果waiting队列不为空,则以HRRN策略选择下一个running进程
//         if (size > 0) {
//             Float[] HRRN = new Float[size];
//             for (int i = 0; i < size; i++) {
//                 PCB p = this.sysData.ProcessController.queue.Ready_Queue.get(i);
//                 int cpu_burst = 0;
//                 for (int j = p.pc; j < p.bursts.size(); j++) {
//                     if (p.bursts.get(j).type == Instruction.Type.C) {
//                         cpu_burst += p.bursts.get(j).args.cpu_time;
//                     }
//                 }
//                 HRRN[i] = (float) p.waiting_time / (float) cpu_burst;
//             }

//             int HRRN_index = findIndexByMaxValue(HRRN);
//             this.sysData.ProcessController.Selected_Process = this.sysData.ProcessController.queue.Ready_Queue
//                     .get(HRRN_index);
//             this.sysData.ProcessController.queue.Ready_Queue.remove(HRRN_index);

//         }
//     }

//     /*
//      * 采用多级队列调度(第一级采用非抢占优先级调度,第二级采用FCFS调度,
//      * 第一级执行时间超过Sysconfig的timeslice则加入第二级队列,
//      * 只有第一级队列为空的时候才能调用第二级队列的进程)
//      * type = 1 表示 CPU调度
//      * type = 2 表示 第一级优先级最高的进程占用CPU时间超过时间片调整多级队列
//      */
//     void Multilevel(int type) {
//         if (type == 1) {
//             int size1 = this.sysData.ProcessController.queue.Ready_Queue.size();
//             int size2 = this.sysData.ProcessController.queue.Second_Queue.size();
//             if (size1 > 0) {
//                 Priority_NonPreemptive();
//             } else if (size2 > 0) {
//                 this.sysData.ProcessController.Selected_Process = this.sysData.ProcessController.queue.Second_Queue
//                         .getFirst();
//                 this.sysData.ProcessController.queue.Second_Queue.removeFirst();
//             }
//         } else {
//             // 将当前正在进行的进程的执行CPU burst 减少一个timeslice;

//             // 将当前超时进程加入二级队列尾
//             this.sysData.ProcessController.queue.Second_Queue.add(this.sysData.ProcessController.Selected_Process);

//             // 更新当前执行的进程
//             this.Multilevel(1);
//         }
//     }
// }
