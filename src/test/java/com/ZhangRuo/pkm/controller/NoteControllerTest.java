package com.ZhangRuo.pkm.controller;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.enums.ExportFormat;
import com.ZhangRuo.pkm.service.ExportService;
import com.ZhangRuo.pkm.service.NoteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoteController 交互逻辑测试 (全新)")
class NoteControllerTest {

    // --- Mock 依赖 ---
    @Mock
    private NoteService mockNoteService;
    @Mock
    private ExportService mockExportService;

    // --- 被测试的对象 ---
    @InjectMocks
    private NoteController noteController;

    // --- 用于捕获控制台输出的工具 ---
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        // 在每个测试前，都重置输出捕获工具
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        // 在每个测试后，恢复控制台输出
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // --- 测试 createNote ---
    @Test
    void testCreateNote_Success() {
        Note fakeNote = new Note("Title", "Content");
        fakeNote.setId("123");
        when(mockNoteService.createNote("t", "c")).thenReturn(fakeNote);

        noteController.createNote("t", "c");

        // 断言标准输出流
        String output = outContent.toString();
        assertTrue(output.contains("笔记创建成功"));
        assertTrue(output.contains("ID: 123"));
    }

    // --- 测试 listNotes ---
    @Test
    void testListNotes_WhenEmpty() {
        when(mockNoteService.getAllNotes()).thenReturn(Collections.emptyList());
        noteController.listNotes(null);
        assertTrue(outContent.toString().contains("没有找到符合条件的笔记"));
    }

    // --- 测试 viewNoteById ---
    @Test
    void testViewNoteById_Success() {
        Note fakeNote = new Note("Title", "Content");
        fakeNote.setId("123");
        fakeNote.setCreatedAt(LocalDateTime.now());
        fakeNote.setUpdatedAt(LocalDateTime.now());
        when(mockNoteService.findNoteById("123")).thenReturn(Optional.of(fakeNote));

        noteController.viewNoteById("123");

        assertTrue(outContent.toString().contains("ID: 123"));
        assertTrue(outContent.toString().contains("标题: Title"));
    }

    @Test
    void testViewNoteById_NotFound() {
        when(mockNoteService.findNoteById("456")).thenReturn(Optional.empty());
        noteController.viewNoteById("456");
        assertTrue(errContent.toString().contains("未找到ID为 '456' 的笔记"));
    }

    // --- 测试 deleteNoteById ---
    @Test
    void testDeleteNoteById_Success() {
        when(mockNoteService.deleteNote("123")).thenReturn(true);
        noteController.deleteNoteById("123");
        assertTrue(outContent.toString().contains("已被成功删除"));
    }

    @Test
    void testDeleteNoteById_NotFound() {
        when(mockNoteService.deleteNote("456")).thenReturn(false);
        noteController.deleteNoteById("456");
        assertTrue(errContent.toString().contains("删除失败"));
    }

    // --- 测试 editNote ---
    @Test
    void testEditNote_Success() {
        when(mockNoteService.updateNoteContent(eq("123"), anyString())).thenReturn(Optional.of(new Note("", "")));
        noteController.editNote("123", "new content");
        assertTrue(outContent.toString().contains("内容已成功更新"));
    }

    // --- 测试 searchNote ---
    @Test
    void testSearchNote_Found() {
        Note fakeNote = new Note("Title", "Content");
        fakeNote.setId("123");
        fakeNote.setCreatedAt(LocalDateTime.now());
        when(mockNoteService.searchNotesByKeyword("key")).thenReturn(List.of(fakeNote));

        noteController.searchNote("key");

        assertTrue(outContent.toString().contains("[123]"));
        assertTrue(outContent.toString().contains("Title"));
    }

    // --- 测试 exportNote ---
    @Test
    void testExportNote_Success() throws IOException {
        when(mockNoteService.findNoteById("123")).thenReturn(Optional.of(new Note("", "")));
        noteController.exportNote("123", "text", "file.txt");
        assertTrue(outContent.toString().contains("已成功导出到: file.txt"));
        // 验证 exportService 的方法被调用了
        verify(mockExportService, times(1)).exportNotes(anyList(), eq("file.txt"), eq(ExportFormat.TEXT));
    }

    // --- 测试 exportAllNotes ---
    @Test
    void testExportAllNotes_Success() throws IOException {
        when(mockNoteService.getAllNotes()).thenReturn(List.of(new Note("", "")));
        noteController.exportAllNotes("text", "file.txt");
        assertTrue(outContent.toString().contains("已成功导出到: file.txt"));
        verify(mockExportService, times(1)).exportNotes(anyList(), eq("file.txt"), eq(ExportFormat.TEXT));
    }
}