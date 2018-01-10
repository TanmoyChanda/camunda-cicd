package com.tn.hiringprocess.bpm.controller;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;

@ProcessApplication("TN Hiring Process")
public class TNHiringProcessApplication extends ServletProcessApplication { 


	@Autowired
	protected ProcessEngine processEngine;

	public void afterPropertiesSet() throws Exception {

	}

	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	@PostDeploy
	public void onDeploymentFinished(ProcessEngine processEngine) {
		System.out.println("TN Hiring Process deployed");
	}
}