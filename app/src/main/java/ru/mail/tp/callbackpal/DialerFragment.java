package ru.mail.tp.callbackpal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Martin on 26.02.2017.
 * martin00@yandex.ru
 */

public class DialerFragment extends Fragment {

	public DialerFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dialer, container, false);



		return rootView;
	}
}
