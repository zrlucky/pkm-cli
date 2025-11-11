package com.ZhangRuo.pkm.cli.command;


/**
 * [命令模式] 命令接口。
 * 所有具体的用户命令（如 NewCommand, ListCommand）都必须实现此接口。
 * 它定义了一个命令对象必须具备的核心行为。
 */
public interface Command {

    /**
     * 执行该命令的核心逻辑。
     *
     * @param args 传递给该命令的参数数组。
     *             注意：这个数组不包含命令本身。例如，对于 "view 1"，args 将是 ["1"]。
     * @throws Exception 如果命令执行过程中发生任何错误。
     */
    void execute(String[] args) throws Exception;

    /**
     * 获取命令的唯一名称（用于在 Map 中注册和查找）。
     *
     * @return 命令的名称字符串 (e.g., "new", "list")。
     */
    String getName();

    /**
     * 获取命令的简短描述，用于在 'help' 命令中展示。
     *
     * @return 命令的描述字符串。
     */
    String getDescription();

    /**
     * 打印该命令的详细用法说明。
     * 这是一个 default 方法，如果子类不重写，就会使用这个默认实现。
     */
    default void printUsage() {
        System.out.println("用法: " + getName() + " [参数...]");
        System.out.println("描述: " + getDescription());
    }
}