package ru.mail.tp.callbackpal.api.models;

/**
 * Created by Martin on 07.01.2017.
 */

public class ValidationCode {

    String function;
    Boolean result;
    Integer pin;

    void ValidationCode() {
    }

    void ValidationCode(String function, Boolean result, Integer pin) {
        this.function = function;
        this.result = result;
        this.pin = pin;
    }

    public String getPin () {
        return String.valueOf(this.pin);
    }
}
