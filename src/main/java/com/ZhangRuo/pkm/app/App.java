package com.ZhangRuo.pkm.app;

import com.ZhangRuo.pkm.cli.CommandParser;

/**
 * [表现层] 应用程序的唯一主入口。
 * 职责：创建并启动命令解析器。
 */
public class App {

    /**
     * Java 程序的主方法。
     * @param args 命令行参数。
     */
    public static void main(String[] args) {
        // 1. 创建整个应用的“总指挥” -> CommandParser
        CommandParser parser = new CommandParser();

        // 2. 将命令行参数交给 parser 处理，由它决定启动模式
        parser.parseArgs(args);

        // 3. (可选但重要) 在程序结束时关闭资源，例如 CommandParser 中的 Scanner
        parser.close();
    }
}
