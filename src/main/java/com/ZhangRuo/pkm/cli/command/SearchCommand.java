package com.ZhangRuo.pkm.cli.command;

import com.ZhangRuo.pkm.controller.NoteController;



/**
 * [命令模式] 搜索笔记的具体命令实现。
 * 负责解析 "search" 命令的参数，并调用 NoteController 来执行搜索。
 */
@CliCommand("search") // 1. 添加注解
public class SearchCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private NoteController noteController;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public SearchCommand() {
        super("search", "搜索标题或内容中包含关键词的笔记");
    }

    /**
     * 4. 新增公共 Setter 方法，用于依赖注入。
     *
     * @param noteController 笔记控制器的一个实例。
     */
    public void setNoteController(NoteController noteController) {
        this.noteController = noteController;
    }

    /**
     * 执行 "search" 命令的核心逻辑。
     *
     * @param args 传递给 "search" 命令的参数数组。
     *             我们期望这里是一个包含了 "[关键词]" 的数组。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (noteController == null) {
            System.err.println("❌ 内部错误: NoteController 未初始化，无法执行 'search' 命令。");
            return;
        }

        // 6. 参数校验
        // "search" 命令的参数由 CommandParser 的特殊解析逻辑处理，
        // 最终应该得到 [关键词] 一个参数。
        if (args.length != 1) {
            printUsage();
            return;
        }

        String keyword = args[0]; // 假设引号已由 CommandParser 去除

        // 7. 调用 Controller 完成工作
        noteController.searchNote(keyword);
    }

    /**
     * 重写 printUsage 方法，提供更详细的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: search \"<关键词>\"");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: search \"设计模式\"");
        System.out.println("      (注意: 关键词必须用双引号包围)");
    }
}