package ru.mail.tp.callbackpal.networkState;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ru.mail.tp.callbackpal.R;

/**
 * Created by Martin on 18.02.2017.
 */

class NetworkUtil {

	private final static int TYPE_NOT_CONNECTED = 0;
	private final static int TYPE_WIFI = 1;
	private final static int TYPE_MOBILE = 2;

	private static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
			.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				return TYPE_WIFI;
			}
			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				return TYPE_MOBILE;
			}
		}
		return TYPE_NOT_CONNECTED;
	}

	static String getConnectivityStatusString(Context context) {
		int conn = NetworkUtil.getConnectivityStatus(context);
		String status = null;
		if (conn == NetworkUtil.TYPE_WIFI) {
			status = context.getString(R.string.info_net_status_wifi_enabled);
		} else if (conn == NetworkUtil.TYPE_MOBILE) {
			status = context.getString(R.string.info_net_status_mobile_enabled);
		} else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
			status = context.getString(R.string.info_net_status_disconnected);
		}
		return status;
	}

	static boolean getConnectivityStatusBoolean(Context context) {
		int conn = NetworkUtil.getConnectivityStatus(context);
		boolean status = false;
		if (conn == NetworkUtil.TYPE_WIFI) {
			status = true;
		} else if (conn == NetworkUtil.TYPE_MOBILE) {
			status = true;
		} else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
			status = false;
		}
		return status;
	}
}
