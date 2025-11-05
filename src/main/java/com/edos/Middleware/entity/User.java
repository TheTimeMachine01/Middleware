package com.edos.Middleware.entity;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Table(name = "user")
public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
//    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updated_at;
    //id
    // name
    // email
    // password
    // role
    // created_at
    // updated_at
}
