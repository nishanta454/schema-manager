package com.poc.schema_manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poc.schema_manager.entity.XFunction;

public interface FunctionRepo extends JpaRepository<XFunction, UUID> {
     XFunction findOneByName(String name);
}
