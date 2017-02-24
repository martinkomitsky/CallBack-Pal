package ru.mail.tp.callbackpal.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Martin on 23.02.2017.
 * martin00@yandex.ru
 */

public class SharedPreferenceHelper {
	private static final String SHARED_PREF_NAME = "ValidationData";
	public static final String SHARED_PREF_VALUE_VALIDATION_STATUS = "phone_validated";
	public static final String SHARED_PREF_VALUE_PHONE = "phone";

	public static boolean isValidated(Context mContext) {
		SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
		return pref.getBoolean(SHARED_PREF_VALUE_VALIDATION_STATUS, false);
	}

	public static void setValue(Context mContext, String key, String value) {
		SharedPreferences pref =  mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putString(key, value);
		editor.apply();
	}

	public static void setValue(Context mContext, String key, boolean value) {
		SharedPreferences pref =  mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(key, value);
		editor.apply();
	}

	public static String getValue(Context mContext, String key) {
		SharedPreferences pref =  mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
		return pref.getString(key, null);
	}
}
