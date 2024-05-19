package com.example.hello.SysConfig;

import com.example.hello.myMemory.allocateStrategy;
import com.example.hello.myProcess.scheduleStrategy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SysData {
    // 系统时钟脉冲设置
    public int SystemPulse;

    // 系统统一指令长度为4Byte
    public int InstructionLength;

    // 多级队列调度时二级队列的最大存在时间限制
    public int Second_Queue_Threshold;

    // 长期调度时间片
    public int LongTermScale;

    // 长期调度的上下阈值
    public int LongTerm_CeilThreshold;
    public int LongTerm_FloorThreshold;

    // 中期调度时间片
    public int MidTermScale;

    // 中期调度的上下阈值
    public double MidTerm_CeilThreshold;
    public double MidTerm_FloorThreshold;

    // 调度算法
    public scheduleStrategy CPUstrategy;

    // 内存分配算法
    public allocateStrategy MMstrategy;

    // 内存大小
    public int Memory_Size;

    // 页大小
    public int Page_Size;

    // 设备数量
    public int Printer_Number;
    public int Keyboard_Number;
    public int OtherDevice_Number;

    // 可用资源数
    public Map<String, Integer> availableMap;

    // 测试相关
    public int Test_Max_Line;
    public int Test_Max_Time;

    public SysData() {
        // 单位毫秒
        SystemPulse = 1000;

        InstructionLength = 4;

        Second_Queue_Threshold = 1000;

        LongTermScale = 100;
        MidTermScale = 100;

        CPUstrategy = scheduleStrategy.MLFQ;

        LongTerm_CeilThreshold = 20;
        LongTerm_FloorThreshold = 3;

        MidTerm_CeilThreshold = 0.8;
        MidTerm_FloorThreshold = 0.3;

        MMstrategy = allocateStrategy.LRU;

        Memory_Size = 4096;
        Page_Size = 512;

        Printer_Number = 3;
        Keyboard_Number = 3;
        OtherDevice_Number = 10;

        InitResourcemap();

        Test_Max_Line = 10;
        Test_Max_Time = 10;
    }

    public SysData(String configFilePath) {
        SystemPulse = 1000;

        InstructionLength = 4;

        Second_Queue_Threshold = 1000;

        LongTermScale = 100;
        MidTermScale = 100;

        CPUstrategy = scheduleStrategy.MLFQ;

        LongTerm_CeilThreshold = 20;
        LongTerm_FloorThreshold = 3;

        MidTerm_CeilThreshold = 0.8;
        MidTerm_FloorThreshold = 0.3;

        MMstrategy = allocateStrategy.LRU;

        Memory_Size = 1536;
        Page_Size = 512;

        Printer_Number = 3;
        Keyboard_Number = 3;
        OtherDevice_Number = 10;

        if (!configFilePath.isEmpty()) {
            loadConfigFromFile(configFilePath);
        }

        InitResourcemap();

        Test_Max_Line = 10;
        Test_Max_Time = 10;
    }

    private void loadConfigFromFile(String configFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length != 2) {
                    continue;
                }
                String key = parts[0].trim();
                String value = parts[1].trim();
                switch (key) {
                    case "SystemPulse":
                        SystemPulse = Integer.parseInt(value);
                        break;
                    case "InstructionLength":
                        InstructionLength = Integer.parseInt(value);
                        break;
                    case "Second_Queue_Threshold":
                        Second_Queue_Threshold = Integer.parseInt(value);
                        break;
                    case "LongTermScale":
                        LongTermScale = Integer.parseInt(value);
                        break;
                    case "MidTermScale":
                        MidTermScale = Integer.parseInt(value);
                        break;
                    case "LongTerm_CeilThreshold":
                        LongTerm_CeilThreshold = Integer.parseInt(value);
                        break;
                    case "LongTerm_FloorThreshold":
                        LongTerm_FloorThreshold = Integer.parseInt(value);
                        break;
                    case "MidTerm_CeilThreshold":
                        MidTerm_CeilThreshold = Double.parseDouble(value);
                        break;
                    case "MidTerm_FloorThreshold":
                        MidTerm_FloorThreshold = Double.parseDouble(value);
                        break;
                    case "Memory_Size":
                        Memory_Size = Integer.parseInt(value);
                        break;
                    case "Page_Size":
                        Page_Size = Integer.parseInt(value);
                        break;
                    case "Printer_Number":
                        Printer_Number = Integer.parseInt(value);
                        break;
                    case "Keyboard_Number":
                        Keyboard_Number = Integer.parseInt(value);
                        break;
                    case "OtherDevice_Number":
                        OtherDevice_Number = Integer.parseInt(value);
                        break;
                    default:
                        System.out.println("invalid config keyword : " + key);
                        break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void InitResourcemap() {
        this.availableMap = new HashMap<>();
        this.availableMap.put("printer", Printer_Number * 5);
        this.availableMap.put("keyboard", Keyboard_Number * 5);
        this.availableMap.put("file", 1000);
        this.availableMap.put("device..", OtherDevice_Number * 5);
    }

    public boolean AvailableResource(String ResourceName) {
        if (!this.availableMap.containsKey(ResourceName))
            return false;
        if (this.availableMap.get(ResourceName) <= 0)
            return false;
        else
            return true;
    }

    public void AllocateResource(String ResourceName) {
        if (!this.availableMap.containsKey(ResourceName))
            return;
        if (this.availableMap.get(ResourceName) < 1)
            return;
        this.availableMap.put(ResourceName, this.availableMap.get(ResourceName) - 1);
    }

    public void FreeResource(String ResourceName) {
        if (!this.availableMap.containsKey(ResourceName))
            return;
        this.availableMap.put(ResourceName, this.availableMap.get(ResourceName) + 1);
    }

    public void MountDevice(String ResourceName) {
        if (!this.availableMap.containsKey(ResourceName))
            this.availableMap.put("device..", this.availableMap.get("device..") + 5);
        else
            this.availableMap.put(ResourceName, this.availableMap.get(ResourceName) + 5);
    }

    public void ReleaseDevice(String ResourceName) {
        if (!this.availableMap.containsKey(ResourceName))
            return;
        this.availableMap.put(ResourceName, this.availableMap.get(ResourceName) + 5);
    }
}
