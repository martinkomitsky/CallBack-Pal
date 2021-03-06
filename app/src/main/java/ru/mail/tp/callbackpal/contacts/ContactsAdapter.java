package ru.mail.tp.callbackpal.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mail.tp.callbackpal.CallbackIntentService;
import ru.mail.tp.callbackpal.R;
import ru.mail.tp.callbackpal.db.CallHistoryHelper;
import ru.mail.tp.callbackpal.utils.InformerCreator;
import ru.mail.tp.callbackpal.utils.SharedPreferenceHelper;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>{
	private List<Contact> contactList = new ArrayList<>();
	private List<Contact> __contactListCached;
	private Cursor dataCursor;
	private final Context mContext;
	private static final String LOG_TAG = "[ContactsAdapter]";

	public boolean getNetworkState(){ return false;}

	public ContactsAdapter(Cursor mCursor, Context mContext) {
		this.dataCursor = mCursor;
		this.mContext = mContext;
	}

	public Cursor swapCursor(Cursor cursor) {
		if (dataCursor == cursor) {
			return null;
		}
		Cursor oldCursor = dataCursor;
		dataCursor = cursor;

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
					if (hasPhoneNumber > 0) {
						contactList.add(new Contact(
								cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
								cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
						));
						__contactListCached = contactList;
						this.notifyDataSetChanged();
					}
				}
			}
		}
		return oldCursor;
	}

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.view_single_contact, parent, false);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			view.setBackgroundResource(R.drawable.ripple);
		} else {
			view.setBackgroundResource(R.drawable.contact_background);
		}

		return new ContactViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ContactViewHolder holder, final int position) {
		final Contact contact = contactList.get(position);
		holder.tvContactName.setText(contact.getContactName());
		holder.tvPhoneNumber.setText(contact.getContactNumber());

		holder.itemView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				String currentPhone = holder.tvPhoneNumber.getText().toString().replaceAll("[^0-9+]", "");
				String currentName = holder.tvContactName.getText().toString();
				String phoneA = SharedPreferenceHelper.getValue(mContext, SharedPreferenceHelper.SHARED_PREF_VALUE_PHONE);

				if (phoneA != null && phoneA.length() > 0 && currentPhone != null && currentPhone.length() > 0 && getNetworkState()) {

					InformerCreator.showToast(String.format(mContext.getString(R.string.action_calling_number), currentPhone), mContext);

					Intent intent = new Intent(mContext.getApplicationContext(), CallbackIntentService.class)
							.setAction(CallbackIntentService.ACTION_INIT_CALLBACK)
							.putExtra(CallbackIntentService.EXTRA_NUMBER_A, phoneA)
							.putExtra(CallbackIntentService.EXTRA_NUMBER_B, currentPhone);
					mContext.startService(intent);

					CallHistoryHelper callHistoryHelper = new CallHistoryHelper(mContext);

					callHistoryHelper.addHistoryRecord(new Call(
						currentName,
						currentPhone,
						new Date()
					));

					InformerCreator.showTimerDialog(mContext);
				} else {
					InformerCreator.showToast(mContext.getString(R.string.unknown_error), mContext);
					Log.d(LOG_TAG, "One of the numbers is blank");
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return contactList.size();
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

	public void setFilter(String searchQuery) {
		if (searchQuery != null) {
			contactList = filter(__contactListCached, searchQuery);
		} else {
			contactList = __contactListCached;
		}
		notifyDataSetChanged();
	}

	static class ContactViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvContactName;
		private final TextView tvPhoneNumber;

		ContactViewHolder(View itemView) {
			super(itemView);
			tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
			tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
		}
	}
}
