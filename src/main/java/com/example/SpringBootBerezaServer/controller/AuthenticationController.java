package com.example.SpringBootBerezaServer.controller;

import com.example.SpringBootBerezaServer.dto.LoginRequestDTO;
import com.example.SpringBootBerezaServer.dto.LoginResponseDTO;
import com.example.SpringBootBerezaServer.dto.UserDTO;
import com.example.SpringBootBerezaServer.exceptions.UserNotCreatedException;
import com.example.SpringBootBerezaServer.model.User;
import com.example.SpringBootBerezaServer.security.JWTUtil;
import com.example.SpringBootBerezaServer.service.PersonDetailsService;
import com.example.SpringBootBerezaServer.service.RegistrationService;
import com.example.SpringBootBerezaServer.util.UserOrgValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/authentication")
public class AuthenticationController {

    private final UserOrgValidator userOrgValidator;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(UserOrgValidator userOrgValidator, RegistrationService registrationService, JWTUtil jwtUtil, AuthenticationManager authenticationManager, PersonDetailsService personDetailsService) {
        this.userOrgValidator = userOrgValidator;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponseDTO> performAuthentication(@RequestBody LoginRequestDTO loginRequestDto) {


        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                        loginRequestDto.getPassword());

        authenticationManager.authenticate(authToken);

        String token = jwtUtil.generateToken(loginRequestDto.getUsername());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }


    @PostMapping("/registration")
    public ResponseEntity<LoginResponseDTO> performRegistration(@RequestBody @Valid UserDTO userDTO,
                                                                BindingResult bindingResult) {
        User user = convertToPerson(userDTO);

        userOrgValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error: errors){
                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new UserNotCreatedException(errorMessage.toString());
        }

        registrationService.register(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    private User convertToPerson(UserDTO userDTO) {
        return new ModelMapper().map(userDTO, User.class);
    }


}
