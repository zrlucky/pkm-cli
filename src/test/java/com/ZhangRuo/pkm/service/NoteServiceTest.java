package com.ZhangRuo.pkm.service; // 请确保这里的包名和你自己的一致

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.repository.JsonStorageService;
import com.ZhangRuo.pkm.repository.StorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NoteService 业务逻辑测试 (完整版)")
class NoteServiceTest {

    private static final String TEST_FILE_PATH = "test_notes_service_full.json";
    private StorageService storageService;
    private NoteService noteService;

    // --- 测试环境设置 ---
    @BeforeEach
    void setUp() {
        storageService = new JsonStorageService(TEST_FILE_PATH);
        noteService = new NoteService(storageService);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE_PATH).delete();
    }

    // --- 已有功能的测试 (保持不变) ---

    @Test
    @DisplayName("✅ createNote 应能成功创建笔记、生成ID并保存")
    void testCreateNote() {
        Note createdNote = noteService.createNote("New Title", "New Content");
        assertNotNull(createdNote.getId());
        assertEquals("New Title", createdNote.getTitle());
        assertEquals(1, storageService.load().size());
    }

    @Test
    @DisplayName("⚠️ createNote 当标题为空时应抛出异常")
    void testCreateNote_withEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> noteService.createNote("   ", "Content"));
    }

    @Test
    @DisplayName("✅ findNoteById 应能找到存在的笔记，找不到则返回空")
    void testFindNoteById() {
        Note createdNote = noteService.createNote("Find Me", "Content");
        assertTrue(noteService.findNoteById(createdNote.getId()).isPresent());
        assertTrue(noteService.findNoteById("non-existent-id").isEmpty());
    }

    @Test
    @DisplayName("✅ deleteNote 应能成功删除笔记")
    void testDeleteNote() {
        Note noteToDelete = noteService.createNote("To Be Deleted", "");
        noteService.createNote("To Be Kept", "");
        assertTrue(noteService.deleteNote(noteToDelete.getId()));
        assertEquals(1, storageService.load().size());
    }

    // --- 【新增功能的测试】 ---

    @Test
    @DisplayName("✅ findNoteByTag 应能根据标签正确过滤笔记")
    void testFindNoteByTag() {
        // Arrange: 准备带有不同标签的笔记
        Note noteJava = noteService.createNote("Java Note", "");
        noteJava.addTag("Java");
        noteJava.addTag("Programming");

        Note notePython = noteService.createNote("Python Note", "");
        notePython.addTag("Python");
        notePython.addTag("Programming");

        // 重新保存一次，确保 noteJava 的标签被持久化
        storageService.save(List.of(noteJava, notePython));

        // Act & Assert
        List<Note> javaNotes = noteService.findNoteByTag("Java");
        assertEquals(1, javaNotes.size());
        assertEquals("Java Note", javaNotes.get(0).getTitle());

        List<Note> programmingNotes = noteService.findNoteByTag("Programming");
        assertEquals(2, programmingNotes.size());

        List<Note> nonExistentTagNotes = noteService.findNoteByTag("C++");
        assertTrue(nonExistentTagNotes.isEmpty());
    }

    @Test
    @DisplayName("✅ updateNoteContent 应能成功更新笔记内容和时间戳")
    void testUpdateNoteContent() throws InterruptedException {
        // Arrange
        Note note = noteService.createNote("Original Title", "Original Content");
        String originalId = note.getId();
        LocalDateTime initialUpdateTime = note.getUpdatedAt();

        // 强制等待以确保时间戳有变化
        Thread.sleep(10);

        // Act
        Optional<Note> updatedNoteOpt = noteService.updateNoteContent(originalId, "Updated Content");

        // Assert
        assertTrue(updatedNoteOpt.isPresent());
        Note updatedNote = updatedNoteOpt.get();
        assertEquals("Updated Content", updatedNote.getContent());
        assertTrue(updatedNote.getUpdatedAt().isAfter(initialUpdateTime), "更新时间戳应该晚于初始时间");

        // 验证持久化
        Note loadedNote = storageService.load().get(0);
        assertEquals("Updated Content", loadedNote.getContent());
    }

    @Test
    @DisplayName("⚠️ updateNoteContent 当笔记不存在时应返回空")
    void testUpdateNoteContent_NotFound() {
        Optional<Note> result = noteService.updateNoteContent("non-existent-id", "any content");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("✅ searchNotesByKeyword 应能不区分大小写地搜索标题和内容")
    void testSearchNotesByKeyword() {
        // Arrange
        noteService.createNote("Learning Java", "Java is an object-oriented language.");
        noteService.createNote("Python Basics", "Learn the basics of python scripting.");
        noteService.createNote("Another Topic", "This note is not about programming.");

        // Act & Assert for title search (case-insensitive)
        List<Note> pythonNotes = noteService.searchNotesByKeyword("python");
        assertEquals(1, pythonNotes.size());
        assertEquals("Python Basics", pythonNotes.get(0).getTitle());

        // Act & Assert for content search (case-insensitive)
        List<Note> javaNotes = noteService.searchNotesByKeyword("OBJECT-ORIENTED");
        assertEquals(1, javaNotes.size());
        assertEquals("Learning Java", javaNotes.get(0).getTitle());

        // Act & Assert for no match
        List<Note> noMatchNotes = noteService.searchNotesByKeyword("non-existent-keyword");
        assertTrue(noMatchNotes.isEmpty());
    }
}