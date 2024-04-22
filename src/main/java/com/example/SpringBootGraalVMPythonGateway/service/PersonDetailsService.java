package com.example.SpringBootGraalVMPythonGateway.service;

import com.example.SpringBootGraalVMPythonGateway.exceptions.UserNotFoundException;
import com.example.SpringBootGraalVMPythonGateway.model.User;
import com.example.SpringBootGraalVMPythonGateway.repositories.UsersRepository;
import com.example.SpringBootGraalVMPythonGateway.security.UserOrgDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public PersonDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> person = usersRepository.findByUsername(username);

        if (person.isEmpty()){
//            throw new UsernameNotFoundException("Username not found!");
            throw new UserNotFoundException();
        }

        return new UserOrgDetails(person.get());
    }
}
