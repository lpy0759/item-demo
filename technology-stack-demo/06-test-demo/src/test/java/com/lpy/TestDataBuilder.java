package com.lpy;


import com.lpy.entity.User;

// 测试工具类
public class TestDataBuilder {

//    public static UserBuilder aUser() {
//        return User.Builder();
//    }

    public static class UserBuilder {
        private String name = "默认用户";
        private String email = "default@example.com";
        private Integer age = 25;
        private Long id;

        public UserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder withAge(Integer age) {
            this.age = age;
            return this;
        }

        public UserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public User build() {
            User user = new User(name, email, age);
            if (id != null) {
                user.setId(id);
            }
            return user;
        }
    }
}
