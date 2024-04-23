package com.example.SpringBootBerezaServer.repositories;

import com.example.SpringBootBerezaServer.model.Host2Nas;
import com.example.SpringBootBerezaServer.model.Nas2Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Host2NasRepository extends JpaRepository<Host2Nas, Integer> {



}
