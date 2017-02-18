package ru.mail.tp.callbackpal.networkState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Martin on 18.02.2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {

		String status = NetworkUtil.getConnectivityStatusString(context);

		Toast.makeText(context, status, Toast.LENGTH_LONG).show();
	}
}