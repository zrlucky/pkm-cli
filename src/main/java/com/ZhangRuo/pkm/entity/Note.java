package com.ZhangRuo.pkm.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 代表一个笔记的核心实体基类。
 * 包含了笔记的基本属性（ID, 标题, 内容, 时间戳）以及标签管理功能。
 */
public class Note {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 与 Tag 类的多对多关联关系。
     * 使用 HashSet 保证标签的唯一性。
     */
    private Set<Tag> tags = new HashSet<>();

    /**
     * Note 类的构造方法。
     * 创建一个笔记时，必须提供标题和内容。
     *
     * @param title   笔记的标题。
     * @param content 笔记的内容。
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- 核心业务方法 ---

    /**
     * 为当前笔记添加一个标签。
     * 此方法是健壮的：如果传入的标签为 null，则不会执行任何操作。
     *
     * @param tag 要添加的标签对象，建议不为 null。
     */
    public void addTag(Tag tag) {
        if (tag != null) {
            this.tags.add(tag);
        }
    }

    /**
     * 从当前笔记中移除一个标签。
     *
     * @param tag 要移除的标签对象。
     */
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * 设置笔记的标题，并自动更新 'updatedAt' 时间戳。
     * @param title 新的标题。
     */
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now(); // 关键逻辑：修改属性时更新时间
    }

    public String getContent() {
        return content;
    }

    /**
     * 设置笔记的内容，并自动更新 'updatedAt' 时间戳。
     * @param content 新的内容。
     */
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now(); // 关键逻辑：修改属性时更新时间
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    // --- 重写 equals, hashCode, toString (专业实践) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tags=" + tags.size() + // 避免打印过多tag信息
                '}';
    }
}