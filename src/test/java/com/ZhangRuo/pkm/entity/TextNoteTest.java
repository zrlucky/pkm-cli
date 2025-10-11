package com.ZhangRuo.pkm.entity;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TextNote 类的单元测试。
 * 专注于测试 TextNote 类的构造、继承行为以及其独有的属性和方法。
 */
@DisplayName("TextNote 子类测试")
class TextNoteTest {

    private TextNote textNote;

    @BeforeEach
    void setUp() {
        // 在每个测试方法运行前，都创建一个全新的、干净的 TextNote 对象实例。
        textNote = new TextNote("Inheritance Title", "This is the content inherited from Note.");
    }

    // --- 继承与多态验证 ---

    @Test
    @DisplayName("✅ 构造方法应能正确调用父类并设置继承属性")
    void testConstructorSetsInheritedProperties() {
        // 这个测试验证 super(title, content) 是否被正确调用
        assertNotNull(textNote.getCreatedAt(), "继承自父类的 createdAt 属性不应为 null");
        assertEquals("Inheritance Title", textNote.getTitle(), "继承自父类的 title 属性应被正确设置");
        assertEquals("This is the content inherited from Note.", textNote.getContent(), "继承自父类的 content 属性应被正确设置");
    }

    @Test
    @DisplayName("✅ 子类对象应同时是父类的一个实例 (多态)")
    void testIsInstanceOfNote() {
        // 这个测试直接验证了面向对象的多态特性
        assertTrue(textNote instanceof Note, "TextNote 对象应该是 Note 类的一个实例");
    }

    @Test
    @DisplayName("✅ 子类应能成功使用从父类继承的方法")
    void testCanUseInheritedMethods() {
        // 这个测试验证了继承带来的代码复用能力
        Tag inheritedTag = new Tag("Inheritance");
        textNote.addTag(inheritedTag); // 调用继承来的 addTag() 方法
        assertTrue(textNote.getTags().contains(inheritedTag), "子类应该能够使用父类的 addTag() 方法");
        assertEquals(1, textNote.getTags().size());
    }

    // --- 子类特有功能测试 ---

    @Test
    @DisplayName("✅ 子类特有的 summary 属性的 Getter 和 Setter 应能正常工作")
    void testSummaryGetterAndSetter() {
        // 专门测试 TextNote 自己独有的功能
        String summaryText = "This is a brief summary.";

        // 1. 初始状态验证 (边界情况)
        assertNull(textNote.getSummary(), "新创建的 TextNote，其 summary 属性初始应为 null");

        // 2. Setter 功能验证
        textNote.setSummary(summaryText);

        // 3. Getter 功能验证
        assertEquals(summaryText, textNote.getSummary(), "getSummary() 应能返回由 setSummary() 设置的值");
    }
}