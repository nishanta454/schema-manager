package com.poc.schema_manager.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "field_mappings")
public class XMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@OneToOne
	@JoinColumn(name = "sourceFieldId")
	private XField sourceField;

	@OneToOne
	@JoinColumn(name = "targetFieldId")
	private XField targetField;
	
	@OneToOne
	@JoinColumn(name = "defaultFieldId")
	private XField defaultField;
	
	@OneToOne
	@JoinColumn(name = "sourceServiceId")
	private XService sourceService;

	@OneToOne
	@JoinColumn(name = "targetServiceId")
	private XService targetService;

	@OneToOne
	@JoinColumn(name = "functionId")
	private XFunction function;

	@OneToOne
	@JoinColumn(name = "criteriaId")
	private XCriteria criteria;
	
	private String defaultValue;
}
