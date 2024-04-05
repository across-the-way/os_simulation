package com.example.hello.myInstrunction;

public class Instruction {
    private InstructionType type;
    private Object[] arguments;

    public Instruction(InstructionType type, Object... arguments) {
        this.type = type;
        this.arguments = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            this.arguments[i] = arguments[i];
        }
    }

    public InstructionType getType() {
        return type;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void ModifyArgument(int index, Object newArgument) {
        this.arguments[index] = newArgument;
    }

    public String toString() {
        StringBuilder args = new StringBuilder();
        args.append(type.toString());
        for (Object arg : arguments) {
            args.append(" ").append(arg.toString());
        }
        return args.toString();
    }
}
