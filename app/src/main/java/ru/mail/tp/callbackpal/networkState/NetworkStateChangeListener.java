package ru.mail.tp.callbackpal.networkState;

/**
 * Created by Martin on 18.02.2017.
 * martin00@yandex.ru
 */

public interface NetworkStateChangeListener {
	void onNetworkStateChange (String message, boolean state);
}
