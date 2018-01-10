package com.tn.hiringprocess.bpm.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public interface ITNHiringJavaDelegateService extends JavaDelegate{

	public void execute(DelegateExecution execution) throws Exception;
}
