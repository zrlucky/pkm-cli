package com.ZhangRuo.pkm.app;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.exception.FileOperationException;
import com.ZhangRuo.pkm.repository.NoteFileRepository;
import com.ZhangRuo.pkm.service.TagService;

import java.util.List;

/*
* 应用程序的主入口
* 负责协调各个板块，完成数据加载、处理和保存的完整流程
* */

public class Application {

    public static void main(String[] args) {
        //1.初始化所有服务和仓库
        System.out.println(">>>1. 初始化服务...");
        NoteFileRepository repository = new NoteFileRepository();
        TagService tagService = new TagService();
        List<Note> notes;//用于存放笔记的列表

        //2.启动时加载数据，并处理潜在异常
        System.out.println(">>>2. 尝试从文件 notes.dat 加载笔记...");
        try {
            notes = repository.loadNotes();
            System.out.println("加载成功！当前共有 "+notes.size()+" 篇笔记。");
        }catch (FileOperationException e){
            //[对应“异常处理全面，用户体验友好”]
            //捕获我们自定义的文件操作异常
            System.err.println("!!!严重错误: 笔记加载失败! 程序将以空列表启动。");
            //打印详细的、包含异常链的错误信息到错误流，便于开发者调试
            e.printStackTrace();
            //即使加载失败，也要保证程序能正常运行，初始化一个空列表
            notes = new java.util.ArrayList<>();
        }

        //3.将加载的笔记重建到标签索引中
        System.out.println(">>>3. 重建标签索引...");
        for (Note note : notes) {
            tagService.indexNote(note);
        }
        System.out.println("索引重建完成!");
        System.out.println("当前标签统计: "+tagService.getTagStatistics());


        //4.模拟一次完整的CRUD操作流程
        System.out.println(">>>4. 模拟用户操作（CRUD）...");

        //CREATE:创建一篇新笔记
        System.out.println("[CREATE] 创建一篇关于“Git”的新笔记。");
        Note newNote = new Note("Git入门","Git是最好的版本控制工具。");
        newNote.addTag("Git");
        newNote.addTag("Programming");
        notes.add(newNote);//添加到主列表
        tagService.indexNote(newNote);//更新标签索引

        //READ: 打印当前所有笔记
        System.out.println("[READ] 当前所有笔记标题: ");
        for (Note note : notes) {
            System.out.println("   -"+note.getTitle());
        }
        System.out.println("当前标签统计: "+tagService.getTagStatistics());

        //UPDATE: 更新一篇已有的笔记
        if (!notes.isEmpty()) {
            System.out.println("[UPDATE] 为第一篇笔记添加 'Update' 标签。]");
            Note firstNote = notes.get(0);
            firstNote.addTag("Update");
            //注意: 因为 Note对象是引用类型，所以修改后无需重新 index,tagMap 中的引用已经自动更新
        }

        //DELETE: 删除一篇笔记
        if (notes.size() > 1) {
            System.out.println("[UPDATE] 删除第二篇笔记");
            notes.remove(1);
        }


        //5.程序退出前，保存所有更改
        System.out.println("\r\n>>>5. 程序即将退出，保存所有笔记...");
        try {
            repository.saveNotes(notes);
            System.out.println("保存成功! 共保存了 "+notes.size()+" 篇笔记到 notes.dat。");
        }catch (FileOperationException e){
            //[对应"异常处理全面，用户体验友好"]
            System.err.println("!!!严重错误: 笔记保存失败! 您的部分更改可能已丢失。");
            e.printStackTrace();
        }
        System.out.println("\r\n>>>程序运行结束。");


    }
}
