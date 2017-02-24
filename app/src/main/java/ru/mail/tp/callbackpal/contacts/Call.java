package ru.mail.tp.callbackpal.contacts;

import java.util.Date;

/**
 * Created by Martin on 24.02.2017.
 * martin00@yandex.ru
 */

public class Call extends Contact {
	public int id;
	public Date date;

	public Call(String contactName, String contactNumber, Date date) {
		this.ContactName = contactName;
		this.ContactNumber = contactNumber;
		this.date = date;
	}
	public Call() {

	}
}
