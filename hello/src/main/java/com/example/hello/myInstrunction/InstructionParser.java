package com.example.hello.myInstrunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstructionParser {

    public static List<Instruction> parseInstructions(String filePath) {
        List<Instruction> instructions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Instruction instruction = parseInstruction(line);
                if (instruction != null) {
                    instructions.add(instruction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return instructions;
    }

    private static Instruction parseInstruction(String line) {
        String[] parts = line.split("\\s+"); // 使用空白字符作为分隔符
        if (parts.length < 2) {
            System.err.println("Invalid instruction format: " + line);
            return null;
        }

        String typeString = parts[0];
        InstructionType type;
        try {
            type = InstructionType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid instruction type: " + typeString);
            return null;
        }

        Object[] arguments = new Object[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            arguments[i - 1] = parseArgument(type, i, parts[i]);
        }

        return new Instruction(type, arguments);
    }

    private static Object parseArgument(InstructionType type, int index, String argString) {
        if (index == 1) {
            switch (type) {
                case Memory:  
                case AccessMemory:
                case Priority:
                case Calculate:
                case Printer:
                case Keyboard:
                    return Integer.valueOf(argString);
                default: break;
            }
        } else {
            switch (type) {
                case WriteFile:
                case ReadFile:
                    return Integer.valueOf(argString);
                default: break;
            }
        }
        return argString;
    }

    public static void main(String[] args) {
        List<Instruction> instructions = parseInstructions("path/to/your/codefile.txt");
        // 现在可以使用解析出的指令列表进行后续处理
        for (Instruction instruction : instructions) {
            System.out.println(instruction.getType() + ": " + Arrays.toString(instruction.getArguments()));
        }
    }
}