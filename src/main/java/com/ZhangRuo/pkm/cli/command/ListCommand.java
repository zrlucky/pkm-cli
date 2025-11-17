package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.NoteController;


import java.util.HashMap;
import java.util.Map;

/**
 * [命令模式] 列出笔记的具体命令实现。
 * 负责解析 "list" 命令的参数 (如 --tag)，并调用 NoteController 来执行列表展示。
 */
@CliCommand({"list", "ls"}) // 1. 添加注解，并支持别名 "ls"
public class ListCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private NoteController noteController;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public ListCommand() {
        super("list", "列出所有笔记，或按标签过滤");
    }

    /**
     * 4. 新增公共 Setter 方法，用于依赖注入。
     *
     * @param noteController 笔记控制器的一个实例。
     */
    public void setNoteController(NoteController noteController) {
        this.noteController = noteController;
    }

    /**
     * 执行 "list" 命令的核心逻辑。
     *
     * @param args 传递给 "list" 命令的参数数组 (e.g., ["--tag", "java"])。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (noteController == null) {
            System.err.println("❌ 内部错误: NoteController 未初始化，无法执行 'list' 命令。");
            return;
        }

        // 6. 将 CommandParser 中 handleListCommand 的逻辑“搬家”到这里

        // 解析选项参数
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

        // 7. 调用 Controller 完成工作
        noteController.listNotes(tagName);
    }

    /**
     * 重写 printUsage，提供详细用法。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: list [--tag <标签名>]");
        System.out.println("描述: " + getDescription());
        System.out.println("别名: ls");
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
                }
            }
        }
        return options;
    }
}