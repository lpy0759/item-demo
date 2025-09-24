package com.lpy.service;

import com.lpy.dao.UserRepository;
import com.lpy.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> searchByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    public List<User> findUsersOlderThan(Integer age) {
        return userRepository.findUsersOlderThan(age);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
