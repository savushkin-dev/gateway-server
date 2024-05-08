package com.host.SpringBootBerezaServer.repositories;

import com.host.SpringBootBerezaServer.model.Host2Nas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Host2NasRepository extends JpaRepository<Host2Nas, Integer> {



}
