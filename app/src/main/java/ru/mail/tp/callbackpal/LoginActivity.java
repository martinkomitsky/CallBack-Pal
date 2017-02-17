package ru.mail.tp.callbackpal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.pinball83.maskededittext.MaskedEditText;

import ru.mail.tp.callbackpal.api.models.ValidationCode;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int REQUEST_READ_CONTACTS = 0;
	private static final String LOG_TAG = "[LoginActivity]";

	private String validationPin = null;
	private String currentPhone;

	// UI references.
	private AutoCompleteTextView mEmailView;
	private MaskedEditText mPhoneView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

	private BroadcastReceiver broadcastReceiver;
	private boolean intentAwaiting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if (mayRequestContacts()) {
			Log.d(LOG_TAG, "May request contacts");
		}
		this.setTitle(getResources().getString(R.string.action_sign_in_short));

		// Set up the login form.
		mPhoneView = (MaskedEditText) findViewById(R.id.number_masked);
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
		View mRetryView = findViewById(R.id.retry_validation);

		final TextView mTimerView = (TextView) findViewById(R.id.timer_view);


		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
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
					Log.d(LOG_TAG, "edit: " + enteredPin);

					SharedPreferences pref =  getApplicationContext().getSharedPreferences("ValidationData", MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();

					if (isPasswordValid(enteredPin)) {
						Log.d(LOG_TAG, "Phone validation success!");

						editor.putBoolean("phone_validated", true);
						editor.putString("phone", "+7" + mPhoneView.getUnmaskedText());
						editor.apply();

						Intent startSecondActivity = new Intent(LoginActivity.this, ContactsListActivity.class);
						LoginActivity.this.startActivity(startSecondActivity);
						finish();

					} else {
						mPasswordView.setError(getString(R.string.error_invalid_password));
						editor.putBoolean("phone_validated", false);
						editor.apply();
					}
				}
			}
		});

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(LOG_TAG, "REQUEST_VALIDATION_CODE_RESULT intent was received");
				intentAwaiting = false;
				showProgress(false);
				// if success is false - do this
//				mPasswordView.setError(activity.getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();

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

					// TODO: change this setText to another textView
					mPasswordView.setText(String.format(getString(R.string.error_message), errorMessage));
				}
			}
		};

		Log.d(LOG_TAG, "Creating Local Intent Filter");
		IntentFilter intentFilter = new IntentFilter(CallbackIntentService.ACTION_REQUEST_VALIDATION_CODE_RESULT);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
	}

	private boolean mayRequestContacts() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin() {
		if (intentAwaiting) {
			return;
		}

		// Reset errors.
		mPhoneView.setError(null);
//		mEmailView.setError(null);
//		mPasswordView.setError(null);

		String phone = mPhoneView.getUnmaskedText();
		this.currentPhone = phone;
//		String email = mEmailView.getText().toString();
//		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid phone.
		if (TextUtils.isEmpty(phone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (!isPhoneValid(phone)) {
			mPhoneView.setError(getString(R.string.error_invalid_phone));
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true);
			intentAwaiting = true;

			// TODO: add "if" and move to another fn
			mPasswordView.setVisibility(View.VISIBLE);
			mPasswordView.requestFocus();
			mPhoneView.setEnabled(false);

			TextView callbackCaption = (TextView) findViewById(R.id.callback_caption);
			callbackCaption.setVisibility(View.VISIBLE);

			TextView retryValidation = (TextView) findViewById(R.id.retry_validation);
			retryValidation.setVisibility(View.VISIBLE);

			Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
			mEmailSignInButton.setVisibility(View.GONE);

			requestValidationCall(phone);
		}
	}

	private boolean isPhoneValid(String phone) {
		return phone.length() == 10;
	}

	private boolean isEmailValid(String email) {
		return email.contains("@");
	}

	private boolean isPasswordValid(String password) {
		return password.length() == 4 && password.equals(validationPin);
	}

	private void requestValidationCall (String phone) {
		if (phone != null && phone.length() > 0) {
			Intent intent = new Intent(getApplicationContext(), CallbackIntentService.class)
					.setAction(CallbackIntentService.ACTION_REQUEST_VALIDATION_CODE)
					.putExtra(CallbackIntentService.EXTRA_PHONE_NUMBER, String.format("+7%s", phone));
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
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
}
