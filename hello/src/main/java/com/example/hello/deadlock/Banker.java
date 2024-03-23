package com.example.hello.deadlock;

import com.example.hello.process.*;
import java.util.*;

public class Banker {
    public Map<String, Integer> available;
    public Map<String, Integer>[] max;
    public Map<String, Integer>[] allocation;
    public Map<String, Integer>[] need;

    public List<PCB> pcblList;
    public boolean[] checkList;
    public List<PCB> safeList;

    /*
     * public static void main(String[] args) {
     * Map<String, Integer> available = new HashMap<>();
     * available.put("A", 3);
     * available.put("B", 3);
     * available.put("C", 2);
     * List<PCB> pcblist = new ArrayList<>();
     * 
     * PCB p1 = new PCB();
     * p1.maxresourceMap = new HashMap<>();
     * p1.maxresourceMap.put("A", 7);
     * p1.maxresourceMap.put("B", 5);
     * p1.maxresourceMap.put("C", 3);
     * p1.allocateresourceMap = new HashMap<>();
     * p1.allocateresourceMap.put("A", 0);
     * p1.allocateresourceMap.put("B", 1);
     * p1.allocateresourceMap.put("C", 0);
     * pcblist.add(p1);
     * 
     * PCB p2 = new PCB();
     * p2.maxresourceMap = new HashMap<>();
     * p2.maxresourceMap.put("A", 3);
     * p2.maxresourceMap.put("B", 2);
     * p2.maxresourceMap.put("C", 2);
     * p2.allocateresourceMap = new HashMap<>();
     * p2.allocateresourceMap.put("A", 2);
     * p2.allocateresourceMap.put("B", 0);
     * p2.allocateresourceMap.put("C", 0);
     * pcblist.add(p2);
     * 
     * PCB p3 = new PCB();
     * p3.maxresourceMap = new HashMap<>();
     * p3.maxresourceMap.put("A", 9);
     * p3.maxresourceMap.put("B", 0);
     * p3.maxresourceMap.put("C", 2);
     * p3.allocateresourceMap = new HashMap<>();
     * p3.allocateresourceMap.put("A", 3);
     * p3.allocateresourceMap.put("B", 0);
     * p3.allocateresourceMap.put("C", 2);
     * pcblist.add(p3);
     * 
     * PCB p4 = new PCB();
     * p4.maxresourceMap = new HashMap<>();
     * p4.maxresourceMap.put("A", 2);
     * p4.maxresourceMap.put("B", 2);
     * p4.maxresourceMap.put("C", 2);
     * p4.allocateresourceMap = new HashMap<>();
     * p4.allocateresourceMap.put("A", 2);
     * p4.allocateresourceMap.put("B", 1);
     * p4.allocateresourceMap.put("C", 1);
     * pcblist.add(p4);
     * 
     * PCB p5 = new PCB();
     * p5.maxresourceMap = new HashMap<>();
     * p5.maxresourceMap.put("A", 4);
     * p5.maxresourceMap.put("B", 3);
     * p5.maxresourceMap.put("C", 3);
     * p5.allocateresourceMap = new HashMap<>();
     * p5.allocateresourceMap.put("A", 0);
     * p5.allocateresourceMap.put("B", 0);
     * p5.allocateresourceMap.put("C", 2);
     * pcblist.add(p5);
     * 
     * Banker banker = new Banker(pcblist, available);
     * System.out.println(banker.isSafe());
     * }
     */

    @SuppressWarnings("unchecked")
    public Banker(List<PCB> pcblList, Map<String, Integer> available) {
        this.available = available;
        this.pcblList = pcblList;
        this.safeList = new ArrayList<PCB>();
        this.checkList = new boolean[this.pcblList.size()];
        this.max = (Map<String, Integer>[]) new Map[this.pcblList.size()];
        this.allocation = (Map<String, Integer>[]) new Map[this.pcblList.size()];
        this.need = (Map<String, Integer>[]) new Map[this.pcblList.size()];
        Arrays.fill(this.checkList, false);

        for (int i = 0; i < this.pcblList.size(); i++) {
            this.max[i] = this.pcblList.get(i).maxresourceMap;
            this.allocation[i] = this.pcblList.get(i).allocateresourceMap;
            this.need[i] = CalNeed(this.max[i], this.allocation[i]);
        }
    }

    // 银行家算法,检测是否存在一个安全队列
    public boolean isSafe() {
        boolean flag = true;
        while (flag == true && safeList.size() != pcblList.size()) {
            boolean lessFlag = false;
            for (int i = 0; i < pcblList.size(); i++) {
                if (!this.checkList[i]) {
                    if (less(need[i], available)) {
                        for (String t : need[i].keySet()) {
                            available.put(t, available.get(t) + allocation[i].get(t));
                        }
                        safeList.add(pcblList.get(i));
                        lessFlag = true;
                        this.checkList[i] = true;
                    }
                }
            }
            if (!lessFlag && safeList.size() != pcblList.size()) {
                flag = false;
            }
        }
        return flag;
    }

    boolean less(Map<String, Integer> need, Map<String, Integer> available) {
        boolean flag = true;
        for (String i : need.keySet()) {
            if (need.get(i) > available.get(i))
                flag = false;
        }
        return flag;
    }

    public static Map<String, Integer> CalNeed(Map<String, Integer> max, Map<String, Integer> allocate) {
        Map<String, Integer> need = new HashMap<String, Integer>();
        for (String i : max.keySet()) {
            need.put(i, max.get(i) - allocate.get(i));
        }
        return need;
    }
}
