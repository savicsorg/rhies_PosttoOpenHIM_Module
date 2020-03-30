/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.posttoopenhim.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.posttoopenhim.PostToOpenhimConstants;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.base64.Base64;
import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.api.UserService;

/**
 * @author gilbertagbodamakou
 */
public class Tunnel {
	
	private Patient patient;
	
	private Encounter encounter;
	
	private AdministrationService adminService;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public Tunnel(Patient patient) {
		this.patient = patient;
	}
	
	public Tunnel(Encounter encounter) {
		this.encounter = encounter;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public void send() {
		
		if (this.encounter == null && this.patient == null) {
			log.info("[info]------ Got null as encounter and null as patient, process stopped");
			return;
		}
		
		adminService = Context.getAdministrationService();
		GlobalProperty gp;
		
		log.info("[info]------ Getting properties.");
		
		final String openhimPatientUrl = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENHIM_PATIENT_URL);
		if (openhimPatientUrl == null || openhimPatientUrl.isEmpty()) {
			log.error("[error]------ Openhim patient report URL is not defined on administration settings.");
			return;
		}
		
		final String openhimContextUrl = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENHIM_CONTEXT_URL);
		if (openhimContextUrl == null || openhimContextUrl.isEmpty()) {
			log.error("[error]------ Openhim context report URL is not defined on administration settings.");
			return;
		}
		
