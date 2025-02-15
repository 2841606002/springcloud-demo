package com.example.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 用户名
    private String password; // 密码
    private String email;    // 邮箱
    private String role;     // 角色（USER, EDITOR, ADMIN）
}