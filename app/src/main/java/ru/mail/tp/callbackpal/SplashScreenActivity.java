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
	public boolean isValidated;
	private boolean backPressed;


	private static class DelayedHandler extends Handler {
		private final WeakReference<SplashScreenActivity> mActivity;

		public DelayedHandler(SplashScreenActivity splashScreenActivity) {
			mActivity = new WeakReference<>(splashScreenActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			SplashScreenActivity activity = mActivity.get();
			if (activity != null) {
				if (!mActivity.get().backPressed) {
					if (mActivity.get().isValidated) {
						Log.d("Phone is", "validated");
						Intent startSecondActivity = new Intent(mActivity.get(), ContactsListActivity.class);
						mActivity.get().startActivity(startSecondActivity);
					} else {
						Log.d("Phone is", "not validated");
						Intent startSecondActivity = new Intent(mActivity.get(), LoginActivity.class);
						mActivity.get().startActivity(startSecondActivity);
					}
					mActivity.get().finish();
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

		SharedPreferences pref = getApplicationContext().getSharedPreferences("ValidationData", MODE_PRIVATE);
		isValidated = pref.getBoolean("phone_validated", false);
		mHandler.sendMessageDelayed(new Message(), 1500);
	}

	@Override
	protected void onStop() {
		backPressed = true;
		super.onStop();
	}
}
