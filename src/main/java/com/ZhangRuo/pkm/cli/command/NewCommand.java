package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.controller.NoteController;



/**
 * [命令模式] 创建新笔记的具体命令实现。
 * 负责解析 "new" 命令的参数，并调用 NoteController 来执行创建操作。
 */
@CliCommand({"new", "create"}) // 1. 添加注解，并支持别名 "create"
public class NewCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final，因为它不会在构造函数中被初始化
    private NoteController noteController;

    /**
     * 3. 提供一个无参数的构造函数。
     * 这个构造函数将被 CommandRegistry 通过反射调用来创建实例。
     * 它只负责调用父类构造器设置命令的名称和描述。
     */
    public NewCommand() {
        super("new", "创建一篇新笔记");
    }

    /**
     * 4. 新增公共 Setter 方法，用于在 CommandParser 中进行依赖注入。
     * CommandParser 会在创建 NewCommand 实例后，调用此方法注入 NoteController。
     *
     * @param noteController 笔记控制器的一个实例。
     */
    public void setNoteController(NoteController noteController) {
        this.noteController = noteController;
    }

    /**
     * 执行 "new" 命令的核心逻辑。
     *
     * @param args 传递给 "new" 命令的参数数组。
     *             我们期望这里是一个包含了 "<标题>" 和 "<内容>" 的字符串。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (noteController == null) {
            System.err.println("❌ 内部错误: NoteController 未初始化，无法执行 'new' 命令。");
            return;
        }

        // 参数校验：期望是 [标题字符串, 内容字符串] 两个参数
        if (args.length != 2) {
            printUsage(); // 如果参数数量不对，打印用法并返回
            return;
        }

        // 参数解析：去除参数两端的双引号 (如果 CommandParser 没有去除)
        // 注意：我们 CommandParser 的 parseCommandLine 已经帮忙处理了引号，所以这里可以直接用
        String title = args[0];
        String content = args[1];

        // 6. 调用 Controller 完成工作
        noteController.createNote(title, content);
    }

    /**
     * 重写 printUsage 方法，提供更详细、更具体的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: new \"<标题>\" \"<内容>\"");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: new \"Java学习笔记\" \"分层架构是重点。\"");
    }
}