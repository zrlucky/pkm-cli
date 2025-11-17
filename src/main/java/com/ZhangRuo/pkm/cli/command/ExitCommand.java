package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.cli.CommandParser;


/**
 * [命令模式] 退出程序的具体命令实现。
 * 它需要能够访问并修改 CommandParser 的运行状态以终止 REPL 循环。
 */
@CliCommand({"exit", "quit"}) // 1. 添加注解，并支持别名 "quit"
public class ExitCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private CommandParser commandParser;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public ExitCommand() {
        super("exit", "退出应用程序");
    }

    /**
     * 4. 新增公共 Setter 方法，用于在 CommandParser 中进行依赖注入。
     * CommandParser 会在初始化时，将自身的引用 (this) 注入进来。
     *
     * @param commandParser 主命令解析器的一个实例。
     */
    public void setCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    /**
     * 执行 "exit" 命令的核心逻辑。
     *
     * @param args 传递给 "exit" 命令的参数数组 (通常为空)。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (commandParser == null) {
            System.err.println("❌ 内部错误: CommandParser 未初始化，无法执行 'exit' 命令。");
            // 即使无法正常退出，也尝试强制终止程序
            System.out.println("👋 感谢使用个人知识管理系统！再见！(强制退出)");
            System.exit(0);
            return;
        }

        System.out.println("👋 感谢使用个人知识管理系统！再见！");
        // 6. 调用 CommandParser 的方法来改变其内部的 isRunning 状态
        commandParser.setRunning(false);
    }

    /**
     * 重写 printUsage，提供详细用法，并提示别名。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: exit");
        System.out.println("别名: quit");
        System.out.println("描述: " + getDescription());
    }
}