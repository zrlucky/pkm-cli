package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.TagController;

/**
 * [命令模式] 从笔记中移除标签的具体命令实现。
 * 负责解析 "untag" 命令的参数，并调用 TagController 来执行操作。
 */
@CliCommand("untag") // 1. 添加注解
public class UntagCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private TagController tagController;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public UntagCommand() {
        super("untag", "从指定的笔记中移除一个标签");
    }

    /**
     * 4. 新增公共 Setter 方法，用于依赖注入。
     *
     * @param tagController 标签控制器的一个实例。
     */
    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    /**
     * 执行 "untag" 命令的核心逻辑。
     *
     * @param args 传递给 "untag" 命令的参数数组，期望包含 [笔记ID, 标签名]。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (tagController == null) {
            System.err.println("❌ 内部错误: TagController 未初始化，无法执行 'untag' 命令。");
            return;
        }

        // 6. 参数校验
        if (args.length != 2) {
            printUsage();
            return;
        }

        String noteId = args[0];
        String tagName = args[1];

        // 7. 调用 Controller 完成工作
        tagController.removeTagFromNote(noteId, tagName);
    }

    /**
     * 重写 printUsage 方法，提供更详细的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: untag <笔记ID> <标签名>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: untag 1 old-tag");
        System.out.println("      (注意: 笔记ID可以是短ID或完整ID)");
    }
}