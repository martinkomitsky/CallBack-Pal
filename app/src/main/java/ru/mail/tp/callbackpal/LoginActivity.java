package ru.mail.tp.callbackpal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.pinball83.maskededittext.MaskedEditText;

import ru.mail.tp.callbackpal.api.models.ValidationCode;
import ru.mail.tp.callbackpal.networkState.NetworkChangeReceiver;
import ru.mail.tp.callbackpal.networkState.NetworkStateChangeListener;
import ru.mail.tp.callbackpal.utils.InformerCreator;
import ru.mail.tp.callbackpal.utils.SharedPreferenceHelper;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via validated phone.
 */
public class LoginActivity extends AppCompatActivity {

	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int REQUEST_READ_CONTACTS = 0;
	private static final String LOG_TAG = "[LoginActivity]";

	private final String PHONE_COUNTRY_CODE = "+7";
	private final String PHONE_COUNTRY_CODE_TEMPLATE = "+7%s";

	private String validationPin = null;
	private String currentPhone;

	// UI references.
	private MaskedEditText mPhoneView;
	private EditText mPasswordView;
	private TextView mErrorDescriptionView;
	private TextView mTimerView;
	private View mProgressView;
	private View mLoginFormView;
	private View mCallbackCaption;
	private View mRetryView;
	private View mSignInButton;

	private ActionBar mActionBar;

	private BroadcastReceiver broadcastReceiver;
	private BroadcastReceiver networkChangedBroadcastReceiver;
	private boolean intentAwaiting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if (mayRequestContacts()) {
			Log.d(LOG_TAG, "May request contacts");
		}
		this.setTitle(getResources().getString(R.string.action_sign_in_short));

		mPhoneView = (MaskedEditText) findViewById(R.id.number_masked);
		mPasswordView = (EditText) findViewById(R.id.password);
		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
		mRetryView = findViewById(R.id.retry_validation);
		mCallbackCaption = findViewById(R.id.callback_caption);
		mSignInButton = findViewById(R.id.sign_in_button);
		mErrorDescriptionView = (TextView) findViewById(R.id.error_description);
		mTimerView = (TextView) findViewById(R.id.timer_view);
		mActionBar = getSupportActionBar();

