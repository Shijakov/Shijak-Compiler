package com.company.old.code_generator;

import java.util.List;

public class CommandRunner {
    public static void runCommand(StringBuilder sb, String command, List<String> arguments) {
        sb.append(command).append(" ").append(String.join(", ", arguments)).append("\n");
    }
}
