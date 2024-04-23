package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.model.Nas2Host;
import com.example.SpringBootBerezaServer.repositories.Nas2HostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class Nas2HostService {

    @Autowired
    private final Nas2HostRepository nas2HostRepository;

    public Nas2HostService(Nas2HostRepository nas2HostRepository) {
        this.nas2HostRepository = nas2HostRepository;
    }

    public List<Nas2Host> findAll(){
        return nas2HostRepository.findAll();
    }
}
