package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.repository.JsonStorageService;
import com.ZhangRuo.pkm.repository.StorageService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NoteService 业务能力测试")
class NoteServiceTest {

    private static String TEST_FILE_PATH="test_notes_service.json";
    private StorageService storageService;
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        //使用真实的JsonStorageService和一个临时文件进行测试
        storageService = new JsonStorageService(TEST_FILE_PATH);
        noteService = new NoteService(storageService);
    }

    @AfterEach
    void tearDown() {
        //每个测试结束后清理临时文件
        new File(TEST_FILE_PATH).delete();
    }

    @Test
    @DisplayName("✅ createNote 应能成功创建笔记、生成ID并保存")
    void createNote() {
        //Act
        Note createNote = noteService.createNote("New Title", "New Content");

        //Assert
        assertNotNull(createNote.getId(),"创建笔记必须有ID");
        assertEquals("New Title", createNote.getTitle());

        //直接通过StorageService验证数据是否真的被持久化了
        List<Note> noteInStorage =storageService.load();
        assertEquals(1, noteInStorage.size());
        assertEquals(createNote.getId(), noteInStorage.get(0).getId());

    }

    @Test
    @DisplayName("⚠ createNote 当标题为空时应抛出异常")
    void testCreateNote_withEmptyTitle() {
        //使用assertThrows验证异常情况
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> noteService.createNote("  ", "Content") //标题是纯空格
        );
        assertEquals("标题不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("✅ findNoteById 应能找到存在的笔记，找不到则返回空")
    void findNoteById() {
        //Arrange
        Note createNote = noteService.createNote("New Title", "New Content");

        //Act & Assert for existing note
        Optional<Note> findNoote = noteService.findNoteById(createNote.getId());
        assertTrue(findNoote.isPresent(),"应该能找到已创建的笔记");
        assertEquals(createNote.getId(), findNoote.get().getId());

        //Act & Assert for non-existing note
        Optional<Note> notFoundNote = noteService.findNoteById("non_existent_id");
        assertTrue(notFoundNote.isEmpty(),"查找不存在的ID时应返回空的Optional");
    }

    @Test
    @DisplayName("✅ deleteNote 应能成功删除笔记")
    void testDeleteNote() {
        //Arrange
        Note noteToDelete = noteService.createNote("To be Deleted", "New Content");
        noteService.createNote("To be Kept", "New Content");



        //Act
        boolean result = noteService.deleteNote(noteToDelete.getId());

        //Assert
        assertTrue(result,"删除成功时应返回true");
        List<Note> remainingNotes =storageService.load();
        assertEquals(1, remainingNotes.size(),"删除后应只剩一篇笔记");
        assertEquals("To be Kept", remainingNotes.get(0).getTitle());
    }
}