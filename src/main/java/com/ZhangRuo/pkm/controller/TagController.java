package com.ZhangRuo.pkm.controller;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.service.TagService;

import java.util.Optional;

/**
 * [控制器层]
 * 负责处理与标签管理相关的用户交互逻辑。
 */
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * [交互逻辑] 处理为笔记添加标签的请求。
     */
    public void addTagToNote(String noteId, String tagName) {
        Optional<Note> updatedNoteOpt = tagService.addTagToNote(noteId, tagName);

        if (updatedNoteOpt.isPresent()) {
            Note updatedNote = updatedNoteOpt.get();
            System.out.println("✅ 标签 '" + tagName + "' 已成功添加到笔记 '" + updatedNote.getTitle() + "'。");
            System.out.println("   当前标签: " + String.join(", ", updatedNote.getTags()));
        } else {
            System.err.println("❌ 错误: 未找到ID为 '" + noteId + "' 的笔记，添加标签失败。");
        }
    }

    /**
     * [交互逻辑] 处理为笔记移除标签的请求。
     */
    public void removeTagFromNote(String noteId, String tagName) {
        Optional<Note> updatedNoteOpt = tagService.removeTagFromNote(noteId, tagName);

        if (updatedNoteOpt.isPresent()) {
            Note updatedNote = updatedNoteOpt.get();
            System.out.println("✅ 标签 '" + tagName + "' 已成功从笔记 '" + updatedNote.getTitle() + "' 移除。");
        } else {
            System.err.println("❌ 错误: 未找到ID为 '" + noteId + "' 的笔记，或该笔记不含此标签，移除失败。");
        }
    }
}