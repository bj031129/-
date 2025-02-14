package com.example.server.util.command;

import org.springframework.util.StringUtils;

/**
 * 指令解析类,以空白符为标记进行切割字符串指令
 *
 * @author 曾佳宝
 */
public class CommandParse {

    public static String[] parseCommand(String command) {
        String[] args;
        if (StringUtils.isEmpty(command) || command.startsWith(" ")) {
            return null;
        }
        args = command.split("\\s+");
        args[0] = args[0].toUpperCase();
        return args;
    }
}
