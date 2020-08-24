package com.poc.schema_manager.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.poc.schema_manager.enums.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity(name = "field_master")
@ToString(exclude = "childrens")
public class XField {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String name;

	@Enumerated(EnumType.STRING)
	private FieldType type;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parentId")
	private XField parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private Set<XField> childrens = new HashSet<>();

	@OneToOne
	@JoinColumn(name = "serviceId")
	private XService service;
}
