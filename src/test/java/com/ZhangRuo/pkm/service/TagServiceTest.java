package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.repository.JsonStorageService;
import com.ZhangRuo.pkm.repository.StorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TagService 业务逻辑测试")
class TagServiceTest {

    private static final String TEST_FILE_PATH = "test_notes_tag_service.json";
    private StorageService storageService;
    private TagService tagService;
    private Note testNote;

    @BeforeEach
    void setUp() {
        storageService = new JsonStorageService(TEST_FILE_PATH);
        tagService = new TagService(storageService);

        // 准备一个已存在的笔记用于测试
        testNote = new Note("Initial Title", "Initial Content");
        testNote.setId("test-id-123");
        testNote.addTag("existing-tag");

        List<Note> initialNotes = new ArrayList<>();
        initialNotes.add(testNote);
        storageService.save(initialNotes);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE_PATH).delete();
    }

    @Test
    @DisplayName("✅ addTagToNote 应能为存在的笔记成功添加标签")
    void testAddTagToNote_Success() {
        // Act
        Optional<Note> updatedNoteOpt = tagService.addTagToNote("test-id-123", "new-tag");

        // Assert
        assertTrue(updatedNoteOpt.isPresent(), "操作成功应返回包含Note的Optional");
        assertTrue(updatedNoteOpt.get().hasTag("new-tag"), "更新后的笔记应包含新标签");
        assertTrue(updatedNoteOpt.get().hasTag("existing-tag"), "更新后的笔记应保留旧标签");
        assertEquals(2, updatedNoteOpt.get().getTags().size());

        // 验证持久化
        List<Note> notesInStorage = storageService.load();
        assertTrue(notesInStorage.get(0).hasTag("new-tag"));
    }

    @Test
    @DisplayName("⚠️ addTagToNote 当笔记不存在时应返回空")
    void testAddTagToNote_NoteNotFound() {
        // Act
        Optional<Note> result = tagService.addTagToNote("non-existent-id", "any-tag");

        // Assert
        assertTrue(result.isEmpty(), "当笔记不存在时，应返回空的Optional");
    }

    @Test
    @DisplayName("✅ removeTagFromNote 应能为存在的笔记成功移除标签")
    void testRemoveTagFromNote_Success() {
        // Act
        Optional<Note> updatedNoteOpt = tagService.removeTagFromNote("test-id-123", "existing-tag");

        // Assert
        assertTrue(updatedNoteOpt.isPresent());
        assertFalse(updatedNoteOpt.get().hasTag("existing-tag"), "更新后的笔记不应再包含被移除的标签");
        assertEquals(0, updatedNoteOpt.get().getTags().size());

        // 验证持久化
        List<Note> notesInStorage = storageService.load();
        assertFalse(notesInStorage.get(0).hasTag("existing-tag"));
    }
}