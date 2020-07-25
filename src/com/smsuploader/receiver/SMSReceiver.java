package com.smsuploader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.smsuploader.service.JobService;

public class SMSReceiver extends BroadcastReceiver {
	
	public static final String EXTRAS = "sms_receiver_extras";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("info", "SMSReceiver onreceive");
		
		Bundle extras = intent.getExtras();
	    
	    Intent serviceIntent = new Intent(context, JobService.class);
	    serviceIntent.putExtra(EXTRAS, extras);
		
		context.startService(serviceIntent);		
	}
}
