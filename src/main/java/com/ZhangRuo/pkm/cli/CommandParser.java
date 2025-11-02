package com.ZhangRuo.pkm.cli;

import com.ZhangRuo.pkm.controller.NoteController;
import com.ZhangRuo.pkm.controller.TagController;
import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.repository.JsonStorageService;
import com.ZhangRuo.pkm.repository.StorageService;
import com.ZhangRuo.pkm.service.ExportService;
import com.ZhangRuo.pkm.service.TagService;
import com.ZhangRuo.pkm.service.NoteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/*
* [表现层]
* 核心中的核心：命令解析器和分发器
* 负责装配整个应用，并管理主控循环（REPL）
* */

public class CommandParser {

    //--- 依赖的控制器 ---
    private final NoteController noteController;
    private final TagController tagController;

    //--- REPL相关的状态 ---
    private final Scanner scanner;
    private boolean isRunning;

    private List<Note> lastListedNotes;//用于缓存上一次list或search的结果


    /**
     * [新增] 程序的总入口方法。
     * 根据传入的命令行参数决定启动模式。
     * @param args 来自 main 方法的命令行参数。
     */
    public void parseArgs(String[] args) {
        if (args.length == 0) {
            // 如果没有提供任何参数，则启动交互模式
            startInteractiveMode();
        } else {
            // 如果提供了参数，则将它们拼接成一个命令字符串并直接执行
            String commandLine = String.join(" ", args);
            executeCommand(commandLine);
        }
    }



    /*
    * [对应"构造函数正确装配依赖"]
    * 构造函数负责"装配"整个应用程序的依赖关系
    * 这是一个典型的依赖注入(DI)容器的简化实现
    * */
    public CommandParser(){
        //1.从底层开始创建：数据持久层
        StorageService storageService = new JsonStorageService();

        //2.创建业务逻辑层：并注入其依赖
        NoteService noteService = new NoteService(storageService);
        TagService tagService = new TagService(storageService);

        ExportService exportService = new ExportService();

        //3.创建控制器层，并注入其依赖
        //注意：TagController 可能也需要NoteService来获取笔记标题等信息
        this.noteController = new NoteController(noteService,exportService);
        this.tagController = new TagController(tagService);

        //4.初始化REPL组件
        this.scanner = new Scanner(System.in);
        this.isRunning = true;

        this.lastListedNotes = new ArrayList<>();

    }

    /*
    * 启动交互式命令行模式（REPL：Read-Eval-Print_Loop）
    * */
    public void startInteractiveMode(){
        System.out.println("> 欢迎使用个人知识管理系统（CLI版）");
        System.out.println("> 输入 help 查看可用命令");

        while(isRunning){
            System.out.print("pkm> ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()){
                executeCommand(input);
            }
        }
    }

    /**
     * 【解析器】
     * 解析单行命令字符串，并将其分发给调度器。
     * 职责：只负责解析，不负责执行。
     */
    private void executeCommand(String input) {
        // 1. 将输入字符串按照第一个空格分割成 [命令] 和 [可能存在的参数字符串]
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();

        // 2. 直接调用分发器，把最原始的 parts 数组传过去，让分发器自己决定怎么用
        dispatchCommand(command, parts);
    }

    /**
     * 【分发器 (Dispatcher)】
     * 根据命令字符串，调用对应的处理方法。
     * 职责：负责 "switch" 决策，并为不同的 handle 方法准备正确的参数。
     * @param command 解析出的小写命令 (e.g., "list", "view")
     * @param parts   原始的、包含命令和参数的字符串数组 (e.g., ["view", "1"])
     */
    private void dispatchCommand(String command, String[] parts) {
        // 为那些需要 "arg1 arg2 ..." 格式的 handle 方法准备参数
        String[] simpleArgs = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];
        // 为那些需要完整参数字符串的 handle 方法准备参数
        String fullParams = (parts.length > 1) ? parts[1] : "";

