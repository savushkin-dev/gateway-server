package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.exceptions.UserNotFoundException;
import com.example.SpringBootBerezaServer.model.User;
import com.example.SpringBootBerezaServer.repositories.UsersRepository;
import com.example.SpringBootBerezaServer.security.UserOrgDetails;
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
