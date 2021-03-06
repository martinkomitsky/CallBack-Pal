package ru.mail.tp.callbackpal.api.models;

import java.io.Serializable;

/**
 * Created by Martin on 07.01.2017.
 * martin00@yandex.ru
 */

public class ValidationCode implements Serializable {

	private String function;
	private Boolean result;
	private Integer pin;

	ValidationCode() {
	}

	ValidationCode(String function, Boolean result, Integer pin) {
		this.function = function;
		this.result = result;
		this.pin = pin;
	}

	public String getPin() {
		return String.valueOf(this.pin);
	}

	public boolean getResult() {
		return result;
	}
}
