package com.lpy.dao;


import com.lpy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByNameContaining(String name);

    @Query("SELECT u FROM User u WHERE u.age > ?1")
    List<User> findUsersOlderThan(Integer age);
}
