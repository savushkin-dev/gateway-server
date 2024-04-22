package com.example.SpringBootBerezaServer.util;

import com.example.SpringBootBerezaServer.model.User;
import com.example.SpringBootBerezaServer.security.UserOrgDetails;
import com.example.SpringBootBerezaServer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class UserOrgValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserOrgValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (userService.findByUsername(user.getUsername()).isPresent() && !getUserOrgDetails().getUsername().equals(user.getUsername()))
            errors.rejectValue("username", "", "Пользователь с таким именем уже существует.");
    }

    private User getUserOrgDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserOrgDetails userOrgDetails = (UserOrgDetails) authentication.getPrincipal();
        return userOrgDetails.getPerson();
    }

}
