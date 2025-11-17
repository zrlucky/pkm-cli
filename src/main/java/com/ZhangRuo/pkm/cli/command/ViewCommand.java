package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.controller.NoteController;


/**
 * [命令模式] 查看笔记详情的具体命令实现。
 * 负责解析 "view" 命令的参数，并调用 NoteController 来执行展示。
 */
@CliCommand("view") // 1. 添加注解
public class ViewCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private NoteController noteController;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public ViewCommand() {
        super("view", "根据ID查看一篇笔记的详细内容");
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
     * 执行 "view" 命令的核心逻辑。
     *
     * @param args 传递给 "view" 命令的参数数组，期望包含一个笔记ID。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (noteController == null) {
            System.err.println("❌ 内部错误: NoteController 未初始化，无法执行 'view' 命令。");
            return;
        }

        // 6. 参数校验
        if (args.length != 1) {
            printUsage();
            return;
        }

        // 7. 调用 Controller 完成工作
        // 注意：这里的 args[0] 可能是短ID或完整ID，
        // 短ID到真实ID的翻译工作，由重构后的 CommandParser 负责。
        // ViewCommand 自身不关心这个翻译过程。
        noteController.viewNoteById(args[0]);
    }

    /**
     * 重写 printUsage 方法，提供更详细的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: view <笔记ID>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: view 1");
        System.out.println("      (注意: 笔记ID可以是 'list' 命令显示的短ID，也可以是完整的UUID)");
    }
}