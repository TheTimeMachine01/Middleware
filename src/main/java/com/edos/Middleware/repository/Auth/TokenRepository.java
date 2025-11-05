package com.edos.Middleware.repository.Auth;


import com.edos.Middleware.entity.Auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserId(Long userId);
}
