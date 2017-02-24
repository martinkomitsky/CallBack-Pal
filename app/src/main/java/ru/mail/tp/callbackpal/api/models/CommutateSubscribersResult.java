package ru.mail.tp.callbackpal.api.models;

import java.io.Serializable;

/**
 * Created by Martin on 19.01.2017.
 * martin00@yandex.ru
 */

public class CommutateSubscribersResult implements Serializable {

	private String function;
	private Boolean result;

	CommutateSubscribersResult() {
	}

	CommutateSubscribersResult(String function, Boolean result) {
		this.function = function;
		this.result = result;
	}

	public boolean getResult() {
		return result;
	}
}