        switch (command) {
            case "new":
                handleNewCommand(fullParams);
                break;
            case "list":
                handleListCommand(simpleArgs);
                break;
            case "view":
                handleViewCommand(simpleArgs);
                break;
            case "edit":
                handleEditCommand(fullParams);
                break;
            case "delete":
                handleDeleteCommand(simpleArgs);
                break;
            case "tag":
                handleTagCommand(simpleArgs);
                break;
            case "untag":
                handleUntagCommand(simpleArgs);
                break;
            case "search":
                handleSearchCommand(fullParams);
                break;
            case "export":
                handleExportCommand(simpleArgs);
                break;
            case "export-all":
                handleExportAllCommand(simpleArgs);
                break;
            case "exit":
                handleExitCommand();
                break;
            case "help":
                printHelp();
                break;
            default:
                System.err.println("❌ 未知命令: '" + command + "'。输入 'help' 查看可用命令。");
                break;
        }
    }

    /**
     * 关闭资源，例如 Scanner。
     */
    public void close() {
        scanner.close();
    }




    // --- 私有的 handle...() 方法，负责参数校验和调用 Controller ---
    private void handleNewCommand(String params) {
        // 一个简单的参数解析，假设标题和内容用双引号包围
        String[] parts = params.split("\"", 4);
        if (parts.length < 3) {
            System.err.println("❌ 参数错误! 用法: new \"<标题>\" \"<内容>\"");
            return;
        }
        String title = parts[1];
        String content = parts[3];
        noteController.createNote(title, content);
    }

    /*
    * 处理list命令
    * 检查是否存在 --tag 参数
    * @param args list 命令后面的所有参数
    * */
    private void handleListCommand(String[] args) {
        String tagName = null;
        //检查参数是否是”--tag“ 并且后面还跟着一个标签名
        if (args.length == 2 && "--tag".equals(args[0])) {
            tagName = args[1];
        }else if (args.length > 0){
            //如果有其他无法识别的参数，打印错误信息
            System.err.println("❌ 参数错误! 用法: list 或 list --tag<标签名>");
            return;

        }
        //接受返回值并存入缓存
        this.lastListedNotes = noteController.listNotes(tagName);

    }


    private void handleViewCommand(String[] args) {
        if (args.length != 1) {
            System.err.println("❌ 参数错误! 用法: view <短ID>");
            return;
        }

        try {
            // 1. 尝试将输入解析为短ID (数字)
            int displayId = Integer.parseInt(args[0]);

            //如果缓存是空的，就主动执行一次list来填充它
            if (lastListedNotes.isEmpty()) {
                System.out.println("ℹ️  首次操作，正在刷新笔记列表...");
                handleListCommand(new String[0]); // 调用 list 命令的处理器
            }

            // 2. 检查短ID是否有效
            if (displayId > 0 && displayId <= lastListedNotes.size()) {
                // 3. 从缓存中获取真实ID
                String realId = lastListedNotes.get(displayId - 1).getId();
                noteController.viewNoteById(realId);
            } else {
                System.err.println("❌ 错误: 无效的短ID '" + displayId + "'。请从下面的列表选择。");
                // 如果ID无效，再次打印列表，方便用户选择
                handleListCommand(new String[0]);
            }
        } catch (NumberFormatException e) {
            // 4. 如果用户输入的不是数字，我们仍然可以尝试把它当作UUID来处理 (兼容老用法)
            System.out.println("ℹ️  尝试将输入作为完整ID进行查找...");
            noteController.viewNoteById(args[0]);
        }
    }


    /*
    * 处理edit命令
    * 解析出笔记ID和带引号的新内容
    * @param params edit 命令后面的所有参数字符串
    * */
    private void handleEditCommand(String params) {
        String[] parts = params.split("\"", 3);
        if (parts.length < 2 || parts[0].trim().isEmpty()) {
            System.err.println("❌ 参数错误! 用法: edit <短ID 或 完整ID> \"<新内容>\"");
            return;
        }

        String idArg = parts[0].trim();
        String newContent = parts[1];

        try {
            int displayId = Integer.parseInt(idArg);

            // 【智能填充】
            if (lastListedNotes.isEmpty()) {
                System.out.println("ℹ️  首次操作ID，正在刷新笔记列表...");
                handleListCommand(new String[0]);
            }

            if (displayId > 0 && displayId <= lastListedNotes.size()) {
                String realId = lastListedNotes.get(displayId - 1).getId();
                noteController.editNote(realId, newContent);
                lastListedNotes.clear(); // 清空缓存
            } else {
                System.err.println("❌ 错误: 无效的短ID '" + displayId + "'。");
            }
        } catch (NumberFormatException e) {
            noteController.editNote(idArg, newContent);
        }
    }


    private void handleDeleteCommand(String[] args) {
        if (args.length != 1) {
            System.err.println("❌ 参数错误! 用法: delete <短ID 或 完整ID>");
            return;
        }

        try {
            int displayId = Integer.parseInt(args[0]);

            // 【智能填充】
            if (lastListedNotes.isEmpty()) {
                System.out.println("ℹ️  首次操作ID，正在刷新笔记列表...");
                handleListCommand(new String[0]);
            }

            if (displayId > 0 && displayId <= lastListedNotes.size()) {
                String realId = lastListedNotes.get(displayId - 1).getId();
                noteController.deleteNoteById(realId);
                // 操作成功后，缓存可能已过时，清空它以便下次重新加载
                lastListedNotes.clear();
            } else {
                System.err.println("❌ 错误: 无效的短ID '" + displayId + "'。");
            }
        } catch (NumberFormatException e) {
            noteController.deleteNoteById(args[0]);
        }
    }

    private void handleTagCommand(String[] args) {
        if (args.length != 2) {
            System.err.println("❌ 参数错误! 用法: tag <短ID 或 完整ID> <标签名>");
            return;
        }
        String idArg = args[0];
        String tagName = args[1];

        try {
            int displayId = Integer.parseInt(idArg);

            // 【智能填充】
            if (lastListedNotes.isEmpty()) {
                System.out.println("ℹ️  首次操作ID，正在刷新笔记列表...");
                handleListCommand(new String[0]);
            }

            if (displayId > 0 && displayId <= lastListedNotes.size()) {
                String realId = lastListedNotes.get(displayId - 1).getId();
                tagController.addTagToNote(realId, tagName);
                lastListedNotes.clear(); // 清空缓存
            } else {
                System.err.println("❌ 错误: 无效的短ID '" + displayId + "'。");
            }
        } catch (NumberFormatException e) {
            tagController.addTagToNote(idArg, tagName);
        }
    }



    private void handleUntagCommand(String[] args) {
        if (args.length != 2) {
            System.err.println("❌ 参数错误! 用法: untag <短ID 或 完整ID> <标签名>");
            return;
        }
        String idArg = args[0];
        String tagName = args[1];

        try {
            int displayId = Integer.parseInt(idArg);

            // 【智能填充】
            if (lastListedNotes.isEmpty()) {
                System.out.println("ℹ️  首次操作ID，正在刷新笔记列表...");
                handleListCommand(new String[0]);
            }

            if (displayId > 0 && displayId <= lastListedNotes.size()) {
                String realId = lastListedNotes.get(displayId - 1).getId();
                tagController.removeTagFromNote(realId, tagName);
                lastListedNotes.clear(); // 清空缓存
            } else {
                System.err.println("❌ 错误: 无效的短ID '" + displayId + "'。");
            }
        } catch (NumberFormatException e) {
            tagController.removeTagFromNote(idArg, tagName);
        }
    }


    /*
    * 处理search命令
    * 解析出带引号的关键词
    * @param params search 命令后面的所有参数字符串
    * */
