package com.ZhangRuo.pkm.cli.command;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * [命令模式] 命令注册器。
 * 负责管理系统中所有可用的命令对象。
 * 它提供注册、查找和获取所有命令的功能。
 */
public class CommandRegistry {

    /**
     * 核心数据结构：使用 Map 来存储命令。
     * Key: 命令的小写名称 (String)。
     * Value: 对应的命令对象 (Command)。
     */
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * 注册一个命令。
     * 命令的名称将被转换为小写，以实现不区分大小写的查找。
     * @param command 要注册的命令对象。
     */
    public void registerCommand(Command command) {
        if (command != null) {
            commands.put(command.getName().toLowerCase(), command);
        }
    }

    /**
     * 为一个已存在的命令注册一个别名。
     * @param alias        别名 (e.g., "q", "quit")。
     * @param commandName  原始的命令名称 (e.g., "exit")。
     */
    public void registerAlias(String alias, String commandName) {
        Command command = commands.get(commandName.toLowerCase());
        if (command != null) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    /**
     * 根据名称获取一个命令对象。
     * @param name 命令的名称或别名。
     * @return 找到的 Command 对象，如果不存在则返回 null。
     */
    public Command getCommand(String name) {
        if (name == null) {
            return null;
        }
        return commands.get(name.toLowerCase());
    }

    /**
     * 检查是否存在指定名称的命令。
     * @param name 命令的名称或别名。
     * @return 如果存在则返回 true，否则返回 false。
     */
    public boolean hasCommand(String name) {
        if (name == null) {
            return false;
        }
        return commands.containsKey(name.toLowerCase());
    }

    /**
     * 获取所有已注册的命令对象。
     * 主要用于 'help' 命令展示所有可用命令。
     * @return 一个包含所有 Command 对象的集合。
     */
    public Collection<Command> getAllCommands() {
        return commands.values();
    }

    /**
     * 获取已注册命令的总数。
     * @return 命令数量。
     */
    public int getCommandCount() {
        return commands.size();
    }
}