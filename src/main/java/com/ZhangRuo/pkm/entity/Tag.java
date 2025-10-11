package com.ZhangRuo.pkm.entity;

import java.util.Objects;

/**
 * 代表一个标签的实体类。
 * Tag 对象通过其 'name' 属性来定义，并通过 'id' 进行唯一标识。
 */
public class Tag {

    /**
     * 标签的唯一标识符，通常由数据库生成。
     */
    private Long id;

    /**
     * 标签的名称，例如 "Java", "OOP" 等。
     */
    private String name;

    /**
     * Tag 类的构造方法。
     * 创建一个标签时，必须为其提供一个名称。
     *
     * @param name 标签的名称，不应为 null。
     */
    public Tag(String name) {
        this.name = name;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // --- 重写 equals, hashCode, toString (专业实践) ---

    /**
     * 比较两个 Tag 对象是否相等。
     * 如果两个 Tag 的 id 相同，则认为它们是相等的。
     * 这对于在 Set 集合中正确处理 Tag 对象至关重要。
     *
     * @param o 要比较的对象。
     * @return 如果对象相等则返回 true，否则返回 false。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id); // 关键：仅基于id判断相等性
    }

    /**
     * 根据对象的 id 生成哈希码。
     * 与 equals() 方法保持一致，是使用 HashSet 的必要条件。
     *
     * @return 对象的哈希码。
     */
    @Override
    public int hashCode() {
        return Objects.hash(id); // 关键：仅基于id生成哈希码
    }

    /**
     * 返回 Tag 对象的字符串表示形式，方便调试。
     *
     * @return 对象的字符串表示。
     */
    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}