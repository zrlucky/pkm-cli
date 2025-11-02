package com.ZhangRuo.pkm.controller;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.service.ExportService;
import com.ZhangRuo.pkm.service.NoteService;
import com.ZhangRuo.pkm.enums.ExportFormat;

import java.util.List;
import java.io.IOException;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

/*
* [控制器层]
*负责处理与笔记相关的用户交互逻辑
* 它接受来自CommandParser的指令，调用NoteService完成工作
* 并将结果格式化后输出到控制台
* */

public class NoteController {

    private final NoteService noteService;
    private final ExportService exportService;

    /*
    * 构造函数，用于接收外部传入的NoteService实例（依赖注入）
    * */
    public NoteController(NoteService noteService , ExportService exportService) {
        this.noteService = noteService;
        this.exportService = exportService;
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


    /**
     * [交互逻辑] 处理列出笔记的请求，支持按标签过滤。
     * @param tagName 如果不为null，则只列出包含该标签的笔记。
     * @return 查询到的笔记列表，用于上层缓存。
     */
    public List<Note> listNotes(String tagName) { // 1. 返回值从 void 改为 List<Note>
        List<Note> notes;
        if (tagName != null) {
            notes = noteService.findNotesByTag(tagName);
            System.out.println("--- 标签为 '" + tagName + "' 的笔记列表 ---");
        } else {
            notes = noteService.getAllNotes();
            System.out.println("--- 所有笔记列表 ---");
        }

        if (notes.isEmpty()) {
            System.out.println("ℹ️  没有找到符合条件的笔记。");
            return notes; // 2. 在这里也要返回 notes 列表
        }

        System.out.println("------------------------------------");
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            int displayId = i + 1; // 用户的“短ID”

            String tags = String.join(", ", note.getTags());
            // 使用短ID进行打印
            System.out.printf("[%d] %s (%s) [%s]%n",
                    displayId,
                    note.getTitle(),
                    note.getCreatedAt().toLocalDate().toString(),
                    tags);
        }
        System.out.println("------------------------------------");

        return notes; // 3. 在方法末尾返回最终的列表
    }



    /*
    * [交互逻辑] 处理根据ID查看笔记详情的请求
    * */
    public void viewNoteById(String id) {
        Optional<Note> noteOpt = noteService.findNoteById(id);
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();

            // 2. 创建一个我们想要的格式化模板
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            System.out.println("--- 笔记详情 ---");
            System.out.println("ID      : " + note.getId());
            System.out.println("标题    : " + note.getTitle());
            System.out.println("标签    : " + String.join(", ", note.getTags()));

            // 3. 在打印时，使用 .format(formatter) 来应用模板
            System.out.println("创建时间: " + note.getCreatedAt().format(formatter));
            System.out.println("更新时间: " + note.getUpdatedAt().format(formatter));

            System.out.println("----------------");
            System.out.println("内容:\n" + note.getContent());
            System.out.println("----------------");
        } else {
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

    /*
    * [交互逻辑]处理编辑笔记内容的请求
    *
    * @Param id  要编辑的笔记ID
    * @Param newContent  新的笔记内容
    * */
    public void editNote(String id, String newContent) {
        Optional<Note> updateNoteOpt = noteService.updateNoteContent(id ,newContent);

        if (updateNoteOpt.isPresent()){
            System.out.println("✅ 笔记 (ID: " + id + ") 内容已成功更新。");
        }else {
            System.err.println("❌ 错误: 未找到ID为 '\" + id + \"' 的笔记，编辑失败。");
        }
    }

    /*
    * [交互逻辑] 处理根据关键词搜索笔记的请求
    *
    * @param keyword 搜索关键词
    * */
    public List<Note> searchNote(String keyword) {
        List<Note>notes = noteService.searchNotesByKeyword(keyword);

        System.out.println("--- 关键词为 ‘"+keyword+"’ 的搜索结果 ---");

        if (notes.isEmpty()){
            System.out.println("ℹ️  没有找到包含该关键词的笔记。");
            return notes;
        }

        //复用list命令的输出格式
        for (Note note : notes) {
            String tags = String.join(", ", note.getTags());
            System.out.printf("[%s] %s (%s) [%s] %n",
                    note.getId(),
                    note.getTitle(),
                    note.getCreatedAt().toLocalDate().toString(),
                    tags);
        }
        System.out.println("---------------------");

        return notes;

    }

    /*
    * [交互逻辑] 导出笔记
    * */
    public void exportNote(String id,String formatStr,String path) {
        //1.查找笔记
        Optional<Note> noteOpt = noteService.findNoteById(id);
        if (noteOpt.isEmpty()){
            System.err.println("❌ 错误: 未找到ID为 '" + id + "' 的笔记，导出失败。");
            return;
        }

        //2.解析格式
        ExportFormat format;
        try{
            format = ExportFormat.valueOf(formatStr.toUpperCase());
        }catch (IllegalArgumentException e){
            System.err.println("❌ 错误: 不支持的导出格式 '" + formatStr + "' 。目前支持TEXT");
            return;
        }

        // 3. 调用导出服务
        try {
            // 将单篇笔记放入一个列表中进行导出
            exportService.exportNotes(List.of(noteOpt.get()), path, format);
            System.out.println("✅ 笔记 (ID: " + id + ") 已成功导出到: " + path);
        } catch (IOException e) {
            System.err.println("❌ 错误: 导出文件时发生错误: " + e.getMessage());
        }

    }

    /*
    * [交互逻辑] 处理导出所有笔记的请求
    *
    * @param formatStr 导出的格式字符串（e.g.“text”）
    * @param path 导出的文件路径
    * */
    public void exportAllNotes(String formatStr, String path) {
        // 1. 获取所有笔记
        List<Note> allNotes = noteService.getAllNotes();
        if (allNotes.isEmpty()) {
            System.out.println("ℹ️  当前没有任何笔记可以导出。");
            return;
        }

        // 2. 解析格式 (与 exportNote 方法逻辑相同)
        ExportFormat format;
        try {
            format = ExportFormat.valueOf(formatStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ 错误: 不支持的导出格式 '" + formatStr + "'。目前仅支持 'TEXT'。");
            return;
        }

        // 3. 调用导出服务
        try {
            exportService.exportNotes(allNotes, path, format);
            System.out.println("✅ 所有 " + allNotes.size() + " 篇笔记已成功导出到: " + path);
        } catch (IOException e) {
            System.err.println("❌ 错误: 导出文件时发生错误: " + e.getMessage());
        }
    }




}
