package com.ZhangRuo.pkm.service;


import com.ZhangRuo.pkm.entity.Note; // 导入我们之前创建的 Note 类

import java.util.*;

/**
 * TagService 提供了标签系统的核心业务逻辑。
 * 它负责建立标签到笔记的索引，并提供基于标签的搜索和统计功能。
 */
public class TagService {

    /**
     * 核心数据结构：一个倒排索引。
     * Key: 标签名 (String)
     * Value: 包含该标签的所有 Note 对象的列表 (List<Note>)
     */
    private Map<String, List<Note>> tagMap = new HashMap<>();

    /**
     * 为单个笔记建立索引。
     * 遍历笔记的所有标签，并将笔记添加到 tagMap 中每个标签对应的列表里。
     *
     * @param note 需要被索引的 Note 对象。
     */
    public void indexNote(Note note) {
        if (note == null) {
            return; // 防御性编程：如果 note 为 null，则直接返回
        }
        // 遍历 note 中的每一个标签名
        for (String tagName : note.getTags()) {
            // 使用 computeIfAbsent (指导书中的技术要点)
            // 1. 尝试获取 tagName 对应的列表。
            // 2. 如果列表不存在(absent), 则执行 -> 后面的 Lambda 表达式 (k -> new ArrayList<>())，
            //    创建一个新的空 ArrayList，将其与 tagName 关联，并返回这个新列表。
            // 3. 如果列表已存在，则直接返回现有列表。
            // 4. .add(note): 无论上面返回的是新列表还是旧列表，都将当前 note 对象添加进去。
            tagMap.computeIfAbsent(tagName, k -> new ArrayList<>()).add(note);
        }
    }

    /**
     * 根据标签名搜索所有相关的笔记。
     *
     * @param tag 要搜索的标签名。
     * @return 包含该标签的所有 Note 对象的列表。如果找不到，则返回一个空列表。
     */
    public List<Note> findNotesByTag(String tag) {
        // 使用 getOrDefault 方法避免空指针异常。
        // 如果能找到 tag 对应的列表，就返回它；否则，返回一个空的 ArrayList。
        return tagMap.getOrDefault(tag, new ArrayList<>());
    }

    /**
     * 获取系统中存在的所有标签。
     *
     * @return 一个包含所有不重复标签名的 Set 集合。
     */
    public Set<String> getAllTags() {
        // Map 的 keySet() 方法直接返回一个包含所有 key 的 Set 集合。
        return tagMap.keySet();
    }

    /**
     * 统计每个标签被使用了多少次（即关联了多少篇笔记）。
     *
     * @return 一个 Map，Key 是标签名，Value 是该标签关联的笔记数量。
     */
    public Map<String, Integer> getTagStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        // 遍历 tagMap 的每一对 key-value
        for (Map.Entry<String, List<Note>> entry : tagMap.entrySet()) {
            String tagName = entry.getKey();
            int count = entry.getValue().size(); // 列表的大小就是笔记的数量
            stats.put(tagName, count);
        }
        return stats;
    }
}