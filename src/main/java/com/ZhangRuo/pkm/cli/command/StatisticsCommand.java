package com.ZhangRuo.pkm.cli.command;

import com.ZhangRuo.pkm.service.NoteService;
import com.ZhangRuo.pkm.service.TagService; // 假设我们需要 TagService
import java.util.List;
import com.ZhangRuo.pkm.entity.Note;

/**
 * [命令模式] 显示系统统计信息的新命令。
 * 用于验证新架构的扩展性。
 */
public class StatisticsCommand extends AbstractCommand {

    // 这个命令需要 Service 来获取真实数据
    private final NoteService noteService;
    private final TagService tagService;

    /**
     * 构造函数，注入依赖。
     */
    public StatisticsCommand(NoteService noteService, TagService tagService) {
        super("stats", "显示系统统计信息");
        this.noteService = noteService;
        this.tagService = tagService;
    }

    /**
     * 执行 "stats" 命令的核心逻辑。
     *
     * @param args 传递给 "stats" 命令的参数数组 (通常为空)。
     */
    @Override
    public void execute(String[] args) {
        // --- TODO: 实现实际统计逻辑 ---
        // 我们在这里实现真实的统计，而不是像示例一样用假数据
        List<Note> allNotes = noteService.getAllNotes();
        int noteCount = allNotes.size();

        // 使用 Stream API 计算总标签数
        long tagCount = allNotes.stream()
                .flatMap(note -> note.getTags().stream())
                .distinct()
                .count();

        System.out.println("\n--- 系统统计信息 ---");
        System.out.println("  总笔记数: " + noteCount);
        System.out.println("  总标签数: " + tagCount);
        // 更多统计信息可以未来添加...
        System.out.println("--------------------");
    }

    @Override
    public void printUsage() {
        System.out.println("用法: stats");
        System.out.println("描述: " + getDescription());
    }
}