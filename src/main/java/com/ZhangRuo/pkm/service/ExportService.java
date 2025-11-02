package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.enums.ExportFormat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
* [业务逻辑层]
* 封装所有与笔记导出相关的业务逻辑
* */
public class ExportService {

    /*
    * 将笔记列表导出到指定格式的文件
    * @param notes 要导出的笔记列表
    * @param filePath 导出的文件路径
    * @param format 导出的文件格式
    * @throws IOException 如果写入文件时发生错误
    * */
    public void exportNotes(List<Note> notes,String filePath, ExportFormat format) throws IOException {
        switch (format) {
            case TEXT:
                exportToTextFile(notes,filePath);
                break;
            //之后可扩展case JSON等

        }
    }

    private void exportToTextFile(List<Note> notes,String filePath) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0;i < notes.size();i++) {
                Note note = notes.get(i);

                writer.write("标题：" + note.getTitle());
                writer.newLine();
                writer.write("创建时间：" + note.getCreatedAt().format(formatter));
                writer.newLine();
                writer.write("最后修改：" + note.getUpdatedAt().format(formatter));
                writer.newLine();
                writer.write("标签：" + String.join(", ", note.getTags()));
                writer.newLine();
                writer.write("内容：");
                writer.newLine();
                writer.write(note.getContent());
                writer.newLine();

                if (i < notes.size() - 1) {
                    writer.write("---");
                    writer.newLine();
                }
            }
        }
    }
}
