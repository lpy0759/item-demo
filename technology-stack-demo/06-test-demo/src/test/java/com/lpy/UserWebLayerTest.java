package com.lpy;


import com.lpy.controller.UserController;
import com.lpy.entity.User;
import com.lpy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//测试切片
@WebMvcTest(UserController.class)
class UserWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetAllUsers_ShouldReturnJsonArray() throws Exception {
        // Given
        User user1 = new User("张三", "zhangsan@example.com", 25);
        user1.setId(1L);
        User user2 = new User("李四", "lisi@example.com", 30);
        user2.setId(2L);

        when(userService.findAll()).thenReturn(Arrays.asList(user1, user2));

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}

