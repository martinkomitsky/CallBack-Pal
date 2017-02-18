package ru.mail.tp.callbackpal.networkState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Martin on 18.02.2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
	private NetworkStateChangeListener obj;

	public NetworkChangeReceiver(NetworkStateChangeListener obj) {
		this.obj = obj;
	}

	public NetworkChangeReceiver() {

	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		String status = NetworkUtil.getConnectivityStatusString(context);
		boolean state = NetworkUtil.getConnectivityStatusBoolean(context);
		obj.onNetworkStateChange(status, state);
	}
}
