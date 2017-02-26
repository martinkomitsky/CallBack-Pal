package ru.mail.tp.callbackpal.contacts;

public class Contact {
	public String ContactName;
	public String ContactNumber;

	public Contact (String name, String number) {
		this.ContactName = name;
		this.ContactNumber = number;
	}

	Contact() {}

	public String getContactName() {
		return ContactName;
	}

	public String getContactNumber() {
		return ContactNumber;
	}

	public void setContactNumber(String contactNumber) {
		ContactNumber = contactNumber;
	}
}
