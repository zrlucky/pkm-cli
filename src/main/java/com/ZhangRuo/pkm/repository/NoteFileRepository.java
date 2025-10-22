package com.ZhangRuo.pkm.repository;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.enums.ExportFormat;
import com.ZhangRuo.pkm.exception.FileOperationException;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.time.format.DateTimeFormatter;

/**
 * NoteFileRepository 负责笔记数据的文件持久化。
 * 提供了保存、加载和备份的核心功能。
 */
public class NoteFileRepository {

    // --- 常量定义 ---
    private static final String DEFAULT_DATA_FILE = "notes.dat"; // 默认数据文件名
    private static final String BACKUP_EXTENSION = ".backup";    // 备份文件扩展名

    private final String dataFilePath; // 实际使用的数据文件路径

    /**
     * 默认构造方法，使用默认文件名。
     */
    public NoteFileRepository() {
        this.dataFilePath = DEFAULT_DATA_FILE;
    }

    /**
     * 可指定文件路径的构造方法，便于测试。
     * @param dataFilePath 数据文件的路径。
     */
    public NoteFileRepository(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }


    // --- 核心方法实现 (对标评分标准) ---

    /**
     * [对应 "保存功能完整" + "备份机制有效"]
     * 将笔记列表序列化并保存到文件，包含完整的备份与恢复机制。
     *
     * @param notes 要保存的笔记列表。
     * @throws FileOperationException 如果保存过程中发生I/O错误。
     */
    public void saveNotes(List<Note> notes) throws FileOperationException {
        // 1. 在写入前，先创建备份
        createBackup();

        // 2. 使用 try-with-resources 确保流被自动关闭
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFilePath))) {
            // 3. 序列化整个列表对象
            oos.writeObject(notes);
        } catch (IOException e) {
            // 4. [关键] 如果写入失败，立即从备份中恢复，保证数据不丢失
            restoreBackup();
            // 5. 将底层异常包装成我们自定义的异常并向上抛出，通知调用者
            throw new FileOperationException("保存笔记数据", dataFilePath, e);
        }
    }

    /**
     * [对应 "加载功能完整"]
     * 从文件加载并反序列化笔记列表。
     *
     * @return 加载到的笔记列表。如果文件不存在或为空，返回空列表。
     * @throws FileOperationException 如果加载过程中发生I/O或反序列化错误。
     */
    public List<Note> loadNotes() throws FileOperationException {
        File dataFile = new File(dataFilePath);

        // 1. [关键] 处理文件不存在或为空的情况
        if (!dataFile.exists() || dataFile.length() == 0) {
            return new ArrayList<>(); // 返回空列表，而不是null或抛出异常
        }

        // 2. 使用 try-with-resources 确保流被自动关闭
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            // 3. 反序列化对象，并进行类型转换
            // @SuppressWarnings("unchecked") 用于抑制因泛型类型擦除而产生的编译警告
            @SuppressWarnings("unchecked")
            List<Note> notes = (List<Note>) ois.readObject();
            return notes;
        } catch (IOException | ClassNotFoundException e) {
            // 4. 将底层异常包装成我们自定义的异常并向上抛出
            throw new FileOperationException("加载笔记数据", dataFilePath, e);
        }
    }

    // --- 备份与恢复辅助方法 (对标 "备份机制有效") ---

    private void createBackup() {
        File originalFile = new File(dataFilePath);
        File backupFile = new File(dataFilePath + BACKUP_EXTENSION);

        if (originalFile.exists()) {
            try {
                Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // 在实际应用中，这里应该用日志记录下来
                System.err.println("警告：创建备份文件失败 - " + e.getMessage());
            }
        }
    }

    private void restoreBackup() {
        File originalFile = new File(dataFilePath);
        File backupFile = new File(dataFilePath + BACKUP_EXTENSION);

        if (backupFile.exists()) {
            try {
                Files.copy(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // 在实际应用中，这里应该用日志记录下来
                System.err.println("严重错误：从备份恢复文件失败 - " + e.getMessage());
            }
        }
    }


    /*
    * [对应“在exportNotes方法中添加格式分发逻辑”]
    * 根据指定的格式，将笔记列表导出到文件
    *
    * @param notes 要导出的笔记列表
    * @param filePath 导出的文件路径
    * @param format 导出的格式（例如ExporFormat.TEXT）
    * @throws FileOperationException 如果导出过程中发生IO错误
    * */
    public void exportNotes(List<Note> notes, String filePath, ExportFormat format) throws FileOperationException {
        //使用switch语句进行格式分发，便于未来扩展
        switch (format) {
            case TEXT :
                try {
                    //调用专门处理文本格式的私有方法
                    exportToTextFile(notes,filePath);
                }catch (IOException e){
                    throw new FileOperationException("导出为文本文件", filePath, e);
                }
                break;
//            default:
//                  throw new IllegalArgumentException("不支持的导出格式"+format);
        }
    }


    /*
    * [对应“实现文本格式导出方法”]
    * 将笔记列表以预定义的纯文本格式写入文件
    *
    * @param notes 要导出的笔记列表
    * @param filePath 导出的文件路径
    * throws IOException 如果写入文件时发生错误
    * */
    private void exportToTextFile(List<Note> notes, String filePath) throws IOException {
        //定义一个日期时间格式化器，让输出更美观
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //使用try-with-resources确保BufferedWriter被自动关闭
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < notes.size(); i++) {
                Note note = notes.get(i);

                //定义并写入文本格式
                writer.write("标题: "+note.getTitle());
                writer.newLine();//换行

                writer.write("创建时间: "+note.getCreatedAt().format(formatter));
                writer.newLine();

                writer.write("最后修改: "+note.getUpdatedAt().format(formatter));
                writer.newLine();

                //将标签列表用","连接成一个字符串
                writer.write("标签: "+String.join(", ",note.getTags()));
                writer.newLine();

                writer.write("内容: ");
                writer.newLine();

                writer.write(note.getContent());
                writer.newLine();

                //在笔记之间添加分隔符，除非是最后一篇笔记
                if (i < notes.size() - 1) {
                    writer.write("---");
                    writer.newLine();
                    writer.newLine();
                }

            }
        }
    }


}