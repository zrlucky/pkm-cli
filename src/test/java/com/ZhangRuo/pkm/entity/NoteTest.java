

package com.ZhangRuo.pkm.entity; // 请确保这里的包名和你自己的一致


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Note 实体类测试 (混合模式)")
class NoteTest {

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note("Test Title", "Test Content");
    }

    @Test
    @DisplayName("✅ [String] 应能成功添加和获取标签名")
    void testAddAndGetTagsWithString() {
        note.addTag("Java");
        note.addTag("OOP");

        assertTrue(note.getTags().contains("Java"));
        assertEquals(2, note.getTags().size());
        assertTrue(note.hasTag("OOP"));
    }

    @Test
    @DisplayName("✅ [Tag Object] 应能通过Tag对象添加标签")
    void testAddTagWithTagObject() {
        Tag tagObject = new Tag("Legacy Support");
        note.addTag(tagObject);

        assertTrue(note.hasTag("Legacy Support"));
        assertEquals(1, note.getTags().size());
    }

    @Test
    @DisplayName("✅ 应能成功移除标签")
    void testRemoveTag() {
        String tagName = "Java";
        note.addTag(tagName);

        // 通过字符串移除
        note.removeTag(tagName);
        assertFalse(note.hasTag(tagName));
        assertTrue(note.getTags().isEmpty());

        // 通过对象移除
        Tag tagObject = new Tag("OOP");
        note.addTag(tagObject);
        note.removeTag(tagObject);
        assertFalse(note.hasTag("OOP"));
        assertTrue(note.getTags().isEmpty());
    }

    @Test
    @DisplayName("⚠️ 添加重复标签时应被忽略")
    void testAddDuplicateTag() {
        note.addTag("Java");
        note.addTag("Java"); // 重复添加字符串
        note.addTag(new Tag("Java")); // 重复添加同名Tag对象

        assertEquals(1, note.getTags().size(), "不应添加重复的标签");
    }
}