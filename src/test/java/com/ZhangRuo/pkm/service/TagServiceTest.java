package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TagService 核心功能测试")
class TagServiceTest {

    private TagService tagService;
    private Note note1, note2, note3;

    @BeforeEach
    void setUp() {
        // 在每个测试前都创建一个全新的 TagService 和几个 Note 对象
        tagService = new TagService();

        // 准备测试数据
        note1 = new Note("Java Basics", "Content about Java.");
        note1.addTag("Java");
        note1.addTag("Programming");

        note2 = new Note("OOP Concepts", "Content about OOP.");
        note2.addTag("Java");
        note2.addTag("OOP");

        note3 = new Note("Python Intro", "Content about Python.");
        note3.addTag("Python");
        note3.addTag("Programming");
    }

    @Test
    @DisplayName("✅ 应能正确建立索引并通过标签搜索笔记")
    void testIndexAndFindFunctionality() {
        // --- Act (执行操作) ---
        tagService.indexNote(note1);
        tagService.indexNote(note2);
        tagService.indexNote(note3);

        // --- Assert (断言验证) ---

        // 验证 "Java" 标签
        List<Note> javaNotes = tagService.findNotesByTag("Java");
        assertEquals(2, javaNotes.size(), "应该有2篇关于 Java 的笔记");
        assertTrue(javaNotes.contains(note1) && javaNotes.contains(note2), "Java 笔记列表应包含 note1 和 note2");

        // 验证 "Programming" 标签
        List<Note> programmingNotes = tagService.findNotesByTag("Programming");
        assertEquals(2, programmingNotes.size(), "应该有2篇关于 Programming 的笔记");
        assertTrue(programmingNotes.contains(note1) && programmingNotes.contains(note3), "Programming 笔记列表应包含 note1 和 note3");

        // 验证 "Python" 标签
        List<Note> pythonNotes = tagService.findNotesByTag("Python");
        assertEquals(1, pythonNotes.size(), "应该有1篇关于 Python 的笔记");
        assertTrue(pythonNotes.contains(note3));

        // 验证一个不存在的标签
        List<Note> nonExistentNotes = tagService.findNotesByTag("C++");
        assertTrue(nonExistentNotes.isEmpty(), "搜索不存在的标签应返回空列表");
    }

    @Test
    @DisplayName("✅ 应能正确统计所有标签的使用次数")
    void testTagStatistics() {
        // Act
        tagService.indexNote(note1);
        tagService.indexNote(note2);
        tagService.indexNote(note3);

        Map<String, Integer> stats = tagService.getTagStatistics();

        // Assert
        // --- 修正点 1 ---
        // 根据测试数据, 独立标签共有 "Java", "Programming", "OOP", "Python" 四个。
        assertEquals(4, stats.size(), "应该统计出4个独立的标签");

        // --- 修正点 2 ---
        // 逐个验证每个标签的计数值，并补上对 "OOP" 的验证。
        assertEquals(2, stats.get("Java"));
        assertEquals(2, stats.get("Programming"));
        assertEquals(1, stats.get("Python"));
        assertEquals(1, stats.get("OOP"));
    }

    @Test
    @DisplayName("✅ 应能获取所有不重复的标签")
    void testGetAllTags() {
        // Act
        // --- 修正点 3 ---
        // 索引所有笔记以获得完整的标签集
        tagService.indexNote(note1);
        tagService.indexNote(note2);
        tagService.indexNote(note3);

        // Assert
        // --- 修正点 4 ---
        // 确认独立标签的总数是 4
        assertEquals(4, tagService.getAllTags().size(), "应该获取到4个独立的标签");
        // 验证获取到的集合包含了所有我们期望的标签
        assertTrue(tagService.getAllTags().containsAll(List.of("Java", "Programming", "OOP", "Python")));
    }
}