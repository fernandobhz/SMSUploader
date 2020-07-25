package com.smsuploader.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SMS implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String address;
	private String msg;
	private String dt;
	
	public SMS(String address, String msg, Date dt) {
		this.address = address;
		this.msg = msg;
		
		DateFormat dtFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.getDefault());				
		this.dt = dtFormat.format(dt);		
	}
	
	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
