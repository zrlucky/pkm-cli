
package com.ZhangRuo.pkm.cli;

import com.ZhangRuo.pkm.cli.command.*; // 导入所有命令类
import com.ZhangRuo.pkm.controller.NoteController;
import com.ZhangRuo.pkm.controller.TagController;
import com.ZhangRuo.pkm.repository.JsonStorageService;
import com.ZhangRuo.pkm.repository.StorageService;
import com.ZhangRuo.pkm.service.ExportService;
import com.ZhangRuo.pkm.service.NoteService;
import com.ZhangRuo.pkm.service.TagService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;


/**
 * [重构后] 命令解析器和应用上下文。
 * 负责装配整个应用，管理主控循环(REPL)，并将命令分发给 CommandRegistry。
 */
public class CommandParser {

    private final Scanner scanner;
    private boolean isRunning;
    private final CommandRegistry commandRegistry;

    /**
     * 重构后的构造函数。
     * 职责：1. 装配所有服务和控制器。 2. 初始化命令并注册到 CommandRegistry。
     */
    public CommandParser() {
        // --- 1. 装配 Service 和 Controller (保留) ---
        StorageService storageService = new JsonStorageService();
        NoteService noteService = new NoteService(storageService);
        TagService tagService = new TagService(storageService);
        ExportService exportService = new ExportService();
        NoteController noteController = new NoteController(noteService, exportService);
        TagController tagController = new TagController(tagService);

        // --- 2. 初始化命令注册器和 REPL 组件 ---
        this.commandRegistry = new CommandRegistry();
        this.scanner = new Scanner(System.in);
        this.isRunning = true;

        // --- 3. 调用私有方法，完成所有命令的初始化和注册 ---
        initializeCommands(noteController, tagController);
    }

    /**
     * 初始化并注册所有可用命令。
     * 将所有依赖项通过参数传入，以注入到各个 Command 对象中。
     */
    private void initializeCommands(NoteController noteController, TagController tagController) {
        // 注册所有具体命令，并将它们需要的依赖注入进去
        commandRegistry.registerCommand(new NewCommand(noteController));
        commandRegistry.registerCommand(new ListCommand(noteController));
        commandRegistry.registerCommand(new ViewCommand(noteController));
        commandRegistry.registerCommand(new EditCommand(noteController));
        commandRegistry.registerCommand(new DeleteCommand(noteController));
        commandRegistry.registerCommand(new SearchCommand(noteController));
        commandRegistry.registerCommand(new ExportCommand(noteController));
        commandRegistry.registerCommand(new ExportAllCommand(noteController));

        commandRegistry.registerCommand(new TagCommand(tagController));
        commandRegistry.registerCommand(new UntagCommand(tagController));

        // 特殊命令，需要 CommandRegistry 或 CommandParser 自身的引用
        commandRegistry.registerCommand(new HelpCommand(commandRegistry));
        commandRegistry.registerCommand(new ExitCommand(this)); // 注入 CommandParser 自身

        // 注册别名
        commandRegistry.registerAlias("quit", "exit");
    }

    // ... parseArgs 和 startInteractiveMode 方法基本不变 ...

    public void parseArgs(String[] args) {
        if (args.length == 0) {
            startInteractiveMode();
        } else {
            executeCommand(String.join(" ", args));
        }
    }

    private void startInteractiveMode() {
        System.out.println("> 欢迎使用个人知识管理系统 (CLI版)");
        System.out.println("> 输入 'help' 查看可用命令\n");
        while (isRunning) {
            System.out.print("pkm> ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                executeCommand(input);
            }
        }
    }

    /**
     * [重构后] 执行命令的核心逻辑。
     * 不再使用 switch，而是委托给 CommandRegistry。
     */
    private void executeCommand(String commandLine) {
        // 1. 使用更健壮的正则表达式解析命令行，以支持带引号的参数
        String[] parts = parseCommandLine(commandLine);
        if (parts.length == 0) {
            return;
        }

        // 2. 获取命令名称和参数
        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        // 3. 从注册器中查找命令对象
        Command command = commandRegistry.getCommand(commandName);

        // 4. 如果找到，则执行；否则，打印错误信息
        if (command != null) {
            try {
                // 将解析好的参数传递给命令对象去执行
                command.execute(args);
            } catch (Exception e) {
                // 捕获所有潜在的异常，保证程序健壮性
                System.err.println("❌ 执行命令时出错: " + e.getMessage());
                // 打印该命令的正确用法
                command.printUsage();
            }
        } else {
            System.err.println("❌ 未知命令: '" + commandName + "'。输入 'help' 查看可用命令。");
        }
    }

    /**
     * [新增] 一个更健壮的命令行解析方法，能正确处理用双引号包围的参数。
     * @param commandLine 完整的命令行输入。
     * @return 分割后的命令和参数数组。
     */
    private String[] parseCommandLine(String commandLine) {
        List<String> parts = new ArrayList<>();
        // 这个正则表达式可以匹配用双引号包围的字符串，或者不含空格的普通字符串
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = regex.matcher(commandLine);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // 匹配到双引号内容
                parts.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // 匹配到单引号内容
                parts.add(matcher.group(2));
            } else {
                // 匹配到不含空格的普通内容
                parts.add(matcher.group());
            }
        }
        return parts.toArray(new String[0]);
    }

    // --- 辅助方法 (用于 ExitCommand 和测试) ---

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}






