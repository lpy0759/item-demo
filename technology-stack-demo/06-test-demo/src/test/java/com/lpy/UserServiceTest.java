package com.lpy;

import com.lpy.dao.UserRepository;
import com.lpy.entity.User;
import com.lpy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("张三", "zhangsan@example.com", 25);
        testUser.setId(1L);
    }

    @Test
    void testSaveUser_Success() {
        // Given
        User inputUser = new User("李四", "lisi@example.com", 30);
        User savedUser = new User("李四", "lisi@example.com", 30);
        savedUser.setId(2L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.saveUser(inputUser);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("李四", result.getName());
        assertEquals("lisi@example.com", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userRepository, times(1)).save(inputUser);
    }

    @Test
    void testSaveUser_WithEmptyName_ShouldThrowException() {
        // Given
        User userWithEmptyName = new User("", "test@example.com", 25);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(userWithEmptyName)
        );

        assertEquals("用户名不能为空", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSaveUser_WithEmptyEmail_ShouldThrowException() {
        // Given
        User userWithEmptyEmail = new User("测试用户", "", 25);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(userWithEmptyEmail)
        );

        assertEquals("邮箱不能为空", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindById_UserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getName(), result.get().getName());
        assertEquals(testUser.getEmail(), result.get().getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserNotExists() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testFindByEmail() {
        // Given
        String email = "zhangsan@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindAll() {
        // Given
        User user2 = new User("王五", "wangwu@example.com", 28);
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAll();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser));
        assertTrue(result.contains(user2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testSearchByName() {
        // Given
        String searchName = "张";
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNameContaining(searchName)).thenReturn(users);

        // When
        List<User> result = userService.searchByName(searchName);

        // Then
        assertEquals(1, result.size());
        assertEquals(testUser.getName(), result.get(0).getName());
        verify(userRepository, times(1)).findByNameContaining(searchName);
    }

    @Test
    void testFindUsersOlderThan() {
        // Given
        Integer age = 20;
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findUsersOlderThan(age)).thenReturn(users);

        // When
        List<User> result = userService.findUsersOlderThan(age);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAge() > age);
        verify(userRepository, times(1)).findUsersOlderThan(age);
    }

    @Test
    void testDeleteUser() {
        // Given
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testIsEmailExists_True() {
        // Given
        String email = "zhangsan@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.isEmailExists(email);

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testIsEmailExists_False() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = userService.isEmailExists(email);

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(email);
    }
}