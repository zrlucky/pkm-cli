package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.cli.CommandParser;

/**
 * [命令模式] 退出程序的具体命令实现。
 * 它需要能够访问并修改 CommandParser 的运行状态。
 */
public class ExitCommand extends AbstractCommand {

    // 依赖于 CommandParser 本身
    private final CommandParser commandParser;

    /**
     * 构造函数。
     * @param commandParser 主命令解析器的一个实例。
     */
    public ExitCommand(CommandParser commandParser) {
        super("exit", "退出应用程序");
        this.commandParser = commandParser;
    }

    /**
     * 执行 "exit" 命令的核心逻辑。
     *
     * @param args 传递给 "exit" 命令的参数数组 (通常为空)。
     */
    @Override
    public void execute(String[] args) {
        System.out.println("👋 感谢使用个人知识管理系统！再见！");
        // 调用 CommandParser 的方法来改变其内部的 isRunning 状态
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