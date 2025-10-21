package com.ZhangRuo.pkm.repository;


import com.ZhangRuo.pkm.entity.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NoteFileRepository 持久化功能测试")
class NoteFileRepositoryTest {

    // 使用一个临时的测试文件名，避免污染项目目录
    private static final String TEST_DATA_FILE = "test_notes.dat";
    private NoteFileRepository repository;
    private File testFile;

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
}