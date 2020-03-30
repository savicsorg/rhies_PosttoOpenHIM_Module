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
package org.openmrs.module.posttoopenhim;

public final class PostToOpenhimConstants {
	
	/**
	 * Specifies the identifier source to use when generating patient identifiers
	 */
	public static final String PATIENT_REGISTRATION_EVENT_TOPIC_NAME = "org.openmrs.module.posttoopenhim.PatientRegistrationEvent";
	
	public static final String PATIENT_EDIT_EVENT_TOPIC_NAME = "org.openmrs.module.posttoopenhim.PatientEditEvent";
	
	public static final String GP_OPENHIM_CONTEXT_URL = "posttoopenhim.openhim.context.url";
	
	public static final String GP_OPENHIM_PATIENT_URL = "posttoopenhim.openhim.patient.url";
	
	public static final String GP_OPENHIM_CLIENT_USER = "posttoopenhim.openhim.client.user";
	
	public static final String GP_OPENHIM_CLIENT_PWD = "posttoopenhim.openhim.client.pwd";
	
	public static String GP_OPENMRS_HOST = "posttoopenhim.openmrs.host";
	
	public static String GP_OPENMRS_USER_NAME = "posttoopenhim.openmrs.user";
	
	public static String GP_OPENMRS_USER_PWD = "posttoopenhim.openmrs.pwd";
	
	public static String GP_OPENMRS_FORMS = "posttoopenhim.openmrs.forms";
	
}
