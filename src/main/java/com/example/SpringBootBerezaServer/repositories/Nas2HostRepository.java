package com.example.SpringBootBerezaServer.repositories;

import com.example.SpringBootBerezaServer.model.Nas2Host;
import com.example.SpringBootBerezaServer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Nas2HostRepository extends JpaRepository<Nas2Host, Integer> {



}
