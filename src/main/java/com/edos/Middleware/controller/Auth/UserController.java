package com.edos.Middleware.controller.Auth;

import com.edos.Middleware.dto.auth.RegistrationRequest;
import com.edos.Middleware.entity.User.Employee;
import com.edos.Middleware.service.User.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Employee> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        Employee newEmployee = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(newEmployee, HttpStatus.CREATED);
    }
}
