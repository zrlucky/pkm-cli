package com.ZhangRuo.pkm.cli.command;

import com.ZhangRuo.pkm.service.NoteService;
import com.ZhangRuo.pkm.service.TagService; // 假设我们需要 TagService
import java.util.List;
import com.ZhangRuo.pkm.entity.Note;






/**
 * [命令模式] 显示系统统计信息的新命令。
 * 遵循注解注册和Setter注入模式。
 */
@CliCommand({"stats", "statistics"}) // 1. 添加注解，并支持别名 "statistics"
public class StatisticsCommand extends AbstractCommand {

    // 2. 依赖声明：移除 final
    private NoteService noteService;
    private TagService tagService;

    /**
     * 3. 提供一个无参数的构造函数。
     */
    public StatisticsCommand() {
        super("stats", "显示系统统计信息");
    }

    /**
     * 4. 新增公共 Setter 方法，用于依赖注入 NoteService。
     * @param noteService 笔记服务的一个实例。
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * 4. 新增公共 Setter 方法，用于依赖注入 TagService。
     * @param tagService 标签服务的一个实例。
     */
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 执行 "stats" 命令的核心逻辑。
     *
     * @param args 传递给 "stats" 命令的参数数组 (通常为空)。
     */
    @Override
    public void execute(String[] args) {
        // 5. 【重要】在执行操作前，检查所有依赖是否已被注入
        if (noteService == null || tagService == null) {
            System.err.println("❌ 内部错误: Service 未初始化，无法执行 'stats' 命令。");
            return;
        }

        // --- 实现真实的统计逻辑 ---
        List<Note> allNotes = noteService.getAllNotes();
        int noteCount = allNotes.size();

        // 使用 Stream API 高效计算唯一的标签总数
        long tagCount = allNotes.stream()
                .flatMap(note -> note.getTags().stream()) // 将所有笔记的标签列表“压平”成一个大的标签流
                .distinct() // 去重
                .count(); // 计数

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