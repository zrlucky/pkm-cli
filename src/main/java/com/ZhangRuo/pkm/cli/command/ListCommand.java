package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.NoteController;

import java.util.HashMap;
import java.util.Map;

/**
 * [命令模式] 列出笔记的具体命令实现。
 * 负责解析 "list" 命令的参数 (如 --tag)，并调用 NoteController 来执行列表展示。
 */
public class ListCommand extends AbstractCommand {

    private final NoteController noteController;

    /**
     * 构造函数。
     * @param noteController 笔记控制器的一个实例。
     */
    public ListCommand(NoteController noteController) {
        super("list", "列出所有笔记");
        this.noteController = noteController;
    }

    /**
     * 执行 "list" 命令的核心逻辑。
     *
     * @param args 传递给 "list" 命令的参数数组 (e.g., ["--tag", "java"])。
     */
    @Override
    public void execute(String[] args) {
        // 1. 解析选项参数
        Map<String, String> options = parseOptions(args);

        String tagName = null;
        if (options.containsKey("tag")) {
            tagName = options.get("tag");
        } else if (!options.isEmpty()) {
            // 如果包含了除 "tag" 以外的其他未知选项，则视为错误
            System.err.println("❌ 参数错误! 'list' 命令只支持 '--tag' 选项。");
            printUsage();
            return;
        }

        // 2. 调用 Controller 完成工作
        noteController.listNotes(tagName);
    }

    /**
     * 重写 printUsage，提供详细用法。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: list [--tag <标签名>]");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: list");
        System.out.println("      list --tag java");
    }

    /**
     * 一个简单的选项解析器。
     * 用于解析 "--key value" 形式的参数。
     * @param args 参数数组。
     * @return 一个包含所有选项的 Map。
     */
    private Map<String, String> parseOptions(String[] args) {
        Map<String, String> options = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2); // 去掉 "--"
                // 检查后面是否跟着一个值，并且那个值不是另一个选项
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    options.put(key, args[i + 1]);
                    i++; // 跳过下一个值
                } else {
                    // 如果后面没有值，可以当作一个布尔标志（这里我们不需要）
                    options.put(key, "true");
                }
            }
        }
        return options;
    }
}