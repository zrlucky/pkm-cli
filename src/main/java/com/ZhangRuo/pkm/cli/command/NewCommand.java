package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.controller.NoteController;

/**
 * [命令模式] 创建新笔记的具体命令实现。
 * 负责解析 "new" 命令的参数，并调用 NoteController 来执行创建操作。
 */
public class NewCommand extends AbstractCommand {

    // 1. 声明该命令所依赖的 Controller
    private final NoteController noteController;

    /**
     * 构造函数。
     * 通过 super() 设置命令的名称和描述。
     * 接收 NoteController 作为依赖（依赖注入）。
     *
     * @param noteController 笔记控制器的一个实例。
     */
    public NewCommand(NoteController noteController) {
        super("new", "创建一篇新笔记");
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
        // 2. 将 CommandParser 中 handleNewCommand 的逻辑“搬家”到这里

        // 参数校验
        if (args.length != 2) {
            printUsage(); // 如果参数数量不对，打印用法并返回
            return;
        }

        // 参数解析：去除参数两端的双引号
        String title = args[0].replaceAll("^\"|\"$", "");
        String content = args[1].replaceAll("^\"|\"$", "");

        // 3. 调用 Controller 完成工作
        noteController.createNote(title, content);
    }

    /**
     * 重写 printUsage 方法，提供更详细、更具体的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: new \"<标题>\" \"<内容>\"");
        System.out.println("示例: new \"Java学习笔记\" \"分层架构是重点。\"");
    }
}
