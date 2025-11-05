package com.edos.Middleware.service.User;

import com.edos.Middleware.dto.auth.RegistrationRequest;
import com.edos.Middleware.entity.User.Employee;
import com.edos.Middleware.entity.User.Role;
import com.edos.Middleware.exceptions.EmailAlreadyExistException;
import com.edos.Middleware.repository.User.RoleRepository;
import com.edos.Middleware.repository.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Employee registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new EmailAlreadyExistException("Employee with this email already exists.");
        }

        Employee employee = new Employee();
        employee.setName(registrationRequest.getName());
        employee.setEmail(registrationRequest.getEmail());
        employee.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        Role defaultRole = roleRepository.findByName(Role.RoleType.USER.getName())
                .orElseThrow(() -> new RuntimeException("Default 'USER' role not found. System configuration error."));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        employee.setRoles(roles);


        return userRepository.save(employee);
    }
}