// --- 这是修改后的 handleSearchCommand 方法 ---

    private void handleSearchCommand(String params) {
        if (params == null || params.isBlank()) {
            System.err.println("❌ 参数错误! 用法: search <关键词> 或 search \"<带空格的关键词>\"");
            return;
        }

        String keyword;
        // 检查参数是否以双引号开头和结尾
        if (params.startsWith("\"") && params.endsWith("\"")) {
            // 如果是，就提取引号内部的内容
            // 使用 substring 去掉首尾的双引号
            keyword = params.substring(1, params.length() - 1);
        } else {
            // 如果没有引号，就把整个参数作为关键词
            // (这种方式只支持不含空格的单个关键词)
            keyword = params;
        }

        if (keyword.isEmpty()) {
            System.err.println("❌ 参数错误! 关键词不能为空。");
            return;
        }

        noteController.searchNote(keyword);
    }



    private void handleExportCommand(String[] args) {
        if (args.length != 3) {
            System.err.println("❌ 参数错误! 用法: export <短ID 或 完整ID> <格式> <路径>");
            return;
        }

        String idArg = args[0];
        String format = args[1];
        String path = args[2];

        try {
            // 1. 尝试将ID参数解析为短ID (数字)
            int displayId = Integer.parseInt(idArg);

            // 2. 【智能填充】如果缓存为空，主动执行 list
            if (lastListedNotes.isEmpty()) {
                System.out.println("ℹ️  首次操作ID，正在刷新笔记列表...");
                handleListCommand(new String[0]); // 模拟执行 "list"
            }

            // 3. 检查短ID是否有效
            if (displayId > 0 && displayId <= lastListedNotes.size()) {
                // 4. 从缓存中获取真实ID
                String realId = lastListedNotes.get(displayId - 1).getId();
                // 5. 调用 Controller 时，传入的是真实ID
                noteController.exportNote(realId, format, path);
            } else {
                System.err.println("❌ 错误: 无效的短ID '" + displayId + "'。");
            }
        } catch (NumberFormatException e) {
            // 6. 如果用户输入的不是数字，我们仍然可以尝试把它当作UUID来处理 (兼容老用法)
            System.out.println("ℹ️  尝试将输入作为完整ID进行查找...");
            noteController.exportNote(idArg, format, path);
        }
    }


    /**
     * 处理 export-all 命令。
     * 解析出格式和路径。
     * @param args export-all 命令后面的所有参数。
     */
    private void handleExportAllCommand(String[] args) {
        if (args.length != 2) {
            System.err.println("❌ 参数错误! 用法: export-all <格式> <路径>");
            return;
        }
        String format = args[0];
        String path = args[1];
        noteController.exportAllNotes(format, path);
    }




    private void handleExitCommand() {
        this.isRunning = false;
        System.out.println("👋 再见!");
    }

    private void printHelp() {
        System.out.println("--- 可用命令 ---");
        System.out.println("  new \"<标题>\" \"<内容>\"   - 创建一篇新笔记");
        System.out.println("  list                     - 列出所有笔记");
        System.out.println("  view <笔记ID>            - 查看笔记详情");
        System.out.println("  edit <笔记ID>\'<新内容>\'  - 编辑一篇笔记的内容");
        System.out.println("  delete <笔记ID>          - 删除一篇笔记");
        System.out.println("  tag <笔记ID> <标签名>    - 为笔记添加标签");
        System.out.println("  untag <笔记ID> <标签名>  - 为笔记移除标签");
        System.out.println("  search <关键词>         - 搜索标题或内容包含关键词的笔记");
        System.out.println("  export <笔记ID> <格式> <路径> - 导出单篇笔记");
        System.out.println("  export-all <格式> <路径> - 导出所有笔记");
        System.out.println("  exit                     - 退出程序");
        System.out.println("  help                     - 显示此帮助信息");
        System.out.println("----------------");
    }
}







