package com.ZhangRuo.pkm.cli;

import com.ZhangRuo.pkm.controller.NoteController;
import com.ZhangRuo.pkm.controller.TagController;
import com.ZhangRuo.pkm.repository.JsonStorageService;
import com.ZhangRuo.pkm.repository.StorageService;
import com.ZhangRuo.pkm.service.ExportService;
import com.ZhangRuo.pkm.service.TagService;
import com.ZhangRuo.pkm.service.NoteService;

import java.util.Arrays;
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

    }

    /*
    * 启动交互式命令行模式（REPL：Read-Eval-Print_Loop）
    * */
    public void startInteractiveMode(){
        System.out.println("> 欢迎使用个人知识管理系统（CLI版）");
        System.out.println("> 输入’help‘查看可用命令");

        while(isRunning){
            System.out.print("pkm> ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()){
                executeCommand(input);
            }
        }
    }

    /*
    * 【解析器】
    * 解析单行命令字符串，并将其分发给调度器
    * 职责：只负责解析，不负责执行
    * */
    private void executeCommand(String input){
        //1.将输入字符串按照第一个空格分割成[命令]和[参数字符串]
        String[] parts = input.split("\\s+",2);
        String command = parts[0].toLowerCase();

        //2.准备好命令和参数
        String[] args;
        if ("new".equals(command)){
            //"new"命令的参数处理比较特殊，直接传递parts
            args = parts;
        }else{
            args = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];
        }

        //3.调用专门的分发方法
        dispatchCommand(command,args);

    }

    /*
    * 【分发器（Dispatcher）】
    * 根据命令字符串，调用对应的处理方法
    * 职责：只负责”switch“决策，不负责解析
    * @param command 解析出的小写命令（e.g. "list","view"）
    * @param parts 原始的、包含命令和参数的字符串数组
    * */

    private void dispatchCommand(String command, String[] parts) {
        // 提取参数部分，如果不存在则为空数组
        String[] args = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];

        switch (command) {
            case "new":
                // 对于 new 命令，我们可能需要包含引号的完整参数字符串
                handleNewCommand(parts.length > 1 ? parts[1] : "");
                break;
            case "list":
                handleListCommand(args);
                break;
            case "view":
                handleViewCommand(args);
                break;
            case "edit":
                handleEditCommand(parts.length > 1 ? parts[1]:"");
            case "delete":
                handleDeleteCommand(args);
                break;
            case "tag":
                handleTagCommand(args);
                break;
            case "untag":
                handleUntagCommand(args);
                break;
            case "search":
                handleSearchCommand(parts.length > 1 ? parts[1]:"");
                break;
            case "export":
                handleExportCommand(args);
                break;
            case "export-all":
                handleExportAllCommand(args);
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
        //调用Controller的方法，传入tagName（可能为null）
        noteController.listNotes(tagName);

    }

    private void handleViewCommand(String[] args) {
        if (args.length != 1) {
            System.err.println("❌ 参数错误! 用法: view <笔记ID>");
            return;
        }
        noteController.viewNoteById(args[0]);
    }


    /*
    * 处理edit命令
    * 解析出笔记ID和带引号的新内容
    * @param params edit 命令后面的所有参数字符串
    * */
    private void handleEditCommand(String params) {
        //格式为<ID><新内容>
        String[] parts = params.split("\"", 3);
        //parts[0] 应该是ID和一个空格
        //parts[1] 应该是新内容

        if (parts.length < 2  || parts[0].trim().isEmpty()) {
            System.err.println("❌ 参数错误! 用法: edit <笔记ID>\'<新内容>\'");
            return;
        }
        String id = parts[0].trim();
        String newContent = parts[1];

        noteController.editNote(id, newContent);

    }


    private void handleDeleteCommand(String[] args) {
        if (args.length != 1) {
            System.err.println("❌ 参数错误! 用法: delete <笔记ID>");
            return;
        }
        noteController.deleteNoteById(args[0]);
    }

    private void handleTagCommand(String[] args) {
        if (args.length != 2) {
            System.err.println("❌ 参数错误! 用法: tag <笔记ID> <标签名>");
            return;
        }
        tagController.addTagToNote(args[0], args[1]);
    }

    private void handleUntagCommand(String[] args) {
        if (args.length != 2) {
            System.err.println("❌ 参数错误! 用法: untag <笔记ID> <标签名>");
            return;
        }
        tagController.removeTagFromNote(args[0], args[1]);
    }

    /*
    * 处理search命令
    * 解析出带引号的关键词
    * @param params search 命令后面的所有参数字符串
    * */
    private void handleSearchCommand(String params) {
        //假设关键词用引号包围
        String[] parts = params.split("\"", 3);
        if (parts.length < 2) {
            System.err.println("❌ 参数错误! 用法: search \'<关键词>\'");
            return;
        }
        String keyword = parts[1];

        noteController.searchNote(keyword);
    }


    private void handleExportCommand(String[] args) {
        if (args.length != 3) {
            System.err.println("❌ 参数错误! 用法: export <笔记ID> <格式><路径>");
            return;
        }
        String id = args[0];
        String format = args[1];
        String path = args[2];
        noteController.exportNote(id, format, path);
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
        System.out.println("  search \"<关键词>\"         - 搜索标题或内容包含关键词的笔记");
        System.out.println("  export <笔记ID> <格式> <路径> - 导出单篇笔记");
        System.out.println("  export-all <格式> <路径> - 导出所有笔记");
        System.out.println("  exit                     - 退出程序");
        System.out.println("  help                     - 显示此帮助信息");
        System.out.println("----------------");
    }
}







