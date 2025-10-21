package com.ZhangRuo.pkm.exception;

/**
 * 专门用于文件读、写、复制、删除等IO操作失败的场景
 * 文件操作失败时抛出的专用异常
 * 包含了操作类型、文件路径和原始原因等详细信息
 */


public class FileOperationException extends PKMException {

    /**
     * @param operation 正在执行的操作，例如“保存笔记数据”，“加载笔记数据”
     * @param filePath  操作失败的文件路径
     * @param cause     原始的底层异常（如IOException）
     */

    public FileOperationException(String operation,String filePath, Throwable cause) {
        //调用父类构造方法，构建详细的错误信息，并传递异常链
        super(
                String.format("文件操作失败：%s[文件: %s]",operation,filePath),
                cause
        );



    }


}

