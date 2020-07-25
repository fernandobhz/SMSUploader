package com.smsuploader;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.smsuploader.model.SMS;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SMS sms = new SMS("", "", new Date());
		Log.d("info", "result date: " + sms.getDt());
		
		Toast.makeText(this, "Monitoring service has been started", Toast.LENGTH_LONG).show();
		finish();
	}
}
