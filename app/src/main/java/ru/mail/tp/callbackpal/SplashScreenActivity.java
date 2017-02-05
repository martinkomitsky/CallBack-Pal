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

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!backPressed) {
					if (isValidated) {
						Log.d("Phone is", "validated");
						Intent startSecondActivity = new Intent(SplashScreenActivity.this, ContactsListActivity.class);
						startActivity(startSecondActivity);
					} else {
						Log.d("Phone is", "not validated");
						Intent startSecondActivity = new Intent(SplashScreenActivity.this, LoginActivity.class);
						startActivity(startSecondActivity);
					}
					finish();
				}
			}
		}, 2000);
	}

	@Override
	protected void onStop() {
		backPressed = true;
		super.onStop();
	}
}
