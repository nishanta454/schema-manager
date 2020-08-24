package com.poc.schema_manager.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.schema_manager.entity.XCriteria;
import com.poc.schema_manager.entity.XField;
import com.poc.schema_manager.entity.XFunction;
import com.poc.schema_manager.entity.XMapping;
import com.poc.schema_manager.entity.XService;
import com.poc.schema_manager.enums.FieldType;
import com.poc.schema_manager.repository.CriteriaRepo;
import com.poc.schema_manager.repository.FieldRepo;
import com.poc.schema_manager.repository.FunctionRepo;
import com.poc.schema_manager.repository.MappingRepo;
import com.poc.schema_manager.repository.ServiceRepo;

@Component
public class LoaderUtils {

	@Autowired
	private ServiceRepo serviceRepo;

	@Autowired
	private CriteriaRepo criteriaRepo;

	@Autowired
	private FieldRepo fieldRepo;

	@Autowired
	private FunctionRepo functionRepo;

	@Autowired
	private MappingRepo mappingRepo;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("classpath:data/node.json")
	private Resource nodePayload;

	@Value("classpath:data/template.json")
	private Resource templatePayload;

	@Value("classpath:data/default.json")
	private Resource emailMapping;

	@Value("classpath:data/email.json")
	private Resource customEmailMapping;

	@PostConstruct
	public void init() throws Exception {
		loadServices();
		loadCriterias();
		loadFields();
		loadMappings();
		loadFunctions();
	}

	private void loadServices() {
		String[] services = new String[] { "node", "template" };
		for (String service : services) {
			XService xService = new XService();
			xService.setName(service);
			serviceRepo.save(xService);
		}
	}

	private void loadCriterias() {
		XCriteria criteria = new XCriteria();
		criteria.setCampaignId("2593");
		criteria.setTemplateId("51867C");
		criteriaRepo.save(criteria);
		criteria = new XCriteria();
		criteria.setCampaignId("email");
		criteria.setTemplateId("email");
		criteriaRepo.save(criteria);
	}

	private void loadFunctions() throws Exception {
		XFunction function = new XFunction();
		function.setName("firstChar");
		function.setCode("function(a, b){ if(a && a.length>1) { return a.charAt(0);} return b;}");
		functionRepo.save(function);
	}

	private void loadFields() throws Exception {
		Map<String, Object> payload = objectMapper.readValue(nodePayload.getInputStream(), new TypeReference<Map<String, Object>>() {
		});
		XService service = serviceRepo.findOneByName("node");
		loadFields(service.getId(), null, null, payload, true);
		payload = objectMapper.readValue(templatePayload.getInputStream(), new TypeReference<Map<String, Object>>() {
		});
		service = serviceRepo.findOneByName("template");
		loadFields(service.getId(), null, null, payload, true);
	}

	@SuppressWarnings("unchecked")
	private void loadFields(UUID servieId, UUID parentId, String name, Object input, boolean insertMe) {
		XField field = null;
		if (input instanceof Map) {
			if (insertMe)
				field = createField(servieId, parentId, name, FieldType.OBJECT);
			for (Map.Entry<String, Object> elm : ((Map<String, Object>) input).entrySet()) {
				loadFields(servieId, ObjectUtils.isEmpty(field) ? parentId : field.getId(), elm.getKey(), elm.getValue(), true);
			}
		} else if (input instanceof List) {
			field = createField(servieId, parentId, name, FieldType.ARRAY);
			loadFields(servieId, field.getId(), null, ((List<Object>) input).get(0), false);
		} else if (input instanceof String) {
			field = createField(servieId, parentId, name, FieldType.TEXT);
		}
	}

	private XField createField(UUID servieId, UUID parentId, String name, FieldType type) {
		XField parent = null;
		XField field = null;
		Optional<XService> resultSrv = serviceRepo.findById(servieId);
		if (resultSrv.isPresent()) {
			if (!ObjectUtils.isEmpty(parentId)) {
				Optional<XField> result = fieldRepo.findById(parentId);
				if (result.isPresent()) {
					parent = result.get();
				}
			}
			field = new XField();
			field.setName(StringUtils.isEmpty(name) ? "Root" : name);
			field.setType(type);
			field.setService(resultSrv.get());
			if (!ObjectUtils.isEmpty(parent)) {
				field.setParent(parent);
			}
			fieldRepo.save(field);
		}
		return field;
	}

