package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.repository.StorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /*
    * [业务逻辑]根据标签名查找所有相关的笔记
    * @param tagName 要搜索的标签名
    * @return 包含该标签的所有Note对象的列表
    * */
    public List<Note> findNotesByTag(String tagName) {
        if (tagName == null || tagName.isBlank()){
            return getAllNotes();//如果标签为空，则返回所有笔记
        }
        //使用Stream API进行过滤
        return storageService.load().stream()
                .filter(note -> note.hasTag(tagName))//只保留包含该标签的笔记
                .collect(Collectors.toList());//将结果收集到列表中

    }

    /*
    * [业务逻辑] 更新一篇已存在笔记的内容
    * 会自动更新笔记的‘update’ 时间戳
    *
    * @param id 要修改的笔记的ID
    * @param newContent 新的笔记内容
    * @return 如果更新成功，返回更新后的Note对象；如果笔记未找到，返回空的Optional
    * */
    public Optional<Note> updateNoteContent(String id, String newContent) {
        List<Note> notes = storageService.load();

        //1.找到需要更新的笔记
        Optional<Note> noteToUpdateOpt = notes.stream()
                .filter(note -> note.getId() != null && note.getId().equals(id))
                .findFirst();

        //2.如果找到了，就执行更新
        if (noteToUpdateOpt.isPresent()) {
            Note noteToUpdate = noteToUpdateOpt.get();
            //调用Note自身的setter方法，该方法会自动更新时间戳
            noteToUpdate.setContent(newContent);
            storageService.save(notes);//保存整个更新后的列表
            return Optional.of(noteToUpdate);
        }else {
            return Optional.empty();//如果没找到笔记，返回空
        }

    }

    /*
    * [业务逻辑] 根据关键词搜索笔记
    * 搜索范围包括笔记的标题和内容
    *
    * @param keyword 要搜索的关键词
    *@return 包含该关键词的笔记列表
    * */
    public List<Note> searchNotesByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()){
            return List.of();//如果关键词为空，返回空列表
        }
        String lowerKeyword = keyword.toLowerCase();//转换为小写以进行不区分大小写的搜索

        return storageService.load().stream()
                .filter(note ->
                        //检查标题是否包含关键词
                        (note.getTitle() != null && note.getTitle().toLowerCase().contains(lowerKeyword)) ||
                        //或者检查内容是否包含关键词
                        (note.getContent() != null && note.getContent().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }



}
