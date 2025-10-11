package com.ZhangRuo.pkm.entity;

import java.util.Objects;

/**
 * 代表一个纯文本笔记的实体类。
 * 它是 Note 的一个具体子类，继承了 Note 的所有基本属性和功能，
 * 并额外增加了一个 'summary' (摘要) 属性。
 */
public class TextNote extends Note {

    /**
     * 文本笔记的摘要信息。
     * 可以用来存放内容的简短概括。
     */
    private String summary;

    /**
     * TextNote 类的构造方法。
     *
     * @param title   笔记的标题，将传递给父类 Note 的构造方法。
     * @param content 笔记的内容，将传递给父类 Note 的构造方法。
     */
    public TextNote(String title, String content) {
        // 调用父类 Note 的构造方法来初始化 title, content, createdAt, updatedAt 等属性。
        // 'super' 关键字是对父类对象的引用。
        super(title, content);
    }

    // --- Getters and Setters for subclass-specific properties ---

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // --- 重写 toString (可选，但推荐) ---

    /**
     * 返回 TextNote 对象的字符串表示形式。
     * 在父类 Note 的基础上，额外追加了 summary 信息。
     *
     * @return 对象的字符串表示。
     */
    @Override
    public String toString() {
        return "TextNote{" +
                "id=" + getId() + ", " +       // 调用继承来的 getId() 方法
                "title='" + getTitle() + "', " + // 调用继承来的 getTitle() 方法
                "summary='" + summary + '\'' +
                '}';
    }
}