package ru.mail.tp.callbackpal.api.models;

import java.io.Serializable;

/**
 * Created by Martin on 19.01.2017.
 */

public class CommutateSubscribersResult implements Serializable {

	private String function;
	private Boolean result;

	void CommutateSubscribersResult() {
	}

	void CommutateSubscribersResult(String function, Boolean result) {
		this.function = function;
		this.result = result;
	}

	public boolean getResult() {
		return result;
	}
}
