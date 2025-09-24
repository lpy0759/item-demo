package com.lpy;


import com.lpy.entity.User;
import com.lpy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserPerformanceTest {

    @Autowired
    private UserService userService;

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testBatchUserCreation_ShouldCompleteWithinTimeLimit() {
        // Given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            users.add(new User("用户" + i, "user" + i + "@example.com", 20 + (i % 50)));
        }

        // When
        long startTime = System.currentTimeMillis();
        for (User user : users) {
            userService.saveUser(user);
        }
        long endTime = System.currentTimeMillis();

        // Then
        long executionTime = endTime - startTime;
        System.out.println("批量创建100个用户耗时: " + executionTime + "ms");

        // 验证所有用户都已保存
        List<User> allUsers = userService.findAll();
        assertTrue(allUsers.size() >= 100);
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testSearchPerformance() {
        // Given - 先创建一些测试数据
        for (int i = 0; i < 50; i++) {
            userService.saveUser(new User("测试用户" + i, "test" + i + "@example.com", 25));
        }

        // When & Then
        long startTime = System.currentTimeMillis();
        List<User> results = userService.searchByName("测试");
        long endTime = System.currentTimeMillis();

        System.out.println("搜索耗时: " + (endTime - startTime) + "ms");
        assertFalse(results.isEmpty());
    }
}
