package com.smsuploader.receiver;

import com.smsuploader.service.SMSSender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
	
	public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        
        return activeNetwork != null;
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("info", "start network receiver...");
		
		if(isNetworkConnected(context)) {
			Log.d("info", "network connected, run service");
			
			context.startService(new Intent(context, SMSSender.class));
		}
		else
			Log.d("info", "network is turned off, pass away");
	}
}
