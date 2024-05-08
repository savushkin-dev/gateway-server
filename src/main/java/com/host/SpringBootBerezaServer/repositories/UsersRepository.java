package com.host.SpringBootBerezaServer.repositories;

import com.host.SpringBootBerezaServer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);


}
