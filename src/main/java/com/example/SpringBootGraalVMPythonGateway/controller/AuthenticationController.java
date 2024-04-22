package com.example.SpringBootGraalVMPythonGateway.controller;

import com.example.SpringBootGraalVMPythonGateway.dto.LoginRequestDTO;
import com.example.SpringBootGraalVMPythonGateway.dto.LoginResponseDTO;
import com.example.SpringBootGraalVMPythonGateway.dto.UserDTO;
import com.example.SpringBootGraalVMPythonGateway.exceptions.UserNotCreatedException;
import com.example.SpringBootGraalVMPythonGateway.model.User;
import com.example.SpringBootGraalVMPythonGateway.security.JWTUtil;
import com.example.SpringBootGraalVMPythonGateway.service.PersonDetailsService;
import com.example.SpringBootGraalVMPythonGateway.service.RegistrationService;
import com.example.SpringBootGraalVMPythonGateway.util.UserOrgValidator;
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
