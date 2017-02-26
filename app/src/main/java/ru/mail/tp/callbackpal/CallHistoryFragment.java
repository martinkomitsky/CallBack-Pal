package ru.mail.tp.callbackpal;

/**
 * Created by Martin on 25.02.2017.
 * martin00@yandex.ru
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CallHistoryFragment extends Fragment {
	public CallHistoryFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_content, container, false);
		return rootView;
	}

}
