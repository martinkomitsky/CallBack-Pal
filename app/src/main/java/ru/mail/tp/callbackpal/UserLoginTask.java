package ru.mail.tp.callbackpal;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.tp.callbackpal.LoginActivity;
import ru.mail.tp.callbackpal.api.models.ValidationCode;
import ru.mail.tp.callbackpal.api.ValidationService;

/**
 * Created by Martin on 25.12.2016.
 */

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
//    public UserLoginTask mAuthTask = null;

//    private UserLoginTask mAuthTask = LoginActivity.mAuthTask;
//    private String[] DUMMY_CREDENTIALS = LoginActivity.DUMMY_CREDENTIALS;
//    private void showProgress = LoginActivity.

    private static WeakReference<LoginActivity> mActivityRef;
    public static void updateActivity(LoginActivity activity) {
        mActivityRef = new WeakReference<LoginActivity>(activity);
    }

    UserLoginTask(String email, String password) {
        mEmail = email;
        mPassword = password;

//        UserLoginTask mAuthTask = loginActivity.m

    }



    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.


        ValidationService validationService = ValidationService.retrofit.create(ValidationService.class);
        final Call<ValidationCode> call =
                validationService.requestValidationCode();

        call.enqueue(new Callback<ValidationCode>() {
            @Override
            public void onResponse(Call<ValidationCode> call, Response<ValidationCode> response) {
                Log.d("sas", "asdasd");
//                final TextView textView = (TextView) findViewById(R.id.textView);
//                textView.setText(response.body().toString());
            }
            @Override
            public void onFailure(Call<ValidationCode> call, Throwable t) {
                Log.d("not sas", "asdasd");
//                final TextView textView = (TextView) findViewById(R.id.textView);
//                textView.setText("Something went wrong: " + t.getMessage());
            }
        });

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        for (String credential : mActivityRef.get().DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(mEmail)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(mPassword);
            }
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        mActivityRef.get().mAuthTask = null;

        if (success) {
            mActivityRef.get().showProgress(false);
//            mActivityRef.get().finish();
        } else {
            mActivityRef.get().mPasswordView.setError(mActivityRef.get().getString(R.string.error_incorrect_password));
            mActivityRef.get().mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        mActivityRef.get().mAuthTask = null;
        mActivityRef.get().showProgress(false);
    }
}