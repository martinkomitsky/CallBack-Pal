package ru.mail.tp.callbackpal;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mail.tp.callbackpal.contacts.Contact;
import ru.mail.tp.callbackpal.contacts.ContactsAdapter;

public class ContactsListActivity extends AppCompatActivity {


	RecyclerView rvContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);

		List<Contact> contactList = getAllContactsList();
		rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
		ContactsAdapter contactAdapter = new ContactsAdapter(contactList, getApplicationContext());
		rvContacts.setLayoutManager(new LinearLayoutManager(this));
		rvContacts.setAdapter(contactAdapter);
	}

	private List<Contact> getAllContactsList() {
		List<Contact> contactList = new ArrayList();
		Contact contact;

		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
				if (hasPhoneNumber > 0) {
					String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					contact = new Contact();
					contact.setContactName(name);

					Cursor phoneCursor = contentResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[]{id},
							null
					);

					if (phoneCursor.moveToNext()) {
						String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						contact.setContactNumber(phoneNumber);
					}
					phoneCursor.close();
					contactList.add(contact);
				}
			}
		}
		return contactList;
	}
}
