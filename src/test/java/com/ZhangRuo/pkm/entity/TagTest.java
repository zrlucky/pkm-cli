package com.ZhangRuo.pkm.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tag 类的单元测试。
 * 覆盖了 Tag 类的正常功能和边界情况，并验证了 equals 和 hashCode 合约。
 */
@DisplayName("Tag 实体类测试")
class TagTest {

    // --- 正常情况测试 (Happy Path Cases) ---

    @Test
    @DisplayName("✅ 构造方法应能正确创建Tag对象")
    void testTagCreation() {
        String tagName = "Java Programming";
        Tag tag = new Tag(tagName);
        assertEquals(tagName, tag.getName(), "通过构造函数设置的 name 应能被正确获取");
        assertNull(tag.getId(), "新创建的 Tag，其 id 初始应为 null");
    }

    @Test
    @DisplayName("✅ Getters 和 Setters 应能正常工作")
    void testGettersAndSetters() {
        Tag tag = new Tag("Initial Name");
        tag.setName("New Name");
        tag.setId(123L);
        assertEquals("New Name", tag.getName(), "setName/getName 应该正常工作");
        assertEquals(123L, tag.getId(), "setId/getId 应该正常工作");
    }

    // --- 边界与合约测试 (Boundary and Contract Cases) ---

    @Test
    @DisplayName("⚠️ set/getName 应能处理null、空字符串和纯空格")
    void testSetNameWithNullOrEmpty() {
        Tag tag = new Tag("Initial");

        // 测试 null
        tag.setName(null);
        assertNull(tag.getName(), "名称可以被设置为 null");

        // 测试空字符串
        tag.setName("");
        assertEquals("", tag.getName(), "名称可以被设置为空字符串");

        // 测试纯空格字符串
        String spaces = "   ";
        tag.setName(spaces);
        assertEquals(spaces, tag.getName(), "名称可以被设置为纯空格字符串");
    }

    @Test
    @DisplayName("📜 应严格遵守 equals 和 hashCode 合约")
    void testEqualsAndHashCodeContract() {
        // 准备对象
        Tag tag1 = new Tag("Java");
        tag1.setId(1L);

        Tag tag2 = new Tag("Java");
        tag2.setId(1L);

        Tag tag3 = new Tag("Python");
        tag3.setId(2L);

        // 1. 自反性: x.equals(x) 必须为 true
        assertTrue(tag1.equals(tag1), "一个对象必须等于它自己");

        // 2. 对称性: 如果 x.equals(y) 为 true, 那么 y.equals(x) 也必须为 true
        assertTrue(tag1.equals(tag2), "ID相同的两个对象应该相等");
        assertTrue(tag2.equals(tag1), "equals 应该是对称的");

        // 3. 验证 hashCode 合约: 如果 x.equals(y) 为 true, 那么 x.hashCode() 必须等于 y.hashCode()
        assertEquals(tag1.hashCode(), tag2.hashCode(), "ID相同的两个对象，其hashCode必须相等");

        // 4. 与不同ID的对象比较
        assertFalse(tag1.equals(tag3), "ID不同的两个对象不应该相等");

        // 5. 与 null 比较
        assertFalse(tag1.equals(null), "对象与null比较，必须返回false");

        // 6. 与不同类型的对象比较
        assertFalse(tag1.equals("a string object"), "对象与不同类型的对象比较，必须返回false");
    }
}