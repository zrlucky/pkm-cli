package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.controller.NoteController;


/**
 * [命令模式] 导出单篇笔记的具体命令实现。
 * 负责解析 "export" 命令的参数，并调用 NoteController 来执行导出。
 */
@CliCommand("export") // 1. 添加注解
public class ExportCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private NoteController noteController;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public ExportCommand() {
        super("export", "将单篇指定的笔记导出为文件");
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
     * 执行 "export" 命令的核心逻辑。
     *
     * @param args 传递给 "export" 命令的参数数组，期望包含 [笔记ID, 格式, 路径]。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (noteController == null) {
            System.err.println("❌ 内部错误: NoteController 未初始化，无法执行 'export' 命令。");
            return;
        }

        // 6. 参数校验
        if (args.length != 3) {
            printUsage();
            return;
        }

        String noteId = args[0];
        String format = args[1];
        String path = args[2];

        // 7. 调用 Controller 完成工作
        noteController.exportNote(noteId, format, path);
    }

    /**
     * 重写 printUsage 方法，提供更详细的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: export <笔记ID> <格式> <路径>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: export 1 text my_note.txt");
        System.out.println("      (注意: 笔记ID可以是短ID或完整ID)");
    }
}