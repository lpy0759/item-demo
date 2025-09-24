package com.lpy;


import com.lpy.entity.User;
import com.lpy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
// 使用自定义注解的测试类
@IntegrationTest
class CustomAnnotationTest {

    @Autowired
    private UserService userService;

    @Test
    void testCustomAnnotation() {
        // Given
        User user = new User("自定义注解测试", "custom@example.com", 28);

        // When
        User savedUser = userService.saveUser(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("自定义注解测试", savedUser.getName());
    }
}
