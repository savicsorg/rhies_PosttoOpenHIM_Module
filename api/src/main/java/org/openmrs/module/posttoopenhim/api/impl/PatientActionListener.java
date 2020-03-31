package org.openmrs.module.posttoopenhim.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.DaemonToken;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * Abstract class for subscribable event listening.
 */
public abstract class PatientActionListener implements SubscribableEventListener {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	protected PostToOpenhimProperties postToOpenhimProperties;
	
	protected PatientService patientService;
	
	private DaemonToken daemonToken;
	
	public void setPostToOpenhimProperties(PostToOpenhimProperties pProperties) {
		this.postToOpenhimProperties = pProperties;
	}
	
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
	
	public void setDaemonToken(DaemonToken daemonToken) {
		this.daemonToken = daemonToken;
	}
	
	/**
	 * Subscribes for Class - Patient and specified Actions event.
	 */
	public void init() {
		Event event = new Event();
		event.setSubscription(this);
	}
	
	/**
	 * Performs action based on messaged received.
	 * 
	 * @param message message that is received by the listener.
	 * @return a list of classes that this can handle
	 */
	@Override
	public void onMessage(final Message message) {
		//if (postToOpenhimProperties.isSyncWithDHIS2Enabled()) {
		Daemon.runInDaemonThread(new Runnable() {
			
			@Override
			public void run() {
				log.info("[info]------ subscribed Encounter creation event...");
				performAction(message);
			}
		}, daemonToken);
		// }
	}
	
	/**
	 * Defines the list of classes that this subscribable event listener class listens for changes
	 * to.
	 * 
	 * @return a list of classes that this can handle
	 */
	public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
		List objects = new ArrayList<Class<? extends OpenmrsObject>>();
		objects.add(Patient.class);
		return objects;
	}
	
	/**
	 * Defines the list of Actions that this subscribable event listener class listens out for.
	 * 
	 * @return a list of Actions this listener can deal with
	 */
	public abstract List<String> subscribeToActions();
	
	/**
	 * Perform action on the MPI.
	 * 
	 * @param message message with properties.
	 */
	public abstract void performAction(Message message);
	
	/**
	 * Retrieves the patient from the DB based on PatientUuid in message.
	 * 
	 * @param message message with properties.
	 * @return retrieved patient
	 */
	protected Patient extractPatient(Message message) {
		validateMessage(message);
		// Property name referenced from org.openmrs.event.EventEngine.fireEvent(javax.jms.Destination, java.lang.Object)
		String patientUuid = getMessagePropertyValue(message, "uuid");
		return getPatient(patientUuid);
	}
	
	/**
	 * Gets value of the property with the provided name from the provided message.
	 * 
	 * @param message message with property
	 * @param propertyName name of the property that you want to get the value of
	 * @return retrieved patient
	 */
	protected String getMessagePropertyValue(Message message, String propertyName) {
		validateMessage(message);
		try {
			return ((MapMessage) message).getString(propertyName);
		}
		catch (JMSException e) {
			throw new APIException("Exception while get uuid of created patient from JMS message. " + e);
		}
	}
	
	/**
	 * Validate that the message is an instance of MapMessage.
	 * 
	 * @param message message to be validated
	 */
	private void validateMessage(Message message) {
		if (!(message instanceof MapMessage)) {
			throw new APIException("Event message should be MapMessage, but it isn't");
		}
	}
	
	/**
	 * Gets patient from local DB using patient uuid provided.
	 * 
	 * @param patientUuid uuid of patient to be retrieved.
	 * @return retrieved patient
	 */
	private Patient getPatient(String patientUuid) {
		Patient patient = patientService.getPatientByUuid(patientUuid);
		if (patient == null) {
			throw new APIException("Unable to retrieve patient by uuid");
		}
		return patient;
	}
}
