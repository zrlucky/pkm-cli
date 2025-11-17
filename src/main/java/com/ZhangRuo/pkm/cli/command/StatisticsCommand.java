package com.ZhangRuo.pkm.cli.command;

import com.ZhangRuo.pkm.service.NoteService;
import com.ZhangRuo.pkm.service.TagService; // 假设我们需要 TagService
import java.util.List;
import com.ZhangRuo.pkm.entity.Note;



@CliCommand({"stats", "statistics"})
public class StatisticsCommand extends AbstractCommand {

    private NoteService noteService;
    private TagService tagService;

    public StatisticsCommand() {
        super("stats", "显示系统统计信息");
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public void execute(String[] args) {
        if (noteService == null || tagService == null) {
            System.err.println("❌ 内部错误: Service 未初始化，无法执行 'stats' 命令。");
            return;
        }

        List<Note> allNotes = noteService.getAllNotes();
        int noteCount = allNotes.size();

        long tagCount = allNotes.stream()
                .flatMap(note -> note.getTags().stream())
                .distinct()
                .count();

        System.out.println("\n--- 系统统计信息 ---");
        System.out.println("  总笔记数: " + noteCount);
        System.out.println("  总标签数: " + tagCount);
        System.out.println("--------------------");
    }

    @Override
    public void printUsage() {
        System.out.println("用法: stats");
        System.out.println("别名: statistics");
        System.out.println("描述: " + getDescription());
    }
}