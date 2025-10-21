package com.ZhangRuo.pkm.exception;

/**
 *对象序列化或对象反序列化失败时抛出的专用异常
 */

public class SerializationException extends PKMException {

    /**
     * @param operation 正在执行的序列化/反序列化操作
     * @param cause     原始的底层异常（例如IOException,ClassNotFoundException）
     */

    public SerializationException(String operation, Throwable cause) {
        //调用父类构造方法，构建错误信息并传递异常链
        super(String.format("序列化/反序列化操作失败：%s",operation), cause);

    }




}
