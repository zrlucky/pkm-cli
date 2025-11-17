package com.ZhangRuo.pkm.cli.command;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [自定义注解] 命令行命令注解。
 * 用于“标记”一个类是可被自动发现和注册的命令行命令。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CliCommand {

    /**
     * 定义命令的名称或别名。
     * 这是一个字符串数组，允许一个命令拥有多个名称。
     * 例如: {"exit", "quit"}
     * @return 命令的名称数组。
     */
    String[] value();
}