package org.openmrs.module.posttoopenhim.api.impl;

import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.posttoopenhim.PostToOpenhimConstants;
import org.openmrs.module.posttoopenhim.api.ModuleProperties;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class PostToOpenhimProperties extends ModuleProperties implements ApplicationContextAware {
	
	//TODO move getting properties related to Registration Core in this class.
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	private boolean isPropertySet(String globalProperty) {
		return StringUtils.isNotBlank(Context.getAdministrationService().getGlobalProperty(globalProperty));
	}
	
	public Object getBeanFromName(String propertyName) {
		Object bean;
		try {
			String beanId = Context.getAdministrationService().getGlobalProperty(propertyName);
			bean = applicationContext.getBean(beanId);
		}
		catch (APIException e) {
			throw new APIException(e);
		}
		return bean;
	}
	
}
