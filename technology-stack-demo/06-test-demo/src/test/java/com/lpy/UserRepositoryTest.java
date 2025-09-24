package com.lpy;


import com.lpy.dao.UserRepository;
import com.lpy.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User("张三", "zhangsan@example.com", 25);
        testUser2 = new User("李四", "lisi@example.com", 30);

        // 持久化测试数据
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    void testFindByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("zhangsan@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("张三", found.get().getName());
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("notfound@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByNameContaining() {
        // When
        List<User> users = userRepository.findByNameContaining("张");

        // Then
        assertEquals(1, users.size());
        assertEquals("张三", users.get(0).getName());
    }

    @Test
    void testFindUsersOlderThan() {
        // When
        List<User> users = userRepository.findUsersOlderThan(28);

        // Then
        assertEquals(1, users.size());
        assertEquals("李四", users.get(0).getName());
    }

    @Test
    void testSaveAndFindById() {
        // Given
        User newUser = new User("王五", "wangwu@example.com", 35);

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertNotNull(saved.getId());
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("王五", found.get().getName());
    }
}
