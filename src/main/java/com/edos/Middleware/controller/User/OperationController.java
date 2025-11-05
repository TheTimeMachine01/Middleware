package com.edos.Middleware.controller.User;

import com.edos.Middleware.config.SecurityUtils;
import com.edos.Middleware.dto.User.CurrentUser;
import com.edos.Middleware.dto.User.ExploreUser;
import com.edos.Middleware.entity.User.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class OperationController {

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping("/me")
    public ResponseEntity<CurrentUser> currentAuthenticatedUser() {

        Employee authenticatedUser = securityUtils.getAuthenticatedUser();

        CurrentUser currentUser = new CurrentUser();
        currentUser.setId(authenticatedUser.getId());
        currentUser.setName(authenticatedUser.getName());

        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @GetMapping("/detailedMe")
    public ResponseEntity<ExploreUser> detailedCurrentAuthenticateUser() {

        Employee authenticatedUser = securityUtils.getAuthenticatedUser();

        ExploreUser exploreUser = new ExploreUser();
        exploreUser.setId(authenticatedUser.getId());

        return new ResponseEntity<>(exploreUser, HttpStatus.OK);
    };
}
