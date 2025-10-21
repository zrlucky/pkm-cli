package com.ZhangRuo.pkm.exception;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@DisplayName("FileOperationException")
class FileOperationExceptionTest {

    @Test
    @DisplayName("✅ 应能携带详细信息和异常链")
    void testExceptionCreation() {
        IOException cause = new IOException("Disk is full");
        FileOperationException ex=new FileOperationException("写入文件","D:/exercise/data.txt",cause);

        assertTrue(ex.getMessage().contains("写入文件"));
        assertTrue(ex.getMessage().contains("D:/exercise/data.txt"));
        assertEquals(cause,ex.getCause());


    }
    @Test
    @DisplayName("✅ 应能被正确抛出和捕获")
    void testThrowAndCatch() {
        assertThrows(FileOperationException.class,()->{
            throw new FileOperationException("测试操作","D:/exercise/data.txt",new IOException("<UNK>"));
        });
    }


}
















