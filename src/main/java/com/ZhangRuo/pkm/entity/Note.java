package com.ZhangRuo.pkm.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 代表一个笔记的核心实体基类。4周支持序列化
 */
public class Note implements Serializable {//实现Serializable接口
    /**
     * 序列化版本UID，用于版本控制
     */
    private static final long serialVersionUID = 1L;  //添加seriaVersionUID

    private String id;
    private String title;
    private String content;

    /**
     * 内部标签集合，根据指导书要求，使用 List<String> 存储标签名。
     */
    private List<String> tags = new ArrayList<>();

    //LocalDateTime自身可序列化
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*
    * 为Jackson反序列化提供的无参数构造方法
    * 框架（如Jackson）需要这个构造方法来创建对象的空实例，然后再填充属性
    * Jackson 默认需要调用 Note 类的无参数构造方法（也叫默认构造方法），也就是 public Note() {}
    * */
    public Note() {
        //构造方法体可以是空的
    }


    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    /**
     * 为笔记添加一个标签 (通过标签名)。
     * 添加前会检查标签是否为 null、空白或已存在。
     *
     * @param tagName 要添加的标签名。
     */
    public void addTag(String tagName) {
        if (tagName != null && !tagName.isBlank() && !this.tags.contains(tagName)) {
            this.tags.add(tagName);
        }
    }

    /**
     * 从笔记中移除一个标签 (通过标签名)。
     *
     * @param tagName 要移除的标签名。
     */
    public void removeTag(String tagName) {
        this.tags.remove(tagName);
    }

    /**
     * 检查笔记是否包含指定的标签 (通过标签名)。
     *
     * @param tagName 要检查的标签名。
     * @return 如果包含则返回 true，否则返回 false。
     */
    public boolean hasTag(String tagName) {
        return this.tags.contains(tagName);
    }

    /**
     * 获取该笔记的所有标签名列表。
     *
     * @return 一个包含所有标签名的列表。
     */
    public List<String> getTags() {
        return this.tags;
    }

    // --- 兼容之前设计的、更健壮的方法 (参数为 Tag 对象) ---

    /**
     * [兼容方法] 为笔记添加一个标签 (通过 Tag 对象)。
     * 内部会提取 Tag 对象的名称并添加到列表中。
     *
     * @param tagObject 要添加的 Tag 对象。
     */
    public void addTag(Tag tagObject) {
        if (tagObject != null) {
            // 调用上面已经写好的、更健壮的 addTag(String) 方法
            this.addTag(tagObject.getName());
        }
    }

    /**
     * [兼容方法] 从笔记中移除一个标签 (通过 Tag 对象)。
     *
     * @param tagObject 要移除的 Tag 对象。
     */
    public void removeTag(Tag tagObject) {
        if (tagObject != null) {
            this.removeTag(tagObject.getName());
        }
    }

    /**
     * [兼容方法] 将标签名列表转换为 Tag 对象列表。
     * 便于未来需要使用 Tag 对象集合的场景。
     *
     * @return 一个包含所有标签的 Tag 对象列表。
     */
    public List<Tag> getTagObjects() {
        return this.tags.stream()
                .map(Tag::new) // 对每个 tagName 字符串，调用 Tag 的构造方法 new Tag(tagName)
                .collect(Collectors.toList());
    }

    // --- 其他 Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; this.updatedAt = LocalDateTime.now(); }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; this.updatedAt = LocalDateTime.now(); }
    public void setTags(List<String> tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ... equals, hashCode, toString ...
    @Override
    public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Note note = (Note) o; return Objects.equals(id, note.id); }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public String toString() { return "Note{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", tags=" + tags + '}'; }
}