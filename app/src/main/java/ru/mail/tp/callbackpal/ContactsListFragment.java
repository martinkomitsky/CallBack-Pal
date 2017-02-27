package ru.mail.tp.callbackpal;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.mail.tp.callbackpal.contacts.ContactsAdapter;
import ru.mail.tp.callbackpal.networkState.NetworkChangeReceiver;
import ru.mail.tp.callbackpal.networkState.NetworkStateChangeListener;
import ru.mail.tp.callbackpal.utils.InformerCreator;

/**
 * Created by Martin on 25.02.2017.
 * martin00@yandex.ru
 */

public class ContactsListFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
	private ContactsAdapter contactAdapter;
	private BroadcastReceiver networkChangedBroadcastReceiver;
	private final String LOG_TAG = "ContactsListFragment";

	private boolean networkState = false;

	private static final String[] PROJECTION = {
			ContactsContract.Data._ID,
			ContactsContract.Data.HAS_PHONE_NUMBER,
			ContactsContract.Data.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.NUMBER
	};

	private Context mContext;

	public ContactsListFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contacts_list, container, false);
		Log.d(LOG_TAG, "onCreateView");

		mContext = getContext();

		getLoaderManager().initLoader(0, null, this);
		RecyclerView rvContacts = (RecyclerView) rootView.findViewById(R.id.rvContacts);

		View fab = rootView.findViewById(R.id.FAB);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
				startActivity(intent);
			}
		});

		contactAdapter = new ContactsAdapter(null, mContext) {
			@Override
			public void callBackFN(){
				showTimerDialog();
			}

			@Override
			public boolean getNetworkState() {
				return networkState;
			}
		};
		rvContacts.setLayoutManager(new LinearLayoutManager(mContext));
		rvContacts.setAdapter(contactAdapter);

		networkChangedBroadcastReceiver = new NetworkChangeReceiver(new CallbackRunner());
		IntentFilter networkChangedFilter = new IntentFilter(NetworkChangeReceiver.ACTION_CONN_CHANGE);
		networkChangedFilter.addAction(NetworkChangeReceiver.ACTION_WIFI_CHANGE);
		networkChangedFilter.addCategory(Intent.CATEGORY_DEFAULT);
		mContext.registerReceiver(networkChangedBroadcastReceiver, networkChangedFilter);

		return rootView;
	}

	private void showTimerDialog() {
		final Dialog dialog = new Dialog(getContext());
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
						contactAdapter.setFilter(null);
						return true; // Return true to collapse action view
					}

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						// Do something when expanded
						return true; // Return true to expand action view
					}
				}
		);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		Log.d(LOG_TAG, "onCreateLoader");
		return new CursorLoader(
				getActivity(),
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
		contactAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		contactAdapter.swapCursor(null);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		contactAdapter.setFilter(newText);
		return true;
	}

	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(networkChangedBroadcastReceiver);
		super.onDestroy();
	}

	public class CallbackRunner implements NetworkStateChangeListener {
		@Override
		public void onNetworkStateChange(String message, boolean state) {
			networkState = state;
			InformerCreator.showSnack(message, state, getActivity().findViewById(R.id.FAB));
		}
	}
}
