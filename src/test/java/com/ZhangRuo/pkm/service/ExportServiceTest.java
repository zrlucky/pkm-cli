package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.enums.ExportFormat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExportService 业务逻辑测试 (全新)")
class ExportServiceTest {

    // --- 测试环境设置 ---
    private static final String TEST_FILE_PATH = "test_export_output.txt";
    private ExportService exportService;
    private File testFile;

    @BeforeEach
    void setUp() {
        exportService = new ExportService();
        testFile = new File(TEST_FILE_PATH);
        // 确保每次测试开始前，旧的测试文件都被删除
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // 每个测试结束后再次尝试清理，确保环境干净
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    // --- 核心功能测试 ---
    @Test
    @DisplayName("✅ exportNotes 应能按指定格式正确导出多篇笔记")
    void testExportNotes_TextFormat_WithMultipleNotes() throws IOException {
        // 1. --- 准备要导出的数据 (Arrange) ---
        List<Note> notesToExport = new ArrayList<>();

        Note note1 = new Note("Java Note", "Content for note 1.");
        note1.addTag("Java");
        // 使用固定的时间，以确保测试结果是可预测的
        note1.setCreatedAt(LocalDateTime.of(2023, 10, 20, 14, 30, 0));
        note1.setUpdatedAt(LocalDateTime.of(2023, 10, 21, 15, 0, 0));
        notesToExport.add(note1);

        Note note2 = new Note("Python Note", "Content for note 2.");
        note2.addTag("Python");
        note2.addTag("Script");
        note2.setCreatedAt(LocalDateTime.of(2023, 11, 1, 9, 0, 0));
        note2.setUpdatedAt(LocalDateTime.of(2023, 11, 2, 10, 0, 0));
        notesToExport.add(note2);

        // 2. --- 构造期望的输出字符串 (Arrange) ---
        // 使用 System.lineSeparator() 保证跨平台兼容性
        String EOL = System.lineSeparator();
        // 关键：日期格式必须与 ExportService 中的 "yyyy/MM/dd HH:mm:ss" 完全一致！
        String expectedContent =
                "标题：Java Note" + EOL +
                        "创建时间：2023/10/20 14:30:00" + EOL +
                        "最后修改：2023/10/21 15:00:00" + EOL +
                        "标签：Java" + EOL +
                        "内容：" + EOL +
                        "Content for note 1." + EOL +
                        "---" + EOL +
                        "标题：Python Note" + EOL +
                        "创建时间：2023/11/01 09:00:00" + EOL +
                        "最后修改：2023/11/02 10:00:00" + EOL +
                        "标签：Python, Script" + EOL +
                        "内容：" + EOL +
                        "Content for note 2." + EOL;

        // 3. --- 执行要测试的方法 (Act) ---
        exportService.exportNotes(notesToExport, TEST_FILE_PATH, ExportFormat.TEXT);

        // 4. --- 验证结果 (Assert) ---
        assertTrue(testFile.exists(), "导出文件应该被创建");

        // 读取实际生成的文件内容
        String actualContent = Files.readString(Path.of(TEST_FILE_PATH));

        // 精确对比期望内容和实际内容
        assertEquals(expectedContent, actualContent, "导出的文件内容和格式应与预期完全一致");
    }

    // --- 边界情况测试 ---
    @Test
    @DisplayName("✅ exportNotes 当导出空列表时应生成一个空文件")
    void testExportNotes_WithEmptyList() throws IOException {
        // Arrange
        List<Note> emptyList = new ArrayList<>();

        // Act
        exportService.exportNotes(emptyList, TEST_FILE_PATH, ExportFormat.TEXT);

        // Assert
        assertTrue(testFile.exists(), "即使列表为空，文件也应该被创建");
        assertEquals(0, testFile.length(), "导出空列表时，生成的文件大小应为0");
    }

    @Test
    @DisplayName("✅ exportNotes 当笔记内容或标签为空时应能正确处理")
    void testExportNotes_WithEmptyContentOrTags() throws IOException {
        // Arrange
        Note note = new Note("Note with empty parts", ""); // 内容为空
        // 标签列表默认就是空的
        note.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        note.setUpdatedAt(LocalDateTime.of(2023, 1, 1, 11, 0));

        String EOL = System.lineSeparator();
        String expectedContent =
                "标题：Note with empty parts" + EOL +
                        "创建时间：2023/01/01 10:00:00" + EOL +
                        "最后修改：2023/01/01 11:00:00" + EOL +
                        "标签：" + EOL + // String.join 会对空列表生成空字符串
                        "内容：" + EOL +
                        "" + EOL; // 内容为空，所以这里只有一个换行

        // Act
        exportService.exportNotes(List.of(note), TEST_FILE_PATH, ExportFormat.TEXT);

        // Assert
        String actualContent = Files.readString(Path.of(TEST_FILE_PATH));
        assertEquals(expectedContent, actualContent);
    }
}