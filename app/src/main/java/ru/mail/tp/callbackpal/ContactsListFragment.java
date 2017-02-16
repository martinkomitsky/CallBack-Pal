package ru.mail.tp.callbackpal;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.mail.tp.callbackpal.contacts.Contact;
import ru.mail.tp.callbackpal.contacts.ContactsAdapter;

public class ContactsListFragment extends Fragment implements SearchView.OnQueryTextListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Object> {

	private RecyclerView rvContacts;
	private ContactsAdapter contactAdapter;
	private List<Contact> contactList;


	private static final String[] PROJECTION = {
			ContactsContract.Data._ID,
			ContactsContract.Data.HAS_PHONE_NUMBER,
			ContactsContract.Data.DISPLAY_NAME
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_contacts_list, container, false);
//
//		recyclerview = (RecyclerView) view.findViewById(R.id.rvContacts);
//		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//		recyclerview.setLayoutManager(layoutManager);


//		List<Contact> contactList = getAllContactsList();
		rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);


		return view;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

//		setHasOptionsMenu(true);
//		String[] locales = Locale.getISOCountries();
//		mCountryModel = new ArrayList<>();


//		adapter = new ContactsAdapter(mCountryModel);
//		recyclerview.setAdapter(adapter);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.contacts_menu, menu);

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

//		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		final List<Contact> filteredModelList = filter(contactList, newText);
		contactAdapter.setFilter(filteredModelList);
		return true;
	}


	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
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

//	@Override
//	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//		return new CursorLoader(
//				getActivity(),
//				ContactsContract.Contacts.CONTENT_URI,
//				PROJECTION,
////				SELECTION,
////				mSelectionArgs,
////				null,
//				null,
//				null,
//				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
//		);
//	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}
//
//	@Override
//	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//		return null;
//	}
//
//	@Override
//	public void onLoadFinished(android.support.v4.content.Loader<Object> loader, Object data) {
//
//	}
//
//	@Override
//	public void onLoaderReset(android.support.v4.content.Loader<Object> loader) {
//
//	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(getActivity(),
				ContactsContract.Contacts.CONTENT_URI,
				PROJECTION,
				null,
				null,
				null);
		return loader;
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Object> loader, Object data) {

	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Object> loader) {

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//		contactAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
//		adapter.swapCursor(null);
	}

//
//	@Override
//	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//		List<Contact> contactList = new ArrayList<>();
//		Contact contact;
//		ContentResolver contentResolver = getActivity().getContentResolver();
//		if (cursor != null) {
//			if (cursor.getCount() > 0) {
//				while (cursor.moveToNext()) {
//
//					int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
//					if (hasPhoneNumber > 0) {
//						String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//						String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//
//						contact = new Contact();
//						contact.setContactName(name);
//
//						Cursor phoneCursor = contentResolver.query(
//								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//								null,
//								ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//								new String[]{id},
//								null
//						);
//
//						if (phoneCursor != null) {
//							if (phoneCursor.moveToNext()) {
//								String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//								contact.setContactNumber(phoneNumber);
//							}
//							phoneCursor.close();
//							contactList.add(contact);
//						}
//					}
//				}
//			}
//		}
//
//		this.contactList = contactList;
//
//		ContactsAdapter contactAdapter = new ContactsAdapter(contactList, getActivity().getApplicationContext());
//		this.contactAdapter = contactAdapter;
//		rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
//		rvContacts.setAdapter(contactAdapter);
//	}
//
//	@Override
//	public void onLoaderReset(Loader<Cursor> loader) {
//
//	}
}