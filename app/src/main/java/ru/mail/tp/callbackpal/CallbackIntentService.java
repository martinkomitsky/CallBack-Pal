package ru.mail.tp.callbackpal;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.tp.callbackpal.api.CommutationService;
import ru.mail.tp.callbackpal.api.models.CommutateSubscribersResult;

public class CallbackIntentService extends IntentService {

    public static final String ACTION_INIT_CALLBACK = "ru.mail.tp.callbackpal.ACTION_INIT_CALLBACK";
    public static final String NUMBER_A = "ru.mail.tp.callbackpal.NUMBER_A";
    public static final String NUMBER_B = "ru.mail.tp.callbackpal.NUMBER_B";

    public CallbackIntentService() {
        super("CallbackIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_CALLBACK.equals(action)) {
                final String numberA = intent.getStringExtra(NUMBER_A);
                final String numberB = intent.getStringExtra(NUMBER_B);

                CommutationService commutationService = CommutationService.retrofit.create(CommutationService.class);
                final Call<CommutateSubscribersResult> call = commutationService.requestCommutation(numberA, numberB);

                call.enqueue(new Callback<CommutateSubscribersResult>() {
                    @Override
                    public void onResponse(Call<CommutateSubscribersResult> call, Response<CommutateSubscribersResult> response) {
                        Log.d("[Subs commutation]", "sas");
                    }

                    @Override
                    public void onFailure(Call<CommutateSubscribersResult> call, Throwable t) {
                        Log.d("[Subs commutation]", t.getMessage());
                    }
                });

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("IntentService", "onCreate");
    }

}