		bindEventListeners();

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "REQUEST_VALIDATION_CODE_RESULT intent was received");
			intentAwaiting = false;
			showProgress(false);
			mErrorDescriptionView.setVisibility(View.GONE);
			Bundle bundle = intent.getExtras();

			final ValidationCode validationCode = (ValidationCode) bundle.getSerializable(CallbackIntentService.EXTRA_REQUEST_VALIDATION_CODE_RESULT);
			final String errorMessage = bundle.getString(CallbackIntentService.EXTRA_ERROR_MESSAGE);

			if (validationCode != null) {
				Log.d(LOG_TAG, String.format("ValidationCode data: {result:%s, pin:%s}", validationCode.getResult(), validationCode.getPin()));
				if (validationCode.getResult()) {
					validationPin = validationCode.getPin();
				} else {
					validationPin = null;
					mPasswordView.setError(getString(R.string.error_incorrect_password));
				}
			} else if (errorMessage != null && !errorMessage.isEmpty()) {
				Log.d(LOG_TAG, String.format("An error occurred: %s", errorMessage));

				mErrorDescriptionView.setVisibility(View.VISIBLE);
				mErrorDescriptionView.setText(String.format(getString(R.string.error_message), errorMessage));
			}
			}
		};

		Log.d(LOG_TAG, "Creating Local Intent Filter");
		IntentFilter intentFilter = new IntentFilter(CallbackIntentService.ACTION_REQUEST_VALIDATION_CODE_RESULT);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

		networkChangedBroadcastReceiver = new NetworkChangeReceiver(new CallbackRunner());
		IntentFilter networkChangedFilter = new IntentFilter(NetworkChangeReceiver.ACTION_CONN_CHANGE);
		networkChangedFilter.addAction(NetworkChangeReceiver.ACTION_WIFI_CHANGE);
		networkChangedFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(networkChangedBroadcastReceiver, networkChangedFilter);
	}

	private void bindEventListeners () {
		mSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mRetryView.setOnClickListener(new OnClickListener() {
			private Boolean permission = true;

			@Override
			public void onClick(View v) {
				if (permission) {
					mErrorDescriptionView.setVisibility(View.GONE);
					requestValidationCall(currentPhone);
					permission = false;

					new CountDownTimer(30000, 1000) {
						public void onTick(long millisUntilFinished) {
							mTimerView.setVisibility(View.VISIBLE);
							mTimerView.setText(String.format(getString(R.string.timer), millisUntilFinished / 1000));
						}

						public void onFinish() {
							mTimerView.setText("");
							permission = true;
							mTimerView.setVisibility(View.GONE);
						}
					}.start();
				}
			}
		});

		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
			if (id == R.id.login || id == EditorInfo.IME_NULL) {
				attemptLogin();
				return true;
			}
			return false;
			}
		});

		mPasswordView.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String enteredPin = s.toString();
				if (enteredPin.length() == 4) {
					if (isPasswordValid(enteredPin)) {
						Log.d(LOG_TAG, "Phone validation success!");

						SharedPreferenceHelper.setValue(getApplicationContext(), SharedPreferenceHelper.SHARED_PREF_VALUE_VALIDATION_STATUS, true);
						SharedPreferenceHelper.setValue(getApplicationContext(), SharedPreferenceHelper.SHARED_PREF_VALUE_PHONE, PHONE_COUNTRY_CODE + mPhoneView.getUnmaskedText());

						Intent startSecondActivity = new Intent(LoginActivity.this, MainDrawerActivity.class);
						LoginActivity.this.startActivity(startSecondActivity);
						finish();

					} else {
						mPasswordView.setError(getString(R.string.error_invalid_password));
						SharedPreferenceHelper.setValue(getApplicationContext(), SharedPreferenceHelper.SHARED_PREF_VALUE_VALIDATION_STATUS, false);
					}
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			showSecondStep(false);
		}

		return super.onOptionsItemSelected(item);
	}

	private boolean mayRequestContacts() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			Snackbar.make(mPasswordView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener() {
						@Override
						@TargetApi(Build.VERSION_CODES.M)
						public void onClick(View v) {
							requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
						}
					});
		} else {
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode == REQUEST_READ_CONTACTS) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(LOG_TAG, "Permission Granted");
			}
		}
	}

	/**
	 * Attempts to sign in account specified by the login form.
	 * If there are form errors (invalid phone, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin() {
		if (!intentAwaiting) {
			boolean cancel = false;
			View focusView = null;

			mPhoneView.setError(null);
			currentPhone = mPhoneView.getUnmaskedText();

			// Check for a valid phone.
			if (TextUtils.isEmpty(currentPhone)) {
				mPhoneView.setError(getString(R.string.error_field_required));
				focusView = mPhoneView;
				cancel = true;
			} else if (!isPhoneValid(currentPhone)) {
				mPhoneView.setError(getString(R.string.error_invalid_phone));
				focusView = mPhoneView;
				cancel = true;
			}

			if (cancel) {
				focusView.requestFocus();
			} else {
				showProgress(true);
				showSecondStep(true);
				if (mActionBar != null) {
					mActionBar.setDisplayHomeAsUpEnabled(true);
				}
				intentAwaiting = true;
				requestValidationCall(currentPhone);
			}
		}
	}

	private void showSecondStep (final boolean showSecond) {
		if (showSecond) {
			mSignInButton.setVisibility(View.GONE);
			mPasswordView.setVisibility(View.VISIBLE);
			mCallbackCaption.setVisibility(View.VISIBLE);
			mRetryView.setVisibility(View.VISIBLE);
			mPhoneView.setEnabled(false);
			mPasswordView.requestFocus();
		} else {
			mPasswordView.setVisibility(View.GONE);
			mCallbackCaption.setVisibility(View.GONE);
			mRetryView.setVisibility(View.GONE);
			mSignInButton.setVisibility(View.VISIBLE);
			mPhoneView.setEnabled(true);
			mPhoneView.requestFocus();
			mActionBar.setDisplayHomeAsUpEnabled(false);
		}
	}

	private boolean isPhoneValid(String phone) {
		return phone.length() == 10;
	}

	private boolean isPasswordValid(String password) {
		return password.length() == 4 && password.equals(validationPin);
	}

	private void requestValidationCall (String phone) {
		if (phone != null && phone.length() > 0) {
			Intent intent = new Intent(getApplicationContext(), CallbackIntentService.class)
				.setAction(CallbackIntentService.ACTION_REQUEST_VALIDATION_CODE)
				.putExtra(CallbackIntentService.EXTRA_PHONE_NUMBER, String.format(PHONE_COUNTRY_CODE_TEMPLATE, phone));
			startService(intent);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(LOG_TAG, "onSaveInstanceState");
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(LOG_TAG, "onRestoreInstanceState");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		unregisterReceiver(networkChangedBroadcastReceiver);
	}

	public class CallbackRunner implements NetworkStateChangeListener {

		@Override
		public void onNetworkStateChange(String message, boolean state) {
			Log.d(LOG_TAG, message);
			InformerCreator.showSnack(message, state, findViewById(R.id.email_login_form));
		}
	}
}
