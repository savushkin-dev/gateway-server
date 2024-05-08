package com.host.SpringBootBerezaServer.repositories;

import com.host.SpringBootBerezaServer.model.Nas2Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Nas2HostRepository extends JpaRepository<Nas2Host, Integer> {



}
