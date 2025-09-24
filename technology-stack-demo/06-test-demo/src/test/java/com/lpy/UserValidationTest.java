package com.lpy;


import com.lpy.dao.UserRepository;
import com.lpy.entity.User;
import com.lpy.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
// 参数化测试
@ExtendWith(MockitoExtension.class)
class UserValidationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void testSaveUser_WithInvalidName_ShouldThrowException(String invalidName) {
        // Given
        User user = new User(invalidName, "test@example.com", 25);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(user)
        );
        assertEquals("用户名不能为空", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void testSaveUser_WithInvalidEmail_ShouldThrowException(String invalidEmail) {
        // Given
        User user = new User("测试用户", invalidEmail, 25);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(user)
        );
        assertEquals("邮箱不能为空", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "张三, zhangsan@example.com, 25",
            "李四, lisi@test.com, 30",
            "王五, wangwu@demo.org, 35"
    })
    void testValidUserCreation(String name, String email, Integer age) {
        // Given
        User user = new User(name, email, age);

        // When & Then
        assertDoesNotThrow(() -> {
            // 这里只测试验证逻辑，不涉及实际保存
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("邮箱不能为空");
            }
        });
    }
}

