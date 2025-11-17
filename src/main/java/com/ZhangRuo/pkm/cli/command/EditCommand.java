
package com.ZhangRuo.pkm.cli.command;

import com.ZhangRuo.pkm.controller.NoteController;


/**
 * [命令模式] 编辑笔记内容的具体命令实现。
 * 负责解析 "edit" 命令的参数，并调用 NoteController 来执行更新。
 */
@CliCommand("edit") // 1. 添加注解
public class EditCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private NoteController noteController;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public EditCommand() {
        super("edit", "编辑一篇已存在笔记的内容");
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
     * 执行 "edit" 命令的核心逻辑。
     *
     * @param args 传递给 "edit" 命令的参数数组。
     *             我们期望这里是一个包含了 "[笔记ID]" 和 "[新内容]" 的数组。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查依赖是否已被注入
        if (noteController == null) {
            System.err.println("❌ 内部错误: NoteController 未初始化，无法执行 'edit' 命令。");
            return;
        }

        // 6. 参数校验
        // "edit" 命令的参数由 CommandParser 的特殊解析逻辑处理，
        // 最终应该得到 [笔记ID, 新内容] 两个参数。
        if (args.length != 2) {
            printUsage();
            return;
        }

        String id = args[0];
        String newContent = args[1]; // 假设引号已由 CommandParser 去除

        // 7. 调用 Controller 完成工作
        noteController.editNote(id, newContent);
    }

    /**
     * 重写 printUsage 方法，提供更详细的用法说明。
     */
    @Override
    public void printUsage() {
        System.out.println("用法: edit <笔记ID> \"<新内容>\"");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: edit 1 \"这是更新后的内容。\"");
        System.out.println("      (注意: 笔记ID可以是短ID或完整ID，新内容必须用双引号包围)");
    }
}