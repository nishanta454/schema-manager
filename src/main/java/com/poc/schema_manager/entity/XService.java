package com.poc.schema_manager.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "service_master")
public class XService {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String name;
}
