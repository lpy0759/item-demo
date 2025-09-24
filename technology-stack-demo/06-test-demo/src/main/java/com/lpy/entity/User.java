package com.lpy.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private Integer age;

    // 无参构造函数
    public User() {}

    // 有参构造函数
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

}
