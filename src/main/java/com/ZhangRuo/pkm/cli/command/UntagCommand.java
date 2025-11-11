package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.controller.TagController;

public class UntagCommand extends AbstractCommand {

    private final TagController tagController;

    public UntagCommand(TagController tagController) {
        super("untag", "从指定的笔记中移除一个标签");
        this.tagController = tagController;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            printUsage();
            return;
        }
        String noteId = args[0];
        String tagName = args[1];
        tagController.removeTagFromNote(noteId, tagName);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: untag <笔记ID> <标签名>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: untag 1 old-tag");
    }
}