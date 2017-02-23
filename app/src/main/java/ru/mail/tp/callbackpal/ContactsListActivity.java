package ru.mail.tp.callbackpal;

import android.app.LoaderManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ru.mail.tp.callbackpal.contacts.Contact;
import ru.mail.tp.callbackpal.contacts.ContactsAdapter;
import ru.mail.tp.callbackpal.networkState.NetworkChangeReceiver;
import ru.mail.tp.callbackpal.networkState.NetworkStateChangeListener;
import ru.mail.tp.callbackpal.utils.InformerCreator;

import android.view.View;
import android.widget.TextView;

public class ContactsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
	private ContactsAdapter contactAdapter;
	private BroadcastReceiver networkChangedBroadcastReceiver;
	private final List<Contact> contactList = new ArrayList<>();
	private final String ACTION_CONN_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	private final String ACTION_WIFI_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";
	private final String LOG_TAG = "ContactListActivity";

	private boolean networkState = false;

	private static final String[] PROJECTION = {
			ContactsContract.Data._ID,
			ContactsContract.Data.HAS_PHONE_NUMBER,
			ContactsContract.Data.DISPLAY_NAME
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);
		Log.d(LOG_TAG, "onCreate");

		getLoaderManager().initLoader(0, null, this);
		RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);

		View fab = findViewById(R.id.FAB);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
				startActivity(intent);
			}
		});
		contactAdapter = new ContactsAdapter(contactList, getApplicationContext()) {
			@Override
			public void callBackFN(){
				showTimerDialog();
			}

			@Override
			public boolean getNetworkState() {
				return networkState;
			}
		};
		rvContacts.setLayoutManager(new LinearLayoutManager(this));
		rvContacts.setAdapter(contactAdapter);

		networkChangedBroadcastReceiver = new NetworkChangeReceiver(new CallbackRunner());
		IntentFilter networkChangedFilter = new IntentFilter(ACTION_CONN_CHANGE);
		networkChangedFilter.addAction(ACTION_WIFI_CHANGE);
		networkChangedFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(networkChangedBroadcastReceiver, networkChangedFilter);
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
				int count =  (int) millisUntilFinished / 1000;
				mDialogText.setText(getResources().getQuantityString(R.plurals.info_next_call_plural, count, count));
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
		Log.d(LOG_TAG, "onCreateLoader");
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
							contactAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(networkChangedBroadcastReceiver);
		super.onDestroy();
	}

	public class CallbackRunner implements NetworkStateChangeListener {
		@Override
		public void onNetworkStateChange(String message, boolean state) {
			networkState = state;
			InformerCreator.showSnack(message, state, findViewById(R.id.rvContacts));
		}
	}
}
