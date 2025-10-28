package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.repository.StorageService;

import java.util.List;
import java.util.Optional;

/**
 * [业务逻辑层]
 * 封装所有与标签管理相关的纯业务逻辑。
 */
public class TagService {

    private final StorageService storageService;

    /**
     * 构造函数，用于接收外部传入的 StorageService 实例（依赖注入）。
     * @param storageService 一个实现了 StorageService 接口的对象。
     */
    public TagService(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 为指定的笔记添加一个标签。
     *
     * @param noteId  要添加标签的笔记ID。
     * @param tagName 要添加的标签名。
     * @return 如果操作成功，返回更新后的 Note 对象；如果笔记未找到，返回空的 Optional。
     */
    public Optional<Note> addTagToNote(String noteId, String tagName) {
        List<Note> notes = storageService.load();

        // 1. 找到需要修改的笔记
        Optional<Note> noteToUpdateOpt = notes.stream()
                .filter(note -> note.getId() != null && note.getId().equals(noteId))
                .findFirst();

        // 2. 如果找到了，就执行修改
        if (noteToUpdateOpt.isPresent()) {
            Note noteToUpdate = noteToUpdateOpt.get();
            noteToUpdate.addTag(tagName); // 调用 Note 自身的 addTag 方法
            storageService.save(notes); // 保存整个更新后的列表
            return Optional.of(noteToUpdate);
        } else {
            return Optional.empty(); // 如果没找到笔记，返回空
        }
    }

    /**
     * 为指定的笔记移除一个标签。
     *
     * @param noteId  要移除标签的笔记ID。
     * @param tagName 要移除的标签名。
     * @return 如果操作成功，返回更新后的 Note 对象；如果笔记未找到，返回空的 Optional。
     */
    public Optional<Note> removeTagFromNote(String noteId, String tagName) {
        List<Note> notes = storageService.load();

        Optional<Note> noteToUpdateOpt = notes.stream()
                .filter(note -> note.getId() != null && note.getId().equals(noteId))
                .findFirst();

        if (noteToUpdateOpt.isPresent()) {
            Note noteToUpdate = noteToUpdateOpt.get();
            noteToUpdate.removeTag(tagName); // 调用 Note 自身的 removeTag 方法
            storageService.save(notes);
            return Optional.of(noteToUpdate);
        } else {
            return Optional.empty();
        }
    }
}