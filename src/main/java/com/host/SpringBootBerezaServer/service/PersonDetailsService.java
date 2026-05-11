package com.host.SpringBootBerezaServer.service;

import com.host.SpringBootBerezaServer.exceptions.UserNotFoundException;
import com.host.SpringBootBerezaServer.model.User;
import com.host.SpringBootBerezaServer.repositories.UsersRepository;
import com.host.SpringBootBerezaServer.security.UserOrgDetails;
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
            throw new UserNotFoundException();
        }

        return new UserOrgDetails(person.get());
    }
}
