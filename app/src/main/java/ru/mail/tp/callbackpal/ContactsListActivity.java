package ru.mail.tp.callbackpal;

import android.app.LoaderManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ru.mail.tp.callbackpal.contacts.Contact;
import ru.mail.tp.callbackpal.contacts.ContactsAdapter;
import android.widget.TextView;

public class ContactsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
	private RecyclerView rvContacts;
	private ContactsAdapter contactAdapter;
	private List<Contact> contactList;


	private static final String[] PROJECTION = {
			ContactsContract.Data._ID,
			ContactsContract.Data.HAS_PHONE_NUMBER,
			ContactsContract.Data.DISPLAY_NAME
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);

//		List<Contact> contactList = getAllContactsList();
		rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
		getLoaderManager().initLoader(0, null, this);

		this.contactList = contactList;
//		rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
//		ContactsAdapter contactAdapter = new ContactsAdapter(contactList, getApplicationContext());
//		this.contactAdapter = contactAdapter;
//		rvContacts.setLayoutManager(new LinearLayoutManager(this));
//		rvContacts.setAdapter(contactAdapter);


		this.contactAdapter = contactAdapter;
//		rvContacts.setLayoutManager(new LinearLayoutManager(this));
//		rvContacts.setAdapter(contactAdapter);
	}

	private void showTimerDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.popup_window);
		dialog.setTitle(R.string.info_dial);

		final TextView mDialogText = (TextView) dialog.findViewById(R.id.text);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		new CountDownTimer(15000, 1000) {

			public void onTick(long millisUntilFinished) {
				mDialogText.setText(String.format(getString(R.string.info_next_call), millisUntilFinished / 1000));
			}

			public void onFinish() {
				mDialogText.setText("");
				dialog.dismiss();
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contacts_menu, menu);

		final MenuItem item = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
		searchView.setOnQueryTextListener(this);

		MenuItemCompat.setOnActionExpandListener(
			item,
			new MenuItemCompat.OnActionExpandListener() {
				@Override
				public boolean onMenuItemActionCollapse(MenuItem item) {
					// Do something when collapsed
					contactAdapter.setFilter(contactList);
					return true; // Return true to collapse action view
				}

				@Override
				public boolean onMenuItemActionExpand(MenuItem item) {
					// Do something when expanded
					return true; // Return true to expand action view
				}
			}
		);

		return super.onCreateOptionsMenu(menu);
	}

//	private List<Contact> getAllContactsList() {
//		List<Contact> contactList = new ArrayList<>();
//		Contact contact;
//
//		ContentResolver contentResolver = getContentResolver();
//		if (contentResolver != null) {
//
//			Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//			if (cursor != null) {
//				if (cursor.getCount() > 0) {
//					while (cursor.moveToNext()) {
//
//						int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
//						if (hasPhoneNumber > 0) {
//							String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//							String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//
//							contact = new Contact();
//							contact.setContactName(name);
//
//							Cursor phoneCursor = contentResolver.query(
//									ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//									null,
//									ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//									new String[]{id},
//									null
//							);
//
//							if (phoneCursor != null) {
//								if (phoneCursor.moveToNext()) {
//									String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//									contact.setContactNumber(phoneNumber);
//								}
//								phoneCursor.close();
//								contactList.add(contact);
//							}
//						}
//					}
//				}
//				cursor.close();
//			}
//		}
//		return contactList;
//	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		final List<Contact> filteredModelList = filter(contactList, newText);
		contactAdapter.setFilter(filteredModelList);
		return true;
	}

	private List<Contact> filter(List<Contact> models, String query) {
		query = query.toLowerCase();

		final List<Contact> filteredModelList = new ArrayList<>();
		for (Contact model : models) {
			final String text = model.getContactName().toLowerCase();
			if (text.contains(query)) {
				filteredModelList.add(model);
			}
		}
		return filteredModelList;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(
				this,
				ContactsContract.Contacts.CONTENT_URI,
				PROJECTION,
//				SELECTION,
//				mSelectionArgs,
//				null,
				null,
				null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
		);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		List<Contact> contactList = new ArrayList<>();
		Contact contact;
		ContentResolver contentResolver = getContentResolver();
		if (cursor != null) {
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

						if (phoneCursor != null) {
							if (phoneCursor.moveToNext()) {
								String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								contact.setContactNumber(phoneNumber);
							}
							phoneCursor.close();
							contactList.add(contact);
						}
					}
				}
			}
		}

		this.contactList = contactList;

		ContactsAdapter contactAdapter = new ContactsAdapter(contactList, getApplicationContext()) {
			@Override
			public void callBackFN(){
				showTimerDialog();
			}
		};
		this.contactAdapter = contactAdapter;
		rvContacts.setLayoutManager(new LinearLayoutManager(this));
		rvContacts.setAdapter(contactAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
