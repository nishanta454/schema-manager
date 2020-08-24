package com.poc.schema_manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poc.schema_manager.entity.XCriteria;

public interface CriteriaRepo extends JpaRepository<XCriteria, UUID> {
	XCriteria findOneByCampaignIdAndTemplateId(String campaignId, String templateId);
}
