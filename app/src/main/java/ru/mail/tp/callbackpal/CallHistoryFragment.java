package ru.mail.tp.callbackpal;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mail.tp.callbackpal.contacts.CallsAdapter;
import ru.mail.tp.callbackpal.db.CallHistoryHelper;

/**
 * Created by Martin on 25.02.2017.
 * martin00@yandex.ru
 */

public class CallHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private CallsAdapter callsAdapter;
	private CallHistoryHelper callHistoryHelper;
	public CallHistoryFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_call_history, container, false);
		callHistoryHelper = new CallHistoryHelper(getContext());
		Context mContext = getContext();

		getLoaderManager().initLoader(0, null, this);
		RecyclerView rvCalls = (RecyclerView) rootView.findViewById(R.id.rvCalls);

		callsAdapter = new CallsAdapter(null, mContext);
		rvCalls.setLayoutManager(new LinearLayoutManager(mContext));
		rvCalls.setAdapter(callsAdapter);

		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(getContext(), null, null, null, null, null) {
			@Override
			public Cursor loadInBackground() {
				return callHistoryHelper.getCursor();
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		callsAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		callsAdapter.swapCursor(null);
	}
}
