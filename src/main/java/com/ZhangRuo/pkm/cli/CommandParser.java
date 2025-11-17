
package com.ZhangRuo.pkm.cli;


import com.ZhangRuo.pkm.cli.command.*; // 导入所有命令类
import com.ZhangRuo.pkm.controller.NoteController;
import com.ZhangRuo.pkm.controller.TagController;
import com.ZhangRuo.pkm.repository.JsonStorageService;
import com.ZhangRuo.pkm.repository.StorageService;
import com.ZhangRuo.pkm.service.ExportService;
import com.ZhangRuo.pkm.service.NoteService;
import com.ZhangRuo.pkm.service.TagService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * [最终重构版] 命令解析器和应用上下文。
 * 职责：1. 装配服务和控制器。2. 创建并配置自动扫描的 CommandRegistry。
 *      3. 管理主控循环(REPL)。4. 将命令分发给 CommandRegistry 执行。
 */
public class CommandParser {

    private final Scanner scanner;
    private boolean isRunning;
    private final CommandRegistry commandRegistry;

    /**
     * 终极版构造函数。
     */
    public CommandParser() {
        // --- 1. 创建命令注册器（它会自动扫描并注册所有命令） ---
        this.commandRegistry = new CommandRegistry();

        // --- 2. 手动进行依赖注入 ---
        // 这是将 Service/Controller 注入到被自动发现的 Command 实例中的过程
        setupCommandDependencies();

        // --- 3. 初始化 REPL 组件 ---
        this.scanner = new Scanner(System.in);
        this.isRunning = true;

        System.out.println("\n✅ 命令系统初始化完成，共加载了 " + commandRegistry.getCommandCount() + " 个命令。");
    }

    /**
     * 【核心】为所有需要依赖的命令，执行 Setter 注入。
     */
    private void setupCommandDependencies() {
        // --- 1. 创建 Service 和 Controller 实例 (只创建一次) ---
        StorageService storageService = new JsonStorageService();
        NoteService noteService = new NoteService(storageService);
        TagService tagService = new TagService(storageService);
        ExportService exportService = new ExportService();
        NoteController noteController = new NoteController(noteService, exportService);
        TagController tagController = new TagController(tagService);

        // --- 2. 遍历所有已自动注册的命令，按需注入依赖 ---
        for (Command command : commandRegistry.getAllCommands()) {
            if (command instanceof NewCommand) {
                ((NewCommand) command).setNoteController(noteController);
            } else if (command instanceof ListCommand) {
                ((ListCommand) command).setNoteController(noteController);
            } else if (command instanceof ViewCommand) {
                ((ViewCommand) command).setNoteController(noteController);
            } else if (command instanceof EditCommand) {
                ((EditCommand) command).setNoteController(noteController);
            } else if (command instanceof DeleteCommand) {
                ((DeleteCommand) command).setNoteController(noteController);
            } else if (command instanceof SearchCommand) {
                ((SearchCommand) command).setNoteController(noteController);
            } else if (command instanceof ExportCommand) {
                ((ExportCommand) command).setNoteController(noteController);
            } else if (command instanceof ExportAllCommand) {
                ((ExportAllCommand) command).setNoteController(noteController);
            } else if (command instanceof TagCommand) {
                ((TagCommand) command).setTagController(tagController);
            } else if (command instanceof UntagCommand) {
                ((UntagCommand) command).setTagController(tagController);
            } else if (command instanceof StatisticsCommand) {
                ((StatisticsCommand) command).setNoteService(noteService);
                ((StatisticsCommand) command).setTagService(tagService);
            } else if (command instanceof HelpCommand) {
                // 注入 CommandRegistry 自身
                ((HelpCommand) command).setCommandRegistry(commandRegistry);
            } else if (command instanceof ExitCommand) {
                // 注入 CommandParser 自身
                ((ExitCommand) command).setCommandParser(this);
            }
        }
    }

    // ... parseArgs, startInteractiveMode, executeCommand, parseCommandLine 等方法保持不变 ...

    public void parseArgs(String[] args) {
        if (args.length == 0) {
            startInteractiveMode();
        } else {
            executeCommand(String.join(" ", args));
        }
    }

    private void startInteractiveMode() {
        System.out.println("\n> 欢迎使用个人知识管理系统 (CLI版)");
        System.out.println("> 输入 'help' 查看可用命令\n");
        while (isRunning) {
            System.out.print("pkm> ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                executeCommand(input);
            }
        }
    }

    private void executeCommand(String commandLine) {
        String[] parts = parseCommandLine(commandLine);
        if (parts.length == 0) return;

        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = commandRegistry.getCommand(commandName);

        if (command != null) {
            try {
                command.execute(args);
            } catch (Exception e) {
                System.err.println("❌ 执行命令时出错: " + e.getMessage());
                command.printUsage();
            }
        } else {
            System.err.println("❌ 未知命令: '" + commandName + "'。输入 'help' 查看可用命令。");
        }
    }

    private String[] parseCommandLine(String commandLine) {
        List<String> parts = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = regex.matcher(commandLine);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
            } else {
                parts.add(matcher.group());
            }
        }
        return parts.toArray(new String[0]);
    }

    // --- 辅助方法 ---
    public void setRunning(boolean running) { this.isRunning = running; }
    public CommandRegistry getCommandRegistry() { return commandRegistry; }
    public void close() { if (scanner != null) scanner.close(); }
}