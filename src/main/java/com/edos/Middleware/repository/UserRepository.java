package com.edos.Middleware.repository;

import com.edos.Middleware.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
