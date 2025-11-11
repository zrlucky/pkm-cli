package com.ZhangRuo.pkm.cli.command;


import com.ZhangRuo.pkm.controller.NoteController;

public class ExportAllCommand extends AbstractCommand {

    private final NoteController noteController;

    public ExportAllCommand(NoteController noteController) {
        super("export-all", "将所有笔记一次性导出到文件");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            printUsage();
            return;
        }
        String format = args[0];
        String path = args[1];
        noteController.exportAllNotes(format, path);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: export-all <格式> <路径>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: export-all text all_notes.txt");
    }
}