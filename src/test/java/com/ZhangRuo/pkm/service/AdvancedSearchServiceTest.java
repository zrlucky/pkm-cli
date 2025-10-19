package com.ZhangRuo.pkm.service;


import com.ZhangRuo.pkm.entity.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdvancedSearchService 高级功能测试")
class AdvancedSearchServiceTest {

    private AdvancedSearchService advancedSearchService;
    private List<Note> testNotes;
    private Note note1, note2, note3, note4;

    @BeforeEach
    void setUp() {
        advancedSearchService = new AdvancedSearchService();
        testNotes = new ArrayList<>();

        note1 = new Note("Java Intro", "");
        note1.addTag("Java");
        note1.addTag("Programming");

        note2 = new Note("OOP Concepts", "");
        note2.addTag("Java");
        note2.addTag("OOP");

        note3 = new Note("Python Basics", "");
        note3.addTag("Python");
        note3.addTag("Programming");

        note4 = new Note("Full Stack Java", "");
        note4.addTag("Java");
        note4.addTag("Programming");
        note4.addTag("OOP");

        testNotes.addAll(List.of(note1, note2, note3, note4));
    }

    @Test
    @DisplayName("⭐ [高级功能] 应能找出同时包含多个指定标签的笔记 (AND)")
    void testSearchByTagsWithAndCondition() {
        // (此测试用例之前是正确的, 无需修改)
        List<String> requiredTags1 = List.of("Java", "OOP");
        List<Note> result1 = advancedSearchService.searchByTags(testNotes, requiredTags1);
        assertEquals(2, result1.size());
        assertTrue(result1.containsAll(List.of(note2, note4)));

        List<String> requiredTags2 = List.of("Java", "Programming", "OOP");
        List<Note> result2 = advancedSearchService.searchByTags(testNotes, requiredTags2);
        assertEquals(1, result2.size());
        assertTrue(result2.contains(note4));

        List<String> requiredTags3 = List.of("Java", "Python");
        List<Note> result3 = advancedSearchService.searchByTags(testNotes, requiredTags3);
        assertTrue(result3.isEmpty());
    }

    @Test
    @DisplayName("⭐ [高级功能] 应能使用 Stream API 正确统计标签云数据")
    void testGenerateTagCloudWithStreamAPI() {
        Map<String, Long> tagCloud = advancedSearchService.generateTagCloud(testNotes);

        assertEquals(4, tagCloud.size(), "应统计出4个独立标签");
        assertEquals(3L, tagCloud.get("Java"));
        // --- 修正点 1 ---
        // "Programming" 标签在 note1, note3, note4 中都出现了, 总共3次。
        assertEquals(3L, tagCloud.get("Programming"));
        assertEquals(2L, tagCloud.get("OOP"));
        assertEquals(1L, tagCloud.get("Python"));
    }

    @Test
    @DisplayName("⭐ [可选功能] 应能根据关键字模糊匹配标签")
    void testSearchByTagKeyword() {
        List<Note> result = advancedSearchService.searchByTagKeyword(testNotes, "Pro");

        // --- 修正点 2 ---
        // 期望找到的笔记数量应该是 3 (note1, note3, note4)
        assertEquals(3, result.size(), "应找到3篇标签中包含'Pro'的笔记");
        // --- 修正点 3 ---
        // 验证结果列表包含了所有匹配的笔记
        assertTrue(result.containsAll(List.of(note1, note3, note4)), "结果应包含note1, note3和note4");
    }
}