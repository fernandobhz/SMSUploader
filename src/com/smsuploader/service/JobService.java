package com.smsuploader.service;

import java.util.ArrayList;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.smsuploader.model.SMS;
import com.smsuploader.receiver.SMSReceiver;
import com.smsuploader.tools.Utils;

public class JobService extends Service {

	private static final String NAME = "com.smsuploader.service.JobService";

	private static volatile PowerManager.WakeLock lockStatic = null;

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
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d("info", "JobService onstartcommand");

		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0)
			lock.acquire();

		doWakefulWork(intent);

		super.onStartCommand(intent, flags, startId);

		return (START_REDELIVER_INTENT);
	}

	protected void doWakefulWork(Intent intent) {

		receiveSMS(intent);
		startService(new Intent(this, SMSSender.class));
		freeLock();
	}
	
	private void receiveSMS(Intent intent) {
		
		if (intent != null && intent.getExtras() != null) {

			Object[] pdus = (Object[]) intent.getExtras().getBundle(SMSReceiver.EXTRAS).get("pdus");
			ArrayList<SMS> smsList = Utils.getSMSFromStorage(this);
			
			if(pdus == null)
				return ;
			
			Log.d("info", "got " + pdus.length + " new sms");

			for (Object pdu : pdus) {
				SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);				
				smsList.add(new SMS(msg.getOriginatingAddress(), msg.getMessageBody(), new Date()));
			}
			
			Utils.writeSMSToStorage(this, smsList);
		}		
	}

	public void freeLock() {

		Log.d("info", "free log called");

		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (lock.isHeld())
			lock.release();
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}
}
