package com.lpy;

import com.lpy.dao.UserRepository;
import com.lpy.entity.User;
import com.lpy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
// 异常测试类
@ExtendWith(MockitoExtension.class)
class UserExceptionTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUser_WhenDatabaseError_ShouldThrowException() {
        // Given
        User user = new User("测试用户", "test@example.com", 25);
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("数据库约束违反"));

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    void testSaveUser_WithNullUser_ShouldThrowNullPointerException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            userService.saveUser(null);
        });
    }
}
