package com.ZhangRuo.pkm.cli.command;



import java.util.Collection;

/**
 * [命令模式] 显示帮助信息的具体命令实现。
 * 它依赖于 CommandRegistry 来获取所有已注册的命令。
 */
public class HelpCommand extends AbstractCommand {

    // 依赖于 CommandRegistry
    private final CommandRegistry commandRegistry;

    /**
     * 构造函数。
     * @param commandRegistry 命令注册器的一个实例。
     */
    public HelpCommand(CommandRegistry commandRegistry) {
        super("help", "显示所有可用命令或特定命令的帮助信息");
        this.commandRegistry = commandRegistry;
    }

    /**
     * 执行 "help" 命令的核心逻辑。
     *
     * @param args 传递给 "help" 命令的参数数组。如果为空，则列出所有命令；
     *             如果包含一个命令名，则显示该命令的详细用法。
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            // --- 如果是 "help"，则列出所有命令 ---
            System.out.println("\n个人知识管理系统 - 命令行版本");
            System.out.println("========================================\n");
            System.out.println("可用命令:");

            // 从注册器中获取所有命令
            Collection<Command> commands = commandRegistry.getAllCommands();
            for (Command cmd : commands) {
                // 使用 printf 进行格式化对齐输出
                System.out.printf("  %-20s %s%n", cmd.getName(), cmd.getDescription());
            }
            System.out.println("\n输入 'help <命令名>' 查看具体命令用法。");

        } else {
            // --- 如果是 "help <command>"，则显示特定命令的用法 ---
            String commandName = args[0];
            Command cmd = commandRegistry.getCommand(commandName);

            if (cmd != null) {
                System.out.println("\n--- 命令 '" + commandName + "' 的详细用法 ---");
                // 调用该命令自身的 printUsage() 方法
                cmd.printUsage();
                System.out.println("------------------------------------");
            } else {
                System.err.println("❌ 未知命令: '" + commandName + "'");
            }
        }
    }

    /**
     * 重写 printUsage，提供 help 命令自身的用法。
     */
    @Override
    public void printUsage() {
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