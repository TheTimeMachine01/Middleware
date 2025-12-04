package com.edos.Middleware.config;

import com.edos.Middleware.entity.User.Employee;
import com.edos.Middleware.service.User.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Autowired
    private static EmployeeService employeeService;

    /**
     * Retrieves the authenticated Employee object from the SecurityContext.
     * @return The authenticated Employee.
     * @throws RuntimeException if no authenticated user is found or email cannot be extracted.
     */
    public static Employee getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated User Found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Employee) {
            return (Employee) principal;
        } else if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            return employeeService.getEmployeeByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Employee not Found with email: " + email));
        }

        throw new RuntimeException("Unable to Extract user Details from authentication principal");
    }

    /**
     * Checks if the currently authenticated user has the 'ADMIN' role
     * by checking the Employee's Set<Role> collection.
     * @return true if the user is an admin, false otherwise.
     */
    public boolean hasAdminRole() {
        try {
            Employee employee = getAuthenticatedUser();
            return employee.getRoles().stream()
                    .anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getName()));
        } catch (RuntimeException e) {
            return false;
        }
    }
}
