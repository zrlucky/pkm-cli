package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.repository.StorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
* [业务逻辑层]
* 封装所有与笔记相关的纯业务逻辑
* 它不关心数据具体如何存储，也不关心结果如何展示给用户
* */

public class NoteService {
    //依赖于StorageService 接口，而不是具体的实现类，这是”面向接口编程“
    private final StorageService storageService;

    /*
    * 构造函数，用于接受外部传入的StorageService实例（依赖注入）
    * @param storageService 一个实现了StorageService接口的对象
    * */
    public NoteService(StorageService storageService) {
        this.storageService = storageService;
    }

    /*
    * [业务逻辑]创建一篇新笔记
    * 包含生成ID、保存到存储等核心逻辑
    *
    * @param title 笔记标题
    * @param content 笔记内容
    * @return 创建好的、带有唯一ID的Note对象
    * */
    public Note createNote(String title, String content) {
        //1.核心业务校验
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }

        //2.加载当前所有笔记，为添加新笔记做准备
        List<Note> notes = storageService.load();

        //3.创建并设置新笔记的核心业务属性
        Note newNote = new Note(title, content);
        newNote.setId(UUID.randomUUID().toString());//在Service层生成唯一ID

        //4.将新笔记添加到列表中
        notes.add(newNote);

        //5.将更新后的完整列表保存回去
        storageService.save(notes);

        return newNote;

    }

    /*
    * [业务逻辑] 获取所有笔记
    * @return 包含所有笔记的列表
    * */
    public List<Note> getAllNotes() {
        return storageService.load();
    }

    /*
    * [业务逻辑] 根据ID查找一篇笔记
    * @param id 要查找的笔记ID
    * @return 一个包含Note的Optional（如果找到），或一个空的Optional(如果没找到)
    * */
    public Optional<Note> findNoteById(String id) {
        return storageService.load().stream()
                .filter(note -> note.getId() != null && note.getId().equals(id))
                .findFirst();
    }

    /*
    * [业务逻辑]根据ID删除一篇笔记
    *@param id 要删除的笔记ID
    * @return 如果成功删除则返回true，否则返回false
    * */
    public boolean deleteNote(String id) {
        List<Note> notes = storageService.load();
        //使用removeIf的方式高效删除
        boolean removed = notes.removeIf(note -> note.getId() != null && note.getId().equals(id));

        //如果真的删除了笔记，才需要执行保存操作
        if (removed) {
            storageService.save(notes);
        }
        return removed;
    }



}
