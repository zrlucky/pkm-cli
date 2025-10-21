package com.ZhangRuo.pkm.exception;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SerializationException测试")
class SerializationExceptionTest {
    @Test
    @DisplayName("✅ 应能携带操作信息和异常链")
    void tsetExceptionCreation() {
        ClassNotFoundException cause = new ClassNotFoundException("com.ZhangRuo.pkm.entity.01dNote");
        SerializationException ex = new SerializationException("反序列化笔记", cause);

        assertTrue(ex.getMessage().contains("反序列化笔记"));
        assertEquals(cause, ex.getCause());
    }
}