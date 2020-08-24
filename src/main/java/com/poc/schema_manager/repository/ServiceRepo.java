package com.poc.schema_manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poc.schema_manager.entity.XService;

public interface ServiceRepo extends JpaRepository<XService, UUID>{
    XService findOneByName(String name);
}
