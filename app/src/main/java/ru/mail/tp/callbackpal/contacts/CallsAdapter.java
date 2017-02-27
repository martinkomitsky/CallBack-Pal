package ru.mail.tp.callbackpal.contacts;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.mail.tp.callbackpal.R;
import ru.mail.tp.callbackpal.db.CallHistoryHelper;

/**
 * Created by Martin on 26.02.2017.
 * martin00@yandex.ru
 */

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ContactViewHolder>{
	private List<Call> callsList = new ArrayList<>();
	private Cursor dataCursor;
	private final Context mContext;

	public CallsAdapter(Cursor mCursor, Context mContext) {
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
			CallHistoryHelper callHistoryHelper = new CallHistoryHelper(mContext);
			callsList = callHistoryHelper.parseCursor(cursor);
			this.notifyDataSetChanged();
		}
		return oldCursor;
	}

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//		View view = LayoutInflater.from(mContext).inflate(R.layout.view_single_contact, parent, false);
		View view = LayoutInflater.from(mContext).inflate(R.layout.view_single_history, parent, false);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			view.setBackgroundResource(R.drawable.ripple);
		} else {
			view.setBackgroundResource(R.drawable.contact_background);
		}

		return new ContactViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ContactViewHolder holder, final int position) {
		final Call call = callsList.get(position);
		holder.tvContactName.setText(call.getContactName());
		holder.tvPhoneNumber.setText(call.getContactNumber());
		holder.tvDate.setText(call.date.toString());
		holder.tvType.setText(R.string.call_type);
	}

	@Override
	public int getItemCount() {
		return callsList.size();
	}

	static class ContactViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvContactName;
		private final TextView tvPhoneNumber;
		private final TextView tvDate;
		private final TextView tvType;

		ContactViewHolder(View itemView) {
			super(itemView);
			tvContactName = (TextView) itemView.findViewById(R.id.tvNameMain);
			tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvNumberMain);
			tvDate = (TextView) itemView.findViewById(R.id.tvDate);
			tvType = (TextView) itemView.findViewById(R.id.tvType);
		}
	}
}
