package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.NoteController;


/**
 * [命令模式] 删除笔记的具体命令实现。
 * 负责解析 "delete" 命令的参数，并调用 NoteController 来执行删除操作。
 */
@CliCommand("delete") // 1. 添加注解
public class DeleteCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private NoteController noteController;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public DeleteCommand() {
        super("delete", "根据ID删除一篇笔记");
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
     * 执行 "delete" 命令的核心逻辑。
     *
     * @param args 传递给 "delete" 命令的参数数组，期望包含一个笔记ID。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (noteController == null) {
            System.err.println("❌ 内部错误: NoteController 未初始化，无法执行 'delete' 命令。");
            return;
        }

        // 6. 参数校验
        if (args.length != 1) {
            printUsage();
            return;
        }

        // 7. 调用 Controller 完成工作
        noteController.deleteNoteById(args[0]);
    }

    /**
     * 重写 printUsage 方法，提供更详细的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: delete <笔记ID>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: delete 1");
        System.out.println("      (注意: 笔记ID可以是短ID或完整ID)");
    }
}