

package com.ZhangRuo.pkm.entity; // 请确保这里的包名和你自己的一致

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Note 类的单元测试。
 * 覆盖了 Note 类的正常功能和多种边界情况，旨在保证代码质量。
 */
@DisplayName("Note 实体类测试")
class NoteTest {

    // 声明两个成员变量，用于在各个测试方法中共享
    private Note note;
    private Tag tag;

    // 这个方法会在每一个 @Test 方法运行之前被执行一次
    @BeforeEach
    void setUp() {
        // 每次都创建全新的对象，确保测试之间互相独立，不受干扰
        note = new Note("Test Title", "Test Content");
        tag = new Tag("Important");
    }

    // --- 正常情况测试 (Happy Path Cases) ---

    @Test
    @DisplayName("✅ 构造方法应能正确创建Note对象并初始化属性")
    void testNoteCreation() {
        // 测试 Note 对象创建后，其核心属性是否符合预期
        assertNotNull(note.getCreatedAt(), "创建后，创建时间不应为null");
        assertEquals("Test Title", note.getTitle(), "创建后，标题应与提供的一致");
        assertTrue(note.getTags().isEmpty(), "新创建的Note，其标签集合应为空");
    }

    @Test
    @DisplayName("✅ 应能成功添加和移除标签")
    void testAddAndRemoveTag() {
        // --- 测试添加功能 ---
        note.addTag(tag);
        assertTrue(note.getTags().contains(tag), "添加标签后，列表应包含该标签");
        assertEquals(1, note.getTags().size(), "添加一个标签后，标签数量应为1");

        // --- 测试移除功能 ---
        note.removeTag(tag);
        assertFalse(note.getTags().contains(tag), "移除标签后，列表不应再包含该标签");
        assertTrue(note.getTags().isEmpty(), "移除最后一个标签后，列表应为空");
    }

    @Test
    @DisplayName("✅ 修改标题或内容应自动更新修改时间")
    void testSettersShouldUpdateTimestamp() throws InterruptedException {
        // 测试业务逻辑：setter方法是否会触发 updatedAt 时间戳的更新
        LocalDateTime initialUpdateTime = note.getUpdatedAt();
        Thread.sleep(10); // 强制等待10毫秒，确保系统时钟有足够的时间前进
        note.setTitle("A New Title");
        assertTrue(note.getUpdatedAt().isAfter(initialUpdateTime), "更新标题后，updatedAt时间戳应该更新");

        LocalDateTime titleUpdatedTime = note.getUpdatedAt();
        Thread.sleep(10);
        note.setContent("A new content.");
        assertTrue(note.getUpdatedAt().isAfter(titleUpdatedTime), "更新内容后，updatedAt时间戳应该再次更新");
    }

    // --- 边界与异常情况测试 (Boundary and Edge Cases) ---

    @Test
    @DisplayName("⚠️ 添加null标签时不应抛出异常且标签数不变")
    void testAddNullTagShouldBeIgnored() {
        // 这个测试专门验证代码的健壮性
        assertDoesNotThrow(() -> note.addTag(null), "添加null标签时不应该抛出任何异常");
        assertEquals(0, note.getTags().size(), "添加null标签后，标签数量不应改变");
    }

    @Test
    @DisplayName("⚠️ 移除一个不存在的标签时不应产生影响或异常")
    void testRemoveNonExistentTagShouldDoNothing() {
        Tag nonExistentTag = new Tag("Non-Existent");
        assertDoesNotThrow(() -> note.removeTag(nonExistentTag), "移除不存在的标签时不应该抛出任何异常");
        assertTrue(note.getTags().isEmpty(), "移除不存在的标签后，标签列表应保持不变");
    }

    @Test
    @DisplayName("⚠️ 使用null或空字符串作为标题创建Note对象应能成功")
    void testNoteCreationWithNullOrEmptyTitle() {
        Note noteWithNullTitle = new Note(null, "Content is fine.");
        assertNull(noteWithNullTitle.getTitle(), "用null标题创建的Note，其标题应为null");

        Note noteWithEmptyTitle = new Note("", "Content is fine.");
        assertEquals("", noteWithEmptyTitle.getTitle(), "用空字符串标题创建的Note，其标题应为空字符串");
    }
}