package com.lpy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpy.dao.UserRepository;
import com.lpy.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 完整集成测试

@SpringBootTest(classes = TestDemoApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCompleteUserFlow() throws Exception {
        // 1. 创建用户
        User newUser = new User("集成测试用户", "integration@example.com", 25);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("集成测试用户")))
                .andExpect(jsonPath("$.email", is("integration@example.com")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);
        Long userId = createdUser.getId();

        // 2. 根据ID查询用户
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("集成测试用户")));

        // 3. 查询所有用户
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 4. 按名字搜索
        mockMvc.perform(get("/api/users/search")
                        .param("name", "集成"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 5. 删除用户
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        // 6. 验证用户已删除
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
