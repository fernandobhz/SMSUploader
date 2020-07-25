package com.smsuploader.service;

import java.net.URLEncoder;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.smsuploader.model.SMS;
import com.smsuploader.tools.Const;
import com.smsuploader.tools.Utils;

public class SMSSender extends Service {

	private static final String NAME = "com.smsuploader.service.SMSSender";

	private static volatile PowerManager.WakeLock lockStatic = null;
	private volatile Worker worker;

	synchronized private static PowerManager.WakeLock getLock(Context context) {

		if (lockStatic == null) {

			PowerManager mgr = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);

			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
			lockStatic.setReferenceCounted(true);
		}

		return (lockStatic);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d("info", "SMSSender onstartcommand");

		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0)
			lock.acquire();

		doWakefulWork(intent);

		super.onStartCommand(intent, flags, startId);

		return (START_REDELIVER_INTENT);
	}

	public void freeLock() {

		Log.d("info", "free log called");

		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (lock.isHeld())
			lock.release();
	}

	protected void doWakefulWork(Intent intent) {
		
		if(worker == null) {
			worker = new Worker();
			worker.execute();
		}
	}

	private class Worker extends AsyncTask<Void, Void, Boolean> {

		private List<SMS> smsList;
		
		@Override
		protected void onPreExecute() {
			smsList = Utils.getSMSFromStorage(SMSSender.this);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			try {

				HttpClient httpclient = new DefaultHttpClient();

				for (SMS sms : smsList) {
					
					HttpGet httpGet = new HttpGet(Const.SERVICE_URL
							+ Const.PARAMETER_SMS_FROM + sms.getAddress()
							+ Const.PARAMETER_SMS_BODY + URLEncoder.encode(sms.getMsg(), "UTF-8")
							+ Const.PARAMETER_SMS_DATETIME + URLEncoder.encode(sms.getDt(), "UTF-8"));
					
					httpclient.execute(httpGet);
					
					Log.d("info", "send request: " + httpGet.getURI().toString());
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.d("info", "something wrong due sending sms information...");
				
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			
			if(result.booleanValue()) {
				Utils.clearStorage(SMSSender.this);
				Log.d("info", "sms storage has been cleared");
			}

			freeLock();
			stopSelf();
		}
	}
}
