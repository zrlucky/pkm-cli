package com.ZhangRuo.pkm.cli.command;


import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * [重构后] 基于注解的命令注册器。
 * 能够在初始化时，自动扫描并注册所有被 @CliCommand 注解标记的命令。
 */
public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    /**
     * 构造函数。
     * 在创建实例时，立即触发自动注册流程。
     */
    public CommandRegistry() {
        System.out.println("🚀 命令注册器初始化，开始自动扫描命令...");
        autoRegisterCommands();
    }

    /**
     * 自动扫描并注册所有带有 @CliCommand 注解的命令类。
     */
    private void autoRegisterCommands() {
        // 1. 设置要扫描的包路径
        Reflections reflections = new Reflections("com.yourname.pkm.cli.command");

        // 2. 获取所有被 @CliCommand 注解标记的类
        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(CliCommand.class);
        System.out.println("    🔍 发现了 " + commandClasses.size() + " 个被 @CliCommand 标记的类。");

        // 3. 遍历并尝试注册每一个类
        for (Class<?> clazz : commandClasses) {
            // 确保这个类是 Command 接口的一个实现
            if (Command.class.isAssignableFrom(clazz)) {
                // 进行类型转换，并调用注册方法
                registerCommandClass((Class<? extends Command>) clazz);
            }
        }
    }

    /**
     * 通过反射，注册单个命令类。
     * @param commandClass 要注册的命令的 Class 对象。
     */
    private void registerCommandClass(Class<? extends Command> commandClass) {
        try {
            // 1. 获取类上的注解实例
            CliCommand annotation = commandClass.getAnnotation(CliCommand.class);
            if (annotation == null) return; // 理论上不会发生，因为我们就是通过注解找到它的

            // 2. 通过反射获取无参数的构造函数，并创建实例
            // 【重要前提】所有被自动注册的命令类，都必须有一个 public 的无参构造函数！
            Constructor<? extends Command> constructor = commandClass.getDeclaredConstructor();
            Command command = constructor.newInstance();

            // 3. 获取注解中定义的所有命令名/别名
            String[] commandNames = annotation.value();

            // 4. 将命令实例与它的所有名字关联并注册到 Map 中
            for (String name : commandNames) {
                commands.put(name.toLowerCase(), command);
                System.out.println("    ✨ 成功注册命令: '" + name.toLowerCase() + "' -> " + commandClass.getSimpleName());
            }

        } catch (Exception e) {
            // 捕获所有可能的反射异常（如找不到构造函数、实例化失败等）
            System.err.println("    ❌ 注册命令类失败: " + commandClass.getName() + " - " + e.getMessage());
        }
    }

    // --- 以下方法保持与上一版基本一致 ---

    /**
     * [兼容方法] 手动注册一个命令。
     * 主要用于注册那些需要复杂依赖、无法通过无参构造函数创建的命令。
     * @param command 要注册的命令对象。
     */
    public void registerCommand(Command command) {
        if (command != null) {
            commands.put(command.getName().toLowerCase(), command);
        }
    }

    public Command getCommand(String name) {
        if (name == null) return null;
        return commands.get(name.toLowerCase());
    }

    public boolean hasCommand(String name) {
        if (name == null) return false;
        return commands.containsKey(name.toLowerCase());
    }

    public Collection<Command> getAllCommands() {
        return commands.values();
    }

    public int getCommandCount() {
        return commands.size();
    }
}