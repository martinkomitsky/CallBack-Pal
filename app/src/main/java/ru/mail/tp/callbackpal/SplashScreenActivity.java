package ru.mail.tp.callbackpal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;

public class SplashScreenActivity extends AppCompatActivity {
	private static final String LOG_TAG = "[SplashScreenActivity]";

	private boolean isValidated;
	private boolean backPressed;
	private final String SHARED_PREF_NAME = "ValidationData";
	private final String SHARED_PREF_VALUE_VALIDATION_STATUS = "phone_validated";

	private static class DelayedHandler extends Handler {
		private final WeakReference<SplashScreenActivity> mActivity;

		private DelayedHandler(SplashScreenActivity splashScreenActivity) {
			mActivity = new WeakReference<>(splashScreenActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			SplashScreenActivity activity = mActivity.get();
			if (activity != null) {
				if (!activity.backPressed) {
					if (activity.isValidated) {
						Log.d(LOG_TAG, "Phone is validated");
						Intent startSecondActivity = new Intent(activity, ContactsListActivity.class);
						activity.startActivity(startSecondActivity);
						activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					} else {
						Log.d(LOG_TAG, "Phone is not validated");
						Intent startSecondActivity = new Intent(activity, LoginActivity.class);
						activity.startActivity(startSecondActivity);
						activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
					activity.finish();
				}
			}
		}
	}

	private final DelayedHandler mHandler = new DelayedHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		backPressed = false;
		ActionBar supportActionBar = getSupportActionBar();

		if (supportActionBar != null) {
			supportActionBar.hide();
		}

		SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
		isValidated = pref.getBoolean(SHARED_PREF_VALUE_VALIDATION_STATUS, false);
		mHandler.sendMessageDelayed(new Message(), 1000);
	}

	@Override
	protected void onStop() {
		backPressed = true;
		super.onStop();
	}
}
