package com.poc.schema_manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poc.schema_manager.entity.XMapping;

public interface MappingRepo extends JpaRepository<XMapping, UUID>{

}
