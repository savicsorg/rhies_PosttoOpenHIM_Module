/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.posttoopenhim.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link RegistrationCoreService}.
 */
@Transactional
public class PostToOpenhimServiceImpl extends BaseOpenmrsService implements ApplicationContextAware {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext applicationContext;
	
	private PatientService patientService;
	
	private PersonService personService;
	
	private LocationService locationService;
	
	private AdministrationService adminService;
	
	private PostToOpenhimProperties postToOpenhimProperties;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}
	
	public void setPostToOpenhimProperties(PostToOpenhimProperties postToOpenhimProperties) {
		this.postToOpenhimProperties = postToOpenhimProperties;
	}
	
}
