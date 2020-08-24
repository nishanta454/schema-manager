package com.poc.schema_manager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poc.schema_manager.entity.XField;

public interface FieldRepo extends JpaRepository<XField, UUID> {
	XField findOneByNameAndServiceId(String name, UUID id);
	List<XField> findByParentId(UUID id);
}
