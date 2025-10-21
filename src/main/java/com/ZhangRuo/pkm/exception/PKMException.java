package com.ZhangRuo.pkm.exception;

/**
* 项目自定义异常的基类PKMException
* 所有的业务特定异常都应继承自此类
*/

public class PKMException extends Exception{

    public PKMException(String message){
        super(message);
    }

    public PKMException(String message, Throwable cause){
        super(message, cause);
    }
}



