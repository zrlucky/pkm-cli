package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.TagController;

public class TagCommand extends AbstractCommand {

    private final TagController tagController;

    public TagCommand(TagController tagController) {
        super("tag", "为指定的笔记添加一个标签");
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
        tagController.addTagToNote(noteId, tagName);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: tag <笔记ID> <标签名>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: tag 1 programming");
    }
}
