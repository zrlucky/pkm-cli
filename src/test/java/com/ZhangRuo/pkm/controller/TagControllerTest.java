package com.ZhangRuo.pkm.controller;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.service.TagService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("TagController 交互逻辑测试")
class TagControllerTest {

    @Mock
    private TagService mockTagService;

    @InjectMocks
    private TagController tagController;

    // --- System.out 和 System.err 重定向 ---
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("✅ addTagToNote 成功时应打印成功信息")
    void testAddTagToNote_Success() {
        // 1. Arrange
        Note updatedNote = new Note("Test Note", "");
        updatedNote.addTag("new-tag");

        // "录制"行为: 当调用 addTagToNote 并传入 "note-1" 和 "new-tag" 时,
        // 返回一个包含 updatedNote 的 Optional。
        when(mockTagService.addTagToNote("note-1", "new-tag"))
                .thenReturn(Optional.of(updatedNote));

        // 2. Act
        tagController.addTagToNote("note-1", "new-tag");

        // 3. Assert
        String output = outContent.toString();
        assertTrue(output.contains("标签 'new-tag' 已成功添加到笔记 'Test Note'"));
        assertTrue(output.contains("当前标签: new-tag"));
    }

    @Test
    @DisplayName("❌ addTagToNote 当笔记不存在时应打印错误信息")
    void testAddTagToNote_NoteNotFound() {
        // 1. Arrange
        // "录制"行为: 当调用 addTagToNote 并传入一个不存在的ID时, 返回一个空的 Optional。
        when(mockTagService.addTagToNote("non-existent-id", "any-tag"))
                .thenReturn(Optional.empty());

        // 2. Act
        tagController.addTagToNote("non-existent-id", "any-tag");

        // 3. Assert
        // 这次我们验证错误输出流
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("错误: 未找到ID为 'non-existent-id' 的笔记"));
    }

    @Test
    @DisplayName("✅ removeTagFromNote 成功时应打印成功信息")
    void testRemoveTagFromNote_Success() {
        // 1. Arrange
        Note updatedNote = new Note("Test Note", ""); // 移除后，标签列表为空

        when(mockTagService.removeTagFromNote("note-1", "old-tag"))
                .thenReturn(Optional.of(updatedNote));

        // 2. Act
        tagController.removeTagFromNote("note-1", "old-tag");

        // 3. Assert
        String output = outContent.toString();
        assertTrue(output.contains("标签 'old-tag' 已成功从笔记 'Test Note' 移除"));
    }
}