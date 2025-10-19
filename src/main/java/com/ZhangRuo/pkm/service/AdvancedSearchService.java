package com.ZhangRuo.pkm.service;

import com.ZhangRuo.pkm.entity.Note; // 导入 Note 类

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AdvancedSearchService 提供了基于 Stream API 的高级搜索和统计功能。
 * 这个服务类是无状态的，所有操作都针对传入的数据集合进行。
 */
public class AdvancedSearchService {

    /**
     * 多标签组合搜索 (AND 条件)。
     * 找出笔记列表中，同时包含所有指定标签的笔记。
     *
     * @param allNotes     要进行搜索的全部笔记列表。
     * @param requiredTags 必须包含的标签名列表。
     * @return 符合条件的笔记列表。
     */
    public List<Note> searchByTags(List<Note> allNotes, List<String> requiredTags) {
        // --- 逻辑拆解 ---
        return allNotes.stream() // 1. 将笔记列表转换为一个流 (Stream<Note>)
                .filter(note -> note.getTags().containsAll(requiredTags)) // 2. 过滤：只保留那些标签列表包含了所有 requiredTags 的笔记
                .collect(Collectors.toList()); // 3. 将过滤后的结果收集到一个新的 List<Note> 中
    }

    /**
     * 标签模糊匹配搜索。
     * 找出笔记列表中，其标签包含指定关键字的笔记。
     *
     * @param allNotes 要进行搜索的全部笔记列表。
     * @param keyword  要匹配的标签关键字。
     * @return 符合条件的笔记列表。
     */
    public List<Note> searchByTagKeyword(List<Note> allNotes, String keyword) {
        // --- 逻辑拆解 ---
        return allNotes.stream() // 1. 将笔记列表转换为一个流 (Stream<Note>)
                .filter(note -> note.getTags().stream() // 2. 过滤：对于每个 note...
                        .anyMatch(tagName -> tagName.contains(keyword))) // ...检查其任何一个(anyMatch)标签名是否包含(contains)关键字
                .collect(Collectors.toList()); // 3. 收集结果
    }

    /**
     * 使用 Stream API 生成标签云统计数据。
     * 统计在一系列笔记中，每个标签出现的次数。
     *
     * @param allNotes 要进行统计的全部笔记列表。
     * @return 一个 Map，Key 是标签名，Value 是该标签出现的次数 (Long 类型)。
     */
    public Map<String, Long> generateTagCloud(List<Note> allNotes) {
        // --- 逻辑拆解 (严格按照指导书技术要点) ---
        return allNotes.stream() // 1. 将 List<Note> 转换为一个流 (Stream<Note>)
                .flatMap(note -> note.getTags().stream()) // 2. "压平"操作：将每个 note 的 List<String> 变成一个更大的、包含所有标签的流 (Stream<String>)
                .collect(Collectors.groupingBy( // 3. 收集并分组
                        tagName -> tagName,       // 分组依据：按标签名自身进行分组
                        Collectors.counting()   // 下游收集器：对分到同一组的元素进行计数 (counting)
                ));
    }
}