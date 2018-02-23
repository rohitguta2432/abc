package com.socioseer.acl.config;

import org.springframework.beans.factory.annotation.Autowired;

import com.socioseer.acl.service.ResourceRoleMappingCache;

/**
 * <b>CacheInitializer Configuration</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class CacheInitializer{

	@Autowired
	private ResourceRoleMappingCache resourceRoleMappingCache;
/**
 * 
 * @throws Exception
 */
	public void afterPropertiesSet() throws Exception {
		resourceRoleMappingCache.reloadCache();
	}
}
