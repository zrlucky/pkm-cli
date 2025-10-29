package com.ZhangRuo.pkm.controller;

import com.ZhangRuo.pkm.entity.Note;
import com.ZhangRuo.pkm.service.NoteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;



@ExtendWith(MockitoExtension.class)//启用MOckito注释
@DisplayName("NoteController 交互逻辑测试")
class NoteControllerTest {

    //--- Mocking and DI ---
    @Mock //告诉Mockito :请为我创建一个NoteService的”假冒对象“
    private NoteService mockNoteService;

    @InjectMocks //告诉Mockito :请创建一个 NoteController 的真实案例，并把上面的 @Mock对象注入进去
    private NoteController noteController;

    //--- System.out 重定向 ---
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;


    @BeforeEach
    void setUpSteams() {
        //在每个测试前，将System.out的输出定向到我们的内存流
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        //在每个测试结束后，恢复System.out的正常输出
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("✅ createNote 成功时应打印成功信息")
    void testCreateNote_Success() {
        //1.Arrange(准备阶段)
        //准备一个当Service被调用时我们希望它返回的Note对象
        Note fakeNote = new Note("Fake Title", "Fake Content");
        fakeNote.setId("fake-id-123");

        //”录制“对象的行为
        //当mockNoteService 的createNote 方法被以"t","c"为参数调用时
        //那么(then) 就返回（return） 我们准备好的fakeNote对象
        when(mockNoteService.createNote("t","c")).thenReturn(fakeNote);

        //2.Act (执行阶段)
        //调用Controller方法，这将触发上面“录制”好的行为
        noteController.createNote("t","c");

        //3.Assert(断言阶段)
        //获取所有被重定向的输出
        String output = outContent.toString();

        //验证输出中是否包含了我们期望的成功信息
        assertTrue(output.contains("笔记创建成功！"));
        assertTrue(output.contains("ID: fake-id-123"));
        assertTrue(output.contains("标题: Fake Title"));

        //验证Service的方法是否真的被调用了1次
        verify(mockNoteService, times(1)).createNote("t","c");

    }

    @Test
    @DisplayName("✅ listAllNoes 当列表为空时应打印提示信息")
    void testListAllNotes_WhenEmpty() {
        //1.Arrange:录制行为
        //当调用getAllNotes()时，返回一个空的列表
        when(mockNoteService.getAllNotes()).thenReturn(Collections.emptyList());

        //2.Act
        noteController.listAllNotes();

        //3.Assert
        String output = outContent.toString();
        assertTrue(output.contains("当前没有任何笔记"));

    }

    @Test
    @DisplayName("✅ listAllNoes 当有笔记时应按格式打印")
    void testListAllNoes_WithData(){
        //1.Arrange
        Note note = new Note("Test Title", "Test Content");
        note.setId("test-id");
        note.addTag("test-tag");

        //录制行为：返回一个包含一个笔记的列表
        when(mockNoteService.getAllNotes()).thenReturn(List.of(note));

        //2.Act
        noteController.listAllNotes();

        //3.Assert
        String output = outContent.toString();
        //精确验证输出格式
        assertTrue(output.contains("[test-id] Test Title [test-tag]"));

    }


    @Test
    @DisplayName("✅ viewNoteById 成功时应打印笔记详情")
    void testViewNoteById_Success() {
        //1.Arrange
        Note fakeNote = new Note("View Title", "View Content");
        fakeNote.setId("view-id");
        fakeNote.addTag("view-tag");
        fakeNote.setCreatedAt(LocalDateTime.now());
        fakeNote.setUpdatedAt(LocalDateTime.now());

        //"录制"行为：当Service被调用查找"view-id"时，返回这个fakeNote
        when(mockNoteService.findNoteById("view-id")).thenReturn(Optional.of(fakeNote));

        //2.Act
        noteController.viewNoteById("view-id");

        //3.Assert
        String output = outContent.toString();
        assertTrue(output.contains("ID: view-id"));
        assertTrue(output.contains("标题: View Title"));
        assertTrue(output.contains("标签: view-tag"));
        assertTrue(output.contains("内容:\r\n View Content"));

    }

    @Test
    @DisplayName("❌ viewNoteById 失败时应打印错误信息")
    void testViewNoteById_NotFound() {
        // Arrange
        when(mockNoteService.findNoteById("not-found-id")).thenReturn(Optional.empty());

        // Act
        noteController.viewNoteById("not-found-id");

        // Assert: 确保我们从 errContent (错误流) 中读取
        String errorOutput = errContent.toString();
        // 验证 outContent (标准输出流) 是空的
        assertTrue(outContent.toString().isEmpty(), "成功时不应有任何标准输出");
        // 验证 errorOutput 包含我们期望的错误信息
        assertTrue(errorOutput.contains("错误: 未找到ID为 'not-found-id' 的笔记。"));
    }

    // --- deleteNoteById Tests  ---
    @Test
    @DisplayName("✅ deleteNoteById 成功时应打印成功信息")
    void testDeleteNoteById_Success() {
        // Arrange
        when(mockNoteService.deleteNote("delete-id")).thenReturn(true);

        // Act
        noteController.deleteNoteById("delete-id");

        // Assert: 确保我们从 outContent (标准输出流) 中读取
        String output = outContent.toString();
        // 验证 errContent (错误输出流) 是空的
        assertTrue(errContent.toString().isEmpty(), "成功时不应有任何错误输出");
        // 验证 output 包含我们期望的成功信息
        assertTrue(output.contains("笔记 (ID: delete-id) 已被成功删除。"));
    }

    @Test
    @DisplayName("❌ deleteNoteById 失败时应打印错误信息")
    void testDeleteNoteById_NotFound() {
        // Arrange
        when(mockNoteService.deleteNote("not-found-id")).thenReturn(false);

        // Act
        noteController.deleteNoteById("not-found-id");

        // Assert: 确保我们从 errContent (错误流) 中读取
        String errorOutput = errContent.toString();
        // 验证 outContent (标准输出流) 是空的
        assertTrue(outContent.toString().isEmpty(), "失败时不应有任何标准输出");
        // 验证 errorOutput 包含我们期望的错误信息
        assertTrue(errorOutput.contains("错误: 未找到ID为 'not-found-id' 的笔记，删除失败。"));
    }




}