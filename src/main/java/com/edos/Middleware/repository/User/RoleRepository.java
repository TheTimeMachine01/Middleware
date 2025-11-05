package com.edos.Middleware.repository.User;

import com.edos.Middleware.entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Override
    Optional<Role> findById(Long Long);

    Optional<Role> findByName(String role);
}
