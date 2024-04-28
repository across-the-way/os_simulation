package com.example.hello.controller;

import com.example.hello.myInstrunction.Instruction;
import com.example.hello.myInstrunction.InstructionType;

import java.util.Random;

public class myTest {
    myKernel kernel;
    Random rand;

    public myTest(myKernel kernel) {
        this.kernel = kernel;
        this.rand = new Random();
    }

    public Instruction[] generateRandomCalculate(int line_num, int max_time) {
        Instruction[] program_part = new Instruction[line_num];
        for (int i = 0; i < line_num; i++) {
            program_part[i] = new Instruction(InstructionType.Calculate, rand.nextInt(1, max_time));
        }
        return program_part;
    }

    public Instruction[] packProgram(Instruction[] ... program_parts) {
        int len = program_parts.length;
        int[] index = new int[len];
        int[] boundary = new int[len];
        int program_line = 0;
        for (int i = 0; i < len; i++) {
            program_line += program_parts[i].length;
            index[i] = 0;
            boundary[i] = program_parts[i].length;
        }
        Instruction[] program = new Instruction[3 + program_line];
        program[0] = new Instruction(InstructionType.Memory, 4 * program_line + rand.nextInt(0, 100));
        program[1] = new Instruction(InstructionType.Priority, rand.nextInt(0, 100));

        int chosen_part = 0;
        for (int i = 0; i < program_line; i++) {
            chosen_part = rand.nextInt(0, len);
            if (index[chosen_part] < boundary[chosen_part]) {
                program[2 + i] = program_parts[chosen_part][index[chosen_part]];
                index[chosen_part]++;
            }
        }

        program[2 + program_line] = new Instruction(InstructionType.Exit);

        return program;
    }

    public void doTest() {
        if (rand.nextBoolean()) return;

        int max_line_num = kernel.getSysData().Test_Max_Line;
        int max_time = kernel.getSysData().Test_Max_Time;

        this.kernel.
                receiveInterrupt(
                        new myInterrupt(
                                InterruptType.SystemCall,
                                SystemCallType.ProcessNew,
                                packProgram(
                                        generateRandomCalculate(
                                                rand.nextInt(1, max_line_num),
                                                rand.nextInt(1, max_time))
                                )
                        )
                );
    }
}
