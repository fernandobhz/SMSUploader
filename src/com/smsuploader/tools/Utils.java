package com.smsuploader.tools;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.smsuploader.model.SMS;

public class Utils {
	
	private static final String PREF_KEY_SMS_DATA_KEYS = "pref_key_sms_data_keys";
	
	@SuppressWarnings("unchecked")
	public static ArrayList<SMS> getSMSFromStorage(Context context) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		ArrayList<SMS> smsList = new ArrayList<SMS>();

		try {			
			ArrayList<SMS> list = (ArrayList<SMS>)ObjectSerializer.deserialize(prefs.getString(PREF_KEY_SMS_DATA_KEYS, null));
			
			if(list !=null) {				
				for(SMS sms : list) 
					smsList.add(sms);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return smsList;
	}

	public static void writeSMSToStorage(Context context, ArrayList<SMS> smsList) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();

		try {
			editor.putString(PREF_KEY_SMS_DATA_KEYS, ObjectSerializer.serialize(smsList));			
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void clearStorage(Context context) {
		writeSMSToStorage(context, new ArrayList<SMS>());
	}
}