	private void loadMappings() throws Exception {
		Map<String, Object> mapping = objectMapper.readValue(emailMapping.getInputStream(), new TypeReference<Map<String, Object>>() {
		});
		loadMapping(null, null, mapping, criteriaRepo.findOneByCampaignIdAndTemplateId("email", "email"));
		mapping = objectMapper.readValue(customEmailMapping.getInputStream(), new TypeReference<Map<String, Object>>() {
		});
		loadMapping(null, null, mapping, criteriaRepo.findOneByCampaignIdAndTemplateId("2593", "51867C"));
	}

	@SuppressWarnings("unchecked")
	private void loadMapping(String prePath, String preVal, Map<String, Object> mapping, XCriteria criteria) {
		XService nodeService = serviceRepo.findOneByName("node");
		XService templateService = serviceRepo.findOneByName("template");

		List<Map<String, Object>> fields = (List<Map<String, Object>>) mapping.get("fields");

		for (Map<String, Object> field : fields) {

			String path = (String) field.get("path");
			String value = (String) field.get("value");

			XField targetField = null;
			XField sourceField = null;

			if (!StringUtils.isEmpty(path)) {
				if (!StringUtils.isEmpty(prePath)) {
					path = prePath + "." + path;
				}
				targetField = getFieldByPath(path, templateService);
			}
			if (!StringUtils.isEmpty(value)) {
				if (!StringUtils.isEmpty(preVal)) {
					value = preVal + "." + value;
				}
				sourceField = getFieldByPath(value, nodeService);
			}

			if (!ObjectUtils.isEmpty(targetField) && (StringUtils.isEmpty(value) || !ObjectUtils.isEmpty(sourceField))) {
				XMapping xmapping = new XMapping();
				xmapping.setCriteria(criteria);
				xmapping.setSourceService(nodeService);
				xmapping.setTargetService(templateService);
				xmapping.setSourceField(sourceField);
				xmapping.setTargetField(targetField);
				String defVal = (String) field.get("defVal");
				if (!StringUtils.isEmpty(defVal) && defVal.indexOf(".") != -1) {
					XField defField = getFieldByPath(path, nodeService);
					if (!ObjectUtils.isEmpty(defField)) {
						xmapping.setDefaultField(defField);
					}
				} else {
					xmapping.setDefaultValue(defVal);
				}
				String operation = (String) field.get("operation");
				if (!StringUtils.isEmpty(operation)) {
					XFunction function = functionRepo.findOneByName(operation);
					if (!ObjectUtils.isEmpty(function)) {
						xmapping.setFunction(function);
					}
				}
				mappingRepo.save(xmapping);
				if (!ObjectUtils.isEmpty(field.get("template"))) {
					loadMapping(path, value, (Map<String, Object>) field.get("template"), criteria);
				}
			}
		}
	}

	private XField getFieldByPath(String field, XService service) {
		XField xfield = null;
		String[] parts = field.split("\\.");
		if (parts.length > 0) {
			XField parentfield = fieldRepo.findOneByNameAndServiceId(parts[0], service.getId());
			for (int i = 1; i < parts.length; i++) {
				parentfield = findField(parts[i], parentfield);
			}
			xfield = parentfield;
		}
		return xfield;
	}

	private XField findField(String field, XField parentfield) {
		XField xfield = null;
		if (!ObjectUtils.isEmpty(parentfield)) {
			List<XField> childs = fieldRepo.findByParentId(parentfield.getId());
			if (!ObjectUtils.isEmpty(childs)) {
				for (XField cField : childs) {
					if (cField.getName().equals(field)) {
						xfield = cField;
						break;
					}
				}
			}
		}
		return xfield;
	}
}
