package com.ZhangRuo.pkm.cli.command;



import com.ZhangRuo.pkm.controller.NoteController;

public class ExportCommand extends AbstractCommand {

    private final NoteController noteController;

    public ExportCommand(NoteController noteController) {
        super("export", "将单篇指定的笔记导出为文件");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            printUsage();
            return;
        }
        String noteId = args[0];
        String format = args[1];
        String path = args[2];
        noteController.exportNote(noteId, format, path);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: export <笔记ID> <格式> <路径>");
        System.out.println("描述: " + getDescription());
        System.out.println("示例: export 1 text my_note.txt");
    }
}