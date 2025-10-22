package com.ZhangRuo.pkm.repository;


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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NoteFileRepository 持久化功能测试")
class NoteFileRepositoryTest {

    // 使用一个临时的测试文件名，避免污染项目目录
    private static final String TEST_DATA_FILE = "test_notes.dat";
    private NoteFileRepository repository;
    private File testFile;

    private static final String TEST_EXPORT_FILE = "test_export.txt";  //4.4.1添加新的常量，用于导出测试

    @BeforeEach
    void setUp() {
        repository = new NoteFileRepository(TEST_DATA_FILE);
        testFile = new File(TEST_DATA_FILE);
    }

    @AfterEach
    void tearDown() {
        // 每个测试结束后，都删除生成的测试文件和备份文件，确保测试环境干净
        new File(TEST_DATA_FILE).delete();
        new File(TEST_DATA_FILE + ".backup").delete();

        new File(TEST_EXPORT_FILE).delete();//4.4.2清理导出文件
    }

    @Test
    @DisplayName("✅ 应能成功保存和加载笔记列表")
    void testSaveAndLoadNotes_HappyPath() throws Exception {
        // 1. 准备数据
        List<Note> notesToSave = new ArrayList<>();
        Note note1 = new Note("Title 1", "Content 1");
        note1.addTag("test");
        notesToSave.add(note1);

        // 2. 执行保存
        repository.saveNotes(notesToSave);

        // 3. 执行加载
        List<Note> loadedNotes = repository.loadNotes();

        // 4. 验证
        assertNotNull(loadedNotes);
        assertEquals(1, loadedNotes.size());
        assertEquals("Title 1", loadedNotes.get(0).getTitle());
        assertTrue(loadedNotes.get(0).hasTag("test"));
    }

    @Test
    @DisplayName("✅ 从不存在的文件加载时应返回空列表")
    void testLoadNotes_WhenFileDoesNotExist() throws Exception {
        // 确保文件不存在
        assertFalse(testFile.exists());

        List<Note> loadedNotes = repository.loadNotes();

        assertNotNull(loadedNotes);
        assertTrue(loadedNotes.isEmpty(), "从不存在的文件加载，应返回一个空列表");
    }

    @Test
    @DisplayName("✅ 多次保存应能正确覆盖")
    void testSaveNotes_Overwriting() throws Exception {
        // 第一次保存
        List<Note> firstSave = new ArrayList<>();
        firstSave.add(new Note("First", ""));
        repository.saveNotes(firstSave);

        // 第二次保存
        List<Note> secondSave = new ArrayList<>();
        secondSave.add(new Note("Second", ""));
        secondSave.add(new Note("Another", ""));
        repository.saveNotes(secondSave);

        // 加载并验证
        List<Note> loadedNotes = repository.loadNotes();
        assertEquals(2, loadedNotes.size());
        assertEquals("Second", loadedNotes.get(0).getTitle());
    }

    // [高级] 测试备份恢复机制是一个复杂的话题，通常需要用到 Mocking 或更复杂的I/O注入。
    // 在这里，我们通过验证备份文件是否被正确创建来间接证明备份机制的一部分。
    @Test
    @DisplayName("✅ 保存时应创建备份文件")
    void testSaveNotes_ShouldCreateBackup() throws Exception {
        File backupFile = new File(TEST_DATA_FILE + ".backup");

        // 初始状态，备份文件不应存在
        assertFalse(backupFile.exists());

        List<Note> notes = new ArrayList<>();
        notes.add(new Note("Note", ""));

        // 第一次保存，此时没有旧文件，不应创建备份
        repository.saveNotes(notes);
        assertFalse(backupFile.exists());

        // 第二次保存，此时有旧文件，应该创建备份
        repository.saveNotes(notes);
        assertTrue(backupFile.exists(), "在覆盖保存前，应该创建备份文件");
    }

//    4.4.3正确格式导出
    @Test
    @DisplayName("✅ 笔记应能以正确格式导出")
    void testExportNotes_TextFormat() throws Exception {
        //1.准备数据
        List<Note> notesToExport = new ArrayList<>();
        Note note1 = new Note("Title 1", "Content 1");
        note1.addTag("java");
        note1.addTag("test");

        //为了更精确地测试时间，我们固定一下时间
        note1.setCreatedAt(java.time.LocalDateTime.of(2023, 10, 1, 10, 0, 0));
        note1.setUpdatedAt(java.time.LocalDateTime.of(2023, 10, 2, 11, 30, 0));
        notesToExport.add(note1);

        Note note2 = new Note("Title 2", "Content 2");
        note2.addTag("python");
        notesToExport.add(note2);

        //2.构建期望的输出字符串
        //我们手动拼接一个完全符合格式要求的“标准答案”字符串
        String expectedContent =
                "标题: Title 1\r\n"+
                "创建时间: 2023-10-01 10:00:00\r\n"+
                "最后修改: 2023-10-02 11:30:00\r\n"+
                "标签: java, test\r\n"+
                "内容: \r\n"+
                "Content 1\r\n"+
                "---\r\n\r\n"+
                "标题: Title 2\r\n"+
                "创建时间: "+note2.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"\r\n"+
                "最后修改: "+note2.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"\r\n"+
                "标签: python\r\n"+
                "内容: \r\n"+
                "Content 2\r\n";

        //3.执行导出操作
        repository.exportNotes(notesToExport,TEST_EXPORT_FILE, ExportFormat.TEXT);

        //4.验证文件是否被创建
        File exportedFile = new File(TEST_EXPORT_FILE);
        assertTrue(exportedFile.exists(),"导出文件应该被创建");

        //5.[关键]读取文件并验证格式和完整新性
        String actualContent = Files.readString(Path.of(TEST_EXPORT_FILE));

        //验证内容,对比
        assertEquals(expectedContent,actualContent,"导出的文件内容和格式应与预期完全一致");



    }

}