package com.ZhangRuo.pkm.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ZhangRuo.pkm.entity.Note;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
* 使用JSON文件实现StorageService接口
*负责将笔记对象序列化为JSON格式并存入文件，以及从文件中反序列化
*
* StorageService接口的具体实现者，它负责读写 notes.json 文件
* */

public class JsonStorageService implements StorageService {

    private final String filePath;
    private final ObjectMapper objectMapper; //Jackon核心对象

    /*
    * 默认构造方法，使用"notes.json"作为文件名
    * */
    public JsonStorageService(){
        this("notes.json");
    }

    /*
    * 可指定文件路径的构造方法
    * @param filepath JSON文件的路径
    * */
    public JsonStorageService(String filePath){
        this.filePath = filePath;
        //初始化并配置ObjectMapper
        this.objectMapper = new ObjectMapper();
        //注册JavaTimeModel以支持LocalTimeModel
        this.objectMapper.registerModule(new JavaTimeModule());
        //配置特性：将日期时间格式化为易读的字符串（例：“2023-10-27T10:00:00”）
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //配置特性：美化输出的JSON格式(带缩进)，便于阅读
        this.objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void save(List<Note> notes){
        try {
            //使用ObjectMapper将notes列表写入到指定的文件中
            objectMapper.writeValue(new File(filePath),notes);
        }catch (IOException e){
            //在实际应用中，这里应该抛出我们自定义的FileOperationExpection
            e.printStackTrace();
        }
    }

    @Override
    public List<Note> load(){
        File file = new File(filePath);
        if(!file.exists() || file.length() == 0){
            return new ArrayList<>(); //如果文件不存在或为空，返回空列表
        }
        try {
            //使用objectMpapper从文件中读取JSON数据，并将其转换为NOTE 对象的数组，再转为列表
            Note[] notesArray =objectMapper.readValue(file,Note[].class);
            return new ArrayList<>(Arrays.asList(notesArray));
        }catch (IOException e){
            e.printStackTrace();
            return new ArrayList<>();//加载失败也返回空列表，保证程序健壮性
        }
    }

}












