package ru.mail.tp.callbackpal;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.tp.callbackpal.api.CommutationService;
import ru.mail.tp.callbackpal.api.ValidationService;
import ru.mail.tp.callbackpal.api.models.CommutateSubscribersResult;
import ru.mail.tp.callbackpal.api.models.ValidationCode;

public class CallbackIntentService extends IntentService {

	public static final String ACTION_INIT_CALLBACK = "ru.mail.tp.callbackpal.ACTION_INIT_CALLBACK";
	public static final String ACTION_REQUEST_VALIDATION_CODE = "ru.mail.tp.callbackpal.ACTION_REQUEST_VALIDATION_CODE";
	private static final String ACTION_INIT_CALLBACK_RESULT = "ru.mail.tp.callbackpal.ACTION_INIT_CALLBACK_RESULT";
	public static final String ACTION_REQUEST_VALIDATION_CODE_RESULT = "ru.mail.tp.callbackpal.ACTION_REQUEST_VALIDATION_CODE_RESULT";

	public static final String EXTRA_NUMBER_A = "ru.mail.tp.callbackpal.NUMBER_A";
	public static final String EXTRA_NUMBER_B = "ru.mail.tp.callbackpal.NUMBER_B";

	public static final String EXTRA_PHONE_NUMBER = "ru.mail.tp.callbackpal.EXTRA_PHONE_NUMBER";

	private static final String EXTRA_INIT_CALLBACK_RESULT = "ru.mail.tp.callbackpal.EXTRA_INIT_CALLBACK_RESULT";
	public static final String EXTRA_REQUEST_VALIDATION_CODE_RESULT = "ru.mail.tp.callbackpal.EXTRA_REQUEST_VALIDATION_CODE_RESULT";
	public static final String EXTRA_ERROR_MESSAGE = "ru.mail.tp.callbackpal.EXTRA_ERROR_MESSAGE";

	private static final String LOG_TAG = "[IntentService]";

	public CallbackIntentService() {
		super("CallbackIntentService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			Log.d(LOG_TAG, String.format("New intent with action %s", action));
			if (ACTION_INIT_CALLBACK.equals(action)) {
				final String numberA = intent.getStringExtra(EXTRA_NUMBER_A);
				final String numberB = intent.getStringExtra(EXTRA_NUMBER_B);
				handleInitCallback(numberA, numberB);
			} else if (action.equals(ACTION_REQUEST_VALIDATION_CODE)) {
				final String phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
				handleRequestValidationCode(phoneNumber);
			}
		}
	}

	private void handleInitCallback(String numberA, String numberB) {
		CommutationService commutationService = CommutationService.retrofit.create(CommutationService.class);
		final Call<CommutateSubscribersResult> call = commutationService.requestCommutation(numberA, numberB);

		call.enqueue(new Callback<CommutateSubscribersResult>() {
			@Override
			public void onResponse(Call<CommutateSubscribersResult> call, Response<CommutateSubscribersResult> response) {
				final String responseBody = response.raw().body().toString();
				Log.d(LOG_TAG, String.format("Request success, response body = %s", responseBody));

				Bundle bundle = new Bundle();
				bundle.putSerializable(EXTRA_INIT_CALLBACK_RESULT, response.body());

				sendBroadcast(bundle, ACTION_INIT_CALLBACK_RESULT);

				Boolean result = response.body().getResult();
				if (result) {
					Log.d(LOG_TAG, "Subs commutated");
				} else {
					Log.d(LOG_TAG, "Subs not commutated");
				}
			}

			@Override
			public void onFailure(Call<CommutateSubscribersResult> call, Throwable t) {
				final String errorMessage = t.getMessage();
				Log.d(LOG_TAG, String.format("Request fails, error message = %s", errorMessage));

				Bundle bundle = new Bundle();
				bundle.putString(EXTRA_ERROR_MESSAGE, errorMessage);

				sendBroadcast(bundle, ACTION_INIT_CALLBACK_RESULT);
			}
		});
	}

	private void handleRequestValidationCode(String phoneNumber) {
		Log.d(LOG_TAG, String.format("New intent with RequestValidationCode action for %s number", phoneNumber));

		ValidationService validationService = ValidationService.retrofit.create(ValidationService.class);
		final Call<ValidationCode> call = validationService.requestValidationCode(phoneNumber);

		call.enqueue(new Callback<ValidationCode>() {
			@Override
			public void onResponse(Call<ValidationCode> call, Response<ValidationCode> response) {
				final String responseBody = response.raw().body().toString();
				Log.d(LOG_TAG, String.format("Request success, response body = %s", responseBody));

				Bundle bundle = new Bundle();
				bundle.putSerializable(EXTRA_REQUEST_VALIDATION_CODE_RESULT, response.body());

				sendBroadcast(bundle, ACTION_REQUEST_VALIDATION_CODE_RESULT);
			}

			@Override
			public void onFailure(Call<ValidationCode> call, Throwable t) {
				final String errorMessage = t.getMessage();
				Log.d(LOG_TAG, String.format("Request fails, error message = %s", errorMessage));

				Bundle bundle = new Bundle();
				bundle.putString(EXTRA_ERROR_MESSAGE, errorMessage);

				sendBroadcast(bundle, ACTION_REQUEST_VALIDATION_CODE_RESULT);
			}
		});
	}
	private void sendBroadcast(Bundle bundle, String action) {
		final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(action);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtras(bundle);
		localBroadcastManager.sendBroadcast(broadcastIntent);
	}
}
