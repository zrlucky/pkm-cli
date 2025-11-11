package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.NoteController;

public class DeleteCommand extends AbstractCommand {

    private final NoteController noteController;

    public DeleteCommand(NoteController noteController) {
        super("delete", "根据ID删除一篇笔记");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            printUsage();
            return;
        }
        noteController.deleteNoteById(args[0]);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: delete <笔记ID>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: delete 1");
    }
}