package com.tn.hiringprocess.bpm.service.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tn.hiringprocess.bpm.service.ITNHiringJavaDelegateService;

public class TNHiringJRJavaDelegateServiceImpl implements ITNHiringJavaDelegateService, ApplicationContextAware {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		String interviewers = (String) execution.getVariable("interviewers");
		String requestor = (String) execution.getVariable("requestor");
		String hrLead = (String) execution.getVariable("hrLead");
		String[] assigneeList = interviewers.split(",");
		List<String> assignee = new ArrayList<String>(Arrays.asList(assigneeList));
//		String requestor = "demo";
//		List<String> assignee = new ArrayList<String>();
//		assignee.add("john");
//		assignee.add("mary");
		assignee.add(requestor);
		
		execution.setVariable("requestorAndStakeholders", assignee);
		execution.setVariable("HRLead", hrLead);
		execution.setVariable("requestor", requestor);
		//execution.setVariable("action", "yes");
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub
		
	}

}
