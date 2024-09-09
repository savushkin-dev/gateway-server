package com.host.SpringBootBerezaServer.repositories;


import com.host.SpringBootBerezaServer.model.connections.NsGrNmsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GRNMSGRepository extends JpaRepository<NsGrNmsg, Integer> {
}
