
package com.ZhangRuo.pkm.cli.command;

import com.ZhangRuo.pkm.controller.NoteController;

public class EditCommand extends AbstractCommand {

    private final NoteController noteController;

    public EditCommand(NoteController noteController) {
        super("edit", "编辑一篇已存在笔记的内容");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        // "edit" 命令的参数比较特殊，我们期望的是 [id, "content"]
        // 这个复杂的解析逻辑将在重构后的 CommandParser 中完成
        // 这里假设 args 已经是解析好的 [id, content]
        if (args.length != 2) {
            printUsage();
            return;
        }
        String id = args[0];
        String newContent = args[1].replaceAll("^\"|\"$", ""); // 移除引号
        noteController.editNote(id, newContent);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: edit <笔记ID> \"<新内容>\"");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: edit 1 \"这是更新后的内容。\"");
    }
}