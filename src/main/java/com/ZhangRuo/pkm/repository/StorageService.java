package com.ZhangRuo.pkm.repository;

import com.ZhangRuo.pkm.entity.Note;
import java.util.List;

/*
*定义了数据存储服务的统一接口（契约）
* 任何具体的存储实现（如JSON，数据库）都必须实现此接口
* */

public interface StorageService {
    /*
    * 保存笔记列表
    * @param notes 要保存的笔记列表
    * */
    void save(List<Note> notes);

    /*
    * 加载所有笔记
    * @return 一个包含所有笔记的列表
    * */
    List<Note> load();

}
