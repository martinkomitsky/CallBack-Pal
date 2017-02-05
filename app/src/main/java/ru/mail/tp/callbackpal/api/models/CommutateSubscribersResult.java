package ru.mail.tp.callbackpal.api.models;

/**
 * Created by Martin on 19.01.2017.
 */

public class CommutateSubscribersResult {

	String function;
	Boolean result;

	void CommutateSubscribersResult() {
	}

	void CommutateSubscribersResult(String function, Boolean result) {
		this.function = function;
		this.result = result;
	}
}
