package com.ZhangRuo.pkm.controller;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.service.NoteService;

import java.util.List;
import java.util.Optional;

/*
* [控制器层]
*负责处理与笔记相关的用户交互逻辑
* 它接受来自CommandParser的指令，调用NoteService完成工作
* 并将结果格式化后输出到控制台
* */

public class NoteController {

    private final NoteService noteService;

    /*
    * 构造函数，用于接收外部传入的NoteService实例（依赖注入）
    * */
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /*
    * [交互逻辑] 处理创建新笔记的请求
    * */

    public void createNote(String title, String content) {
        try {
            Note newNote = noteService.createNote(title, content);
            //交互逻辑：成功信息的展示逻辑
            System.out.println("✅ 笔记创建成功！");
            System.out.println("ID: " + newNote.getId());
            System.out.println("标题: " + newNote.getTitle());
        }catch (IllegalArgumentException e){
            //失败信息的展示逻辑
            System.out.println("❌ 错误: " + e.getMessage());
        }
    }

    /*
     * [交互逻辑] 处理列出所有笔记的请求
     * */
    public void listAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        if (notes.isEmpty()){
            System.out.println("ℹ️  当前没有任何笔记。");
            return;
        }

        System.out.println("--- 笔记列表 ---");
        for (Note note : notes){
            //将Note对象格式化为一行摘要
            String tags = String.join(",", note.getTags());
            System.out.printf("[%s] %s [%s]%n",note.getId(),note.getTitle(),tags);
        }
        System.out.println("----------");
    }

    /*
    * [交互逻辑] 处理根据ID查看笔记详情的请求
    * */
    public void viewNoteById(String id){
        Optional<Note> noteOpt = noteService.findNoteById(id);
        if (noteOpt.isPresent()){
            Note note = noteOpt.get();
            //将Note对象的完整信息格式化后输出
            System.out.println("--- 笔记详情 ---");
            System.out.println("ID: " + note.getId());
            System.out.println("标题: " + note.getTitle());
            System.out.println("标签: " + String.join(",", note.getTags()));
            System.out.println("创建时间: " + note.getCreatedAt());
            System.out.println("更新时间: " + note.getUpdatedAt());
            System.out.println("----------");
            System.out.println("内容:\r\n " + note.getContent());
            System.out.println("----------");
        }else {
            System.err.println("❌ 错误: 未找到ID为 '" + id + "' 的笔记。");
        }
    }

    /*
    * [交互逻辑] 处理删除笔记的请求
    * */
    public void deleteNoteById(String id){
        boolean deleted = noteService.deleteNote(id);
        if (deleted){
            System.out.println("✅ 笔记 (ID: " + id + ") 已被成功删除。");
        }else {
            System.err.println("❌ 错误: 未找到ID为 '" + id + "' 的笔记，删除失败。");
        }
    }


}
