package com.ZhangRuo.pkm.repository;

import com.ZhangRuo.pkm.entity.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("JsonStorageService 持久化功能测试")
class JsonStorageServiceTest {

    private static final String TEST_JSON_FILE="test_notes.json";
    private StorageService storageService;//我们面向接口进行测试

    @BeforeEach
    void setUp() {
        //在测试前，实例化具体的实现类
        storageService = new JsonStorageService(TEST_JSON_FILE);
    }

    @AfterEach
    void tearDown() {
        //每个测试结束后，都删除生成的测试文件，确保环境干净
        new File(TEST_JSON_FILE).delete();
    }

    @Test
    @DisplayName("✅ 应能成功保存和加载笔记列表")
    void testSaveAndLoad() {
        //1.准备数据
        List<Note> notesToSave = new ArrayList<>();
        Note note1 =new Note("Title JSON","Content JSON");
        note1.addTag("json");
        notesToSave.add(note1);

        //2.执行保存
        assertDoesNotThrow(()->storageService.save(notesToSave));

        //3.执行加载
        List<Note> loadedNotes = storageService.load();

        //4.验证
        assertNotNull(loadedNotes);
        assertEquals(1, loadedNotes.size());
        assertEquals("Title JSON", loadedNotes.get(0).getTitle());
        assertTrue(loadedNotes.get(0).hasTag("json"));

        /*
        * 当 Jackson 从 notes.json 文件里读到一个代表笔记的 JSON 对象时，它需要先在内存里创建一个空的 Note 对象，
        * 然后再把 JSON 里的 title, content 等值，通过 setter 方法一个个地填充进去。
        *但是，要创建那个“空的 Note 对象”，Jackson 默认需要调用 Note 类的无参数构造方法（也叫默认构造方法），也就是 public Note() {}。
        *  */

    }

    @Test
    @DisplayName("✅ 从不存在的文件加载时应返回空列表")
    void testLoadWhenDoesNotExist() {
        //确保文件不存在
        assertFalse(new File(TEST_JSON_FILE).exists());

        List<Note> loadedNotes = storageService.load();

        assertNotNull(loadedNotes);
        assertTrue(loadedNotes.isEmpty(), "从不存在的文件加载，应返回一个空列表");
    }

    @Test
    @DisplayName("✅ 保存空列表应能正确生成空的JSON数组文件")
    void testSaveEmptyList() throws Exception{
        List<Note> emptyList = new ArrayList<>();

        storageService.save(emptyList);

        File file = new File(TEST_JSON_FILE);
        assertTrue(file.exists());

        //读取文件内容，验证它是否是一个空的JSON数组”[]“(或带有换行)
        String content = Files.readString(Path.of(TEST_JSON_FILE)).trim();
        assertTrue(content.equals("[]") || content.equals("[ ]"));
    }

}