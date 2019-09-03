package org.openmrs.module.posttoopenhim.api.impl;

import org.openmrs.Patient;
import org.openmrs.event.Event;

import javax.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.posttoopenhim.api.Tunnel;

/**
 * This class listens for patient UPDATED events. If MPI is enabled it updates patient in MPI.
 */
public class PatientUpdatedListener extends PatientActionListener {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PatientUpdatedListener.class);
	
	/**
	 * Defines the list of Actions that this subscribable event listener class listens out for.
	 * 
	 * @return a list of Actions this listener can deal with
	 */
	public List<String> subscribeToActions() {
		List actions = new ArrayList<String>();
		actions.add(Event.Action.UPDATED.name());
		return actions;
	}
	
	/**
	 * Update patient in MPI server.
	 * 
	 * @param message message with properties.
	 */
	@Override
	public void performMpiAction(Message message) {
		log.info("[info]------ got an updated patient");
		Patient patient = extractPatient(message);
		Tunnel tunnel = new Tunnel(patient);
		tunnel.send();
		
	}
}
