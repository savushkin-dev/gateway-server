package com.example.SpringBootBerezaServer.service;

import com.example.SpringBootBerezaServer.model.Host2Nas;
import com.example.SpringBootBerezaServer.model.Nas2Host;
import com.example.SpringBootBerezaServer.repositories.Host2NasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class Host2NasService {

    @Autowired
    private final Host2NasRepository host2NasRepository;

    public Host2NasService(Host2NasRepository host2NasRepository) {
        this.host2NasRepository = host2NasRepository;
    }

    public List<Host2Nas> findAll() {
        return host2NasRepository.findAll();
    }
}
