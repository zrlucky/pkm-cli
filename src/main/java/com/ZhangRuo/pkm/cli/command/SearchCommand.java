package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.NoteController;

public class SearchCommand extends AbstractCommand {

    private final NoteController noteController;

    public SearchCommand(NoteController noteController) {
        super("search", "搜索标题或内容中包含关键词的笔记");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        // "search" 命令也需要特殊解析，这里假设 args 已经是解析好的 ["keyword"]
        if (args.length != 1) {
            printUsage();
            return;
        }
        String keyword = args[0].replaceAll("^\"|\"$", ""); // 移除引号
        noteController.searchNote(keyword);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: search \"<关键词>\"");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: search \"设计模式\"");
    }
}