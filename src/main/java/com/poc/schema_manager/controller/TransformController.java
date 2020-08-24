package com.poc.schema_manager.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.poc.schema_manager.service.TransformService;

@RestController
public class TransformController {
	 @Autowired
	 private TransformService transformService;
	
     public Map<String, Object> transfrom(@RequestBody Map<String, Object> request){
    	 return transformService.transfrom(request); 
     }
}