		final String openhimClientID = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENHIM_CLIENT_USER);
		if (openhimClientID == null || openhimClientID.isEmpty()) {
			log.error("[error]------ Openhim client ID is not defined on administration settings.");
			return;
		}
		final String openhimPwd = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENHIM_CLIENT_PWD);
		if (openhimPwd == null || openhimPwd.isEmpty()) {
			log.error("[error]------ Openhim client Basic Auth Password is not defined on administration settings.");
			return;
		}
		
		final String openmrsHost = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENMRS_HOST);
		if (openmrsHost == null || openmrsHost.isEmpty()) {
			log.error("[error]------ Openmrs host is not defined on administration settings.");
			return;
		}
		
		final String openmrsUser = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENMRS_USER_NAME);
		if (openmrsUser == null || openmrsUser.isEmpty()) {
			log.error("[error]------ Openmrs user name is not defined on administration settings.");
			return;
		}
		
		final String openmrsUserPwd = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENMRS_USER_PWD);
		if (openmrsUserPwd == null || openmrsUserPwd.isEmpty()) {
			log.error("[error]------ Openmrs user password is not defined on administration settings.");
			return;
		}
		
		final String openmrsForms = adminService.getGlobalProperty(PostToOpenhimConstants.GP_OPENMRS_FORMS);
		if (openmrsForms == null || openmrsForms.isEmpty()) {
			log.error("[error]------ Openmrs post to openhim forms are not defined on administration settings.");
			return;
		}
		
		if (this.encounter != null
		        && openmrsForms.trim().toUpperCase().contains(this.encounter.getForm().getName().trim().toUpperCase()) == false) {
			log.error("[error]------ this form is not concerned by the post to openhim module ...");
			return;
		}
		
		try {
			
			if (this.encounter == null) {
				log.info("[info]------ Preparing to send Encounter");
				
				boolean useHttps = openmrsHost.substring(0, 4).toUpperCase().equals("https".toUpperCase());
				HttpURLConnection HttpCon = null;
				HttpsURLConnection HttpsCon = null;
				int responseCode = 0;
				String UrlBaseUserpass = openmrsUser + ":" + openmrsUserPwd;
				String OpenhimUserpass = openhimClientID + ":" + openhimPwd;
				String UrlBaseBasicAuth = "Basic " + new String(Base64.encode(UrlBaseUserpass.getBytes()));
				String OpenhimBasicAuth = "Basic " + new String(Base64.encode(OpenhimUserpass.getBytes()));
				
				log.info("[info]------ Preparing to get encounter json from " + openmrsHost
				        + "/openmrs/ws/rest/v1/encounter/" + this.encounter.getUuid() + "?v=full");
				//Get json format with openmrs ws rest service
				String url = openmrsHost + "/openmrs/ws/rest/v1/encounter/" + this.encounter.getUuid() + "?v=full";
				URL obj = new URL(url);
				
				if (useHttps == true) {
					HttpsCon = (HttpsURLConnection) obj.openConnection();
					HttpsCon.setRequestMethod("GET");
					HttpsCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				} else {
					
					HttpCon = (HttpURLConnection) obj.openConnection();
					HttpCon.setRequestMethod("GET");
					HttpCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				}
				
				String inputLine;
				BufferedReader in;
				if (useHttps == true) {
					responseCode = HttpsCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpsCon.getInputStream()));
				} else {
					responseCode = HttpCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpCon.getInputStream()));
				}
				
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				log.error("[info]------got encounter json response " + response.toString());
				String encounterJsonString = response.toString();
				
				log.info("[info]------ Preparing to get patient json from " + openmrsHost + "/openmrs/ws/rest/v1/patient/"
				        + this.encounter.getPatient().getUuid() + "?v=full");
				//Get json format with openmrs ws rest service 
				url = openmrsHost + "/openmrs/ws/rest/v1/patient/" + this.encounter.getPatient().getUuid() + "?v=full";
				obj = new URL(url);
				
				if (useHttps == true) {
					HttpsCon = (HttpsURLConnection) obj.openConnection();
					HttpsCon.setRequestMethod("GET");
					HttpsCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				} else {
					
					HttpCon = (HttpURLConnection) obj.openConnection();
					HttpCon.setRequestMethod("GET");
					HttpCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				}
				
				if (useHttps == true) {
					responseCode = HttpsCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpsCon.getInputStream()));
				} else {
					responseCode = HttpCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpCon.getInputStream()));
				}
				
				response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				String patientJsonString = response.toString();
				
				log.info("[info]------ Preparing to get location json from " + openmrsHost + "/openmrs/ws/rest/v1/location/"
				        + this.encounter.getLocation().getUuid());
				//Get json format with openmrs ws rest service
				url = openmrsHost + "/openmrs/ws/rest/v1/location/" + this.encounter.getLocation().getUuid();
				obj = new URL(url);
				
				if (useHttps == true) {
					HttpsCon = (HttpsURLConnection) obj.openConnection();
					HttpsCon.setRequestMethod("GET");
					HttpsCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				} else {
					
					HttpCon = (HttpURLConnection) obj.openConnection();
					HttpCon.setRequestMethod("GET");
					HttpCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				}
				
				if (useHttps == true) {
					responseCode = HttpsCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpsCon.getInputStream()));
				} else {
					responseCode = HttpCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpCon.getInputStream()));
				}
				
				response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				String locationJsonString = response.toString();
				
				String jsonTosend = "{\"patient\" :" + patientJsonString + ",\"encounter\" :" + encounterJsonString
				        + ", \"location\" :" + locationJsonString + "}";
				
				log.error("[info]------ json response " + jsonTosend.toString());
				
				//Post to OpenHim
				byte[] input = jsonTosend.getBytes("utf-8");
				try {
					log.info("[info]------ Preparing to post json to " + openhimContextUrl);
					useHttps = openhimContextUrl.substring(0, 4).toUpperCase().equals("https".toUpperCase());
					
					obj = new URL(openhimContextUrl);
					if (useHttps == true) {
						HttpsCon = (HttpsURLConnection) obj.openConnection();
						HttpsCon.setRequestMethod("POST");
						HttpsCon.setRequestProperty("Authorization", OpenhimBasicAuth);
					} else {
						HttpCon = (HttpURLConnection) obj.openConnection();
						HttpCon.setRequestMethod("POST");
						HttpCon.setRequestProperty("Authorization", OpenhimBasicAuth);
					}
					
					OutputStream os;
					BufferedReader br;
					HttpCon.setDoOutput(true);
					
					if (useHttps == true) {
						HttpsCon.setRequestProperty("Content-Type", "application/json; utf-8");
						os = HttpsCon.getOutputStream();
						os.write(input, 0, input.length);
						br = new BufferedReader(new InputStreamReader(HttpsCon.getInputStream(), "utf-8"));
					} else {
						HttpCon.setRequestProperty("Content-Type", "application/json; utf-8");
						os = HttpCon.getOutputStream();
						os.write(input, 0, input.length);
						br = new BufferedReader(new InputStreamReader(HttpCon.getInputStream(), "utf-8"));
					}
					StringBuilder newResponse = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						newResponse.append(responseLine.trim());
					}
					
					log.info("[info]------end of process ");
				}
				catch (Exception ex) {
					log.error("[error]------process aborted, " + ex.toString());
				}
			}
			
			if (this.patient == null) {
				log.info("[info]------ Preparing to send Patient");
				
				boolean useHttps = openmrsHost.substring(0, 4).toUpperCase().equals("https".toUpperCase());
				HttpURLConnection HttpCon = null;
				HttpsURLConnection HttpsCon = null;
				int responseCode = 0;
				String UrlBaseUserpass = openmrsUser + ":" + openmrsUserPwd;
				String OpenhimUserpass = openhimClientID + ":" + openhimPwd;
				String UrlBaseBasicAuth = "Basic " + new String(Base64.encode(UrlBaseUserpass.getBytes()));
				String OpenhimBasicAuth = "Basic " + new String(Base64.encode(OpenhimUserpass.getBytes()));
				
				//Get json format with openmrs ws rest service 
				String url = openmrsHost + "/openmrs/ws/rest/v1/patient/" + this.encounter.getPatient().getUuid()
				        + "?v=full";
				URL obj = new URL(url);
				String inputLine;
				BufferedReader in;
				StringBuffer response = new StringBuffer();
				
				if (useHttps == true) {
					HttpsCon = (HttpsURLConnection) obj.openConnection();
					HttpsCon.setRequestMethod("GET");
					HttpsCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				} else {
					
					HttpCon = (HttpURLConnection) obj.openConnection();
					HttpCon.setRequestMethod("GET");
					HttpCon.setRequestProperty("Authorization", UrlBaseBasicAuth);
				}
				
				if (useHttps == true) {
					responseCode = HttpsCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpsCon.getInputStream()));
				} else {
					responseCode = HttpCon.getResponseCode();
					in = new BufferedReader(new InputStreamReader(HttpCon.getInputStream()));
				}
				
				response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				String patientJsonString = response.toString();
				String jsonTosend = "patient\" :" + patientJsonString;
				
				log.error("[info]------ json response " + jsonTosend.toString());
				
				//Post to OpenHim
				byte[] input = jsonTosend.getBytes("utf-8");
				try {
					log.info("[info]------ Preparing to post json to " + openhimContextUrl);
					useHttps = openhimContextUrl.substring(0, 4).toUpperCase().equals("https".toUpperCase());
					
					obj = new URL(openhimContextUrl);
					if (useHttps == true) {
						HttpsCon = (HttpsURLConnection) obj.openConnection();
						HttpsCon.setRequestMethod("POST");
						HttpsCon.setRequestProperty("Authorization", OpenhimBasicAuth);
					} else {
						HttpCon = (HttpURLConnection) obj.openConnection();
						HttpCon.setRequestMethod("POST");
						HttpCon.setRequestProperty("Authorization", OpenhimBasicAuth);
					}
					
					OutputStream os;
					BufferedReader br;
					HttpCon.setDoOutput(true);
					
					if (useHttps == true) {
						HttpsCon.setRequestProperty("Content-Type", "application/json; utf-8");
						os = HttpsCon.getOutputStream();
						os.write(input, 0, input.length);
						br = new BufferedReader(new InputStreamReader(HttpsCon.getInputStream(), "utf-8"));
					} else {
						HttpCon.setRequestProperty("Content-Type", "application/json; utf-8");
						os = HttpCon.getOutputStream();
						os.write(input, 0, input.length);
						br = new BufferedReader(new InputStreamReader(HttpCon.getInputStream(), "utf-8"));
					}
					StringBuilder newResponse = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						newResponse.append(responseLine.trim());
					}
					
					log.info("[info]------end of process ");
				}
				catch (Exception ex) {
					log.error("[error]------process aborted, " + ex.toString());
				}
			}
			
		}
		catch (Exception ex) {
			log.error("[error]------process aborted, " + ex.toString());
		}
		
	}
	
}
