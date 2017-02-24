package ru.mail.tp.callbackpal.networkState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Martin on 18.02.2017.
 * martin00@yandex.ru
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
	private NetworkStateChangeListener obj;

	public static final String ACTION_CONN_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_WIFI_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";

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
