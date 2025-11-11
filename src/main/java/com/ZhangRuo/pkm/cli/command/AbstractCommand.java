package com.ZhangRuo.pkm.cli.command;


/**
 * [命令模式] 抽象命令基类。
 * 实现了 Command 接口中的通用部分 (getName, getDescription)，
 * 以减少具体命令子类中的重复代码。
 */
public abstract class AbstractCommand implements Command {

    protected final String name;
    protected final String description;

    /**
     * 构造函数，所有子类都必须调用它来设置自己的名称和描述。
     * @param name        命令名称。
     * @param description 命令描述。
     */
    protected AbstractCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    // 注意：execute 和 printUsage 方法在这里没有被实现，
    // 因为它们的具体逻辑是每个命令都不同的，所以我们把它们留给子类去 `abstract` (隐式地) 或 `override`。
}