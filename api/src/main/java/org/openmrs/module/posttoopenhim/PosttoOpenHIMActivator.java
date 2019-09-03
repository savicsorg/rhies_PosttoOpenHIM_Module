/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.posttoopenhim;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.posttoopenhim.api.impl.PatientActionListener;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class PosttoOpenHIMActivator implements ModuleActivator, DaemonTokenAware {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private DaemonToken daemonToken;
	
	private AdministrationService adminService;
	
	private ProviderService providerService;
	
	private PersonService personService;
	
	private ConceptService conceptService;
	
	@Override
	public void setDaemonToken(DaemonToken daemonToken) {
		log.info("[info] Set daemon token to Registration Core Module");
		List<PatientActionListener> registeredComponents = Context.getRegisteredComponents(PatientActionListener.class);
		for (PatientActionListener patientActionListener : registeredComponents) {
			patientActionListener.setDaemonToken(daemonToken);
			
		}
		
	}
	
	public void started() {
		adminService = Context.getAdministrationService();
		GlobalProperty gp;
		
		String openhimUrl = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENHIM_URL);
		if (openhimUrl == null || openhimUrl.isEmpty()) {
			log.error("[error]------ Openhim URL is not defined on administration settings.");
			gp = new GlobalProperty(PostToOpenhimConstants.GP_OPENHIM_URL, "localhost:4002/report/");
			gp.setDescription("OpenHIM URL");
			adminService.saveGlobalProperty(gp);
		}
		
		String openhimUser = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENHIM_USER);
		if (openhimUser == null || openhimUser.isEmpty()) {
			log.error("[error]------ Openhim user name is not defined on administration settings.");
			gp = new GlobalProperty(PostToOpenhimConstants.GP_OPENHIM_USER, "openmrs");
			gp.setDescription("OpenHIM openmrs client User");
			adminService.saveGlobalProperty(gp);
		}
		
		String openhimPwd = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENHIM_PWD);
		if (openhimPwd == null || openhimPwd.isEmpty()) {
			log.error("[error]------ Openhim user password is not defined on administration settings.");
			gp = new GlobalProperty(PostToOpenhimConstants.GP_OPENHIM_PWD, "savior");
			gp.setDescription("OpenHIM openmrs client User password");
			adminService.saveGlobalProperty(gp);
		}
		
		String openmrsPatientUrlBase = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENMRS_PATIENT_BASE);
		if (openmrsPatientUrlBase == null || openmrsPatientUrlBase.isEmpty()) {
			log.error("[error]------ Openmrs patient url base is not defined on administration settings.");
			gp = new GlobalProperty(PostToOpenhimConstants.GP_OPENMRS_PATIENT_BASE,
			        "http://localhost:8080/openmrs/ws/rest/v1/patient/");
			gp.setDescription("OpenHIM Openmrs patient url base");
			adminService.saveGlobalProperty(gp);
		}
		
		String openmrsPatientUrlBaseUser = adminService
		        .getGlobalProperty(PostToOpenhimConstants.GP_OPENMRS_PATIENT_BASE_USER);
		if (openmrsPatientUrlBaseUser == null || openmrsPatientUrlBaseUser.isEmpty()) {
			log.error("[error]------ Openmrs patient url base user name is not defined on administration settings.");
			gp = new GlobalProperty(PostToOpenhimConstants.GP_OPENMRS_PATIENT_BASE_USER, "savior");
			gp.setDescription("OpenHIM Openmrs patient url base user name");
			adminService.saveGlobalProperty(gp);
		}
		
		String openmrsPatientUrlBasePwd = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENMRS_PATIENT_BASE_PWD);
		if (openmrsPatientUrlBasePwd == null || openmrsPatientUrlBasePwd.isEmpty()) {
			log.error("[error]------ Openmrs patient url base user pwd is not defined on administration settings.");
			gp = new GlobalProperty(PostToOpenhimConstants.GP_OPENMRS_PATIENT_BASE_PWD, "savior1nAction");
			gp.setDescription("OpenHIM Openmrs patient url base user password");
			adminService.saveGlobalProperty(gp);
		}
		
		log.info("[info]------ Started PostToOpenHIM Module");
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("[info]------ Stopping PostToOpenHIM Module");
	}
	
	@Override
	public void willRefreshContext() {
		log.info("[info]------ refreshing PostToOpenHIM Module");
	}
	
	@Override
	public void contextRefreshed() {
		log.info("[info]------ Refreshed PostToOpenHIM Module");
	}
	
	@Override
	public void willStart() {
		log.info("[info]------ Starting PostToOpenHIM Module");
	}
	
	@Override
	public void stopped() {
		log.info("[info]------ Stopped PostToOpenHIM Module");
	}
}
