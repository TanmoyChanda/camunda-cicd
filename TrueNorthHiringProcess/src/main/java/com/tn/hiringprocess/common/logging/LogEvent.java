package com.tn.hiringprocess.common.logging;




public class LogEvent {

	private String loggerName;
	private String message;
	private String logObject;
	private RelativeContext context;

	public LogEvent(String loggerName, String message) {
		super();
		this.loggerName = loggerName;
		this.message = message;
	}

	public String getLogObject() {
		return this.logObject;
	}

	public void setLogObject(String logObject) {
		this.logObject = logObject;
	}

	public String getLoggerName() {
		return this.loggerName;
	}

	public String getMessage() {
		return this.message;
	}

	public RelativeContext getContext() {
		return this.context;
	}

	public void setContext(RelativeContext context) {
		this.context = context;
	}


}
