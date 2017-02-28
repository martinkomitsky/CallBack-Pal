package ru.mail.tp.callbackpal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.pinball83.maskededittext.MaskedEditText;

import java.util.Date;

import ru.mail.tp.callbackpal.contacts.Call;
import ru.mail.tp.callbackpal.db.CallHistoryHelper;
import ru.mail.tp.callbackpal.utils.InformerCreator;
import ru.mail.tp.callbackpal.utils.SharedPreferenceHelper;

/**
 * Created by Martin on 26.02.2017.
 * martin00@yandex.ru
 */

public class DialerFragment extends Fragment {
	private final String LOG_TAG = "DialerFragment";
	private MaskedEditText mPhoneView;

	public DialerFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dialer, container, false);
		mPhoneView = (MaskedEditText) rootView.findViewById(R.id.number_masked);
		mPhoneView.requestFocus();
		View mDialButton = rootView.findViewById(R.id.dial_button);

		mDialButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPhoneView.setError(null);
				String currentPhone = mPhoneView.getText().toString().replaceAll("[^0-9+]", "");
				String phoneA = SharedPreferenceHelper.getValue(getActivity(), SharedPreferenceHelper.SHARED_PREF_VALUE_PHONE);

				if (phoneA != null && phoneA.length() > 0 && currentPhone != null && currentPhone.length() > 11) {
					InformerCreator.showToast(String.format(getActivity().getString(R.string.action_calling_number), currentPhone), getActivity());

					Intent intent = new Intent(getActivity().getApplicationContext(), CallbackIntentService.class)
							.setAction(CallbackIntentService.ACTION_INIT_CALLBACK)
							.putExtra(CallbackIntentService.EXTRA_NUMBER_A, phoneA)
							.putExtra(CallbackIntentService.EXTRA_NUMBER_B, currentPhone);
					getActivity().startService(intent);

					InformerCreator.showTimerDialog(getContext());
					CallHistoryHelper callHistoryHelper = new CallHistoryHelper(getActivity());

					callHistoryHelper.addHistoryRecord(new Call(
							getString(R.string.unknown_contact),
							currentPhone,
							new Date()
					));
				} else {
					mPhoneView.setError(getString(R.string.error_invalid_phone));
					Log.d(LOG_TAG, "One of the numbers is blank");
				}
			}
		});
		return rootView;
	}
}
