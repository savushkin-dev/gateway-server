package com.host.SpringBootBerezaServer.repositories;

import com.host.SpringBootBerezaServer.model.NasHostTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NasHostTestRepository extends JpaRepository<NasHostTest, Integer> {
}
