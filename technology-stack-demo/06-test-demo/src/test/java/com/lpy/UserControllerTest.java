package com.lpy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpy.controller.UserController;
import com.lpy.entity.User;
import com.lpy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("张三", "zhangsan@example.com", 25);
        testUser.setId(1L);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Given
        User inputUser = new User("李四", "lisi@example.com", 30);
        User savedUser = new User("李四", "lisi@example.com", 30);
        savedUser.setId(3L);

        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("李四")))
                .andExpect(jsonPath("$.email", is("lisi@example.com")))
                .andExpect(jsonPath("$.age", is(30)));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testCreateUser_BadRequest() throws Exception {
        // Given
        User invalidUser = new User("", "invalid@example.com", 25);
        when(userService.saveUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("用户名不能为空"));

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById_Found() throws Exception {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("张三")))
                .andExpect(jsonPath("$.email", is("zhangsan@example.com")))
                .andExpect(jsonPath("$.age", is(25)));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(999L);
    }

    @Test
    void testGetAllUsers() throws Exception {
        // Given
        User user2 = new User("王五", "wangwu@example.com", 28);
        user2.setId(2L);
        List<User> users = Arrays.asList(testUser, user2);

        when(userService.findAll()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("张三")))
                .andExpect(jsonPath("$[1].name", is("王五")));

        verify(userService, times(1)).findAll();
    }

    @Test
    void testSearchUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.searchByName("张")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                        .param("name", "张"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("张三")));

        verify(userService, times(1)).searchByName("张");
    }

    @Test
    void testDeleteUser() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}
