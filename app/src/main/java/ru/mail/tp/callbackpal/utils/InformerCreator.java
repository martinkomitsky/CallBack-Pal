package ru.mail.tp.callbackpal.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Martin on 23.02.2017.
 * martin00@yandex.ru
 */

public class InformerCreator {
	public static void showSnack(String message, boolean isConnected, View mView) {
		int color;
		if (isConnected) {
			color = Color.WHITE;
		} else {
			color = Color.RED;
		}

		Snackbar snackbar = Snackbar
				.make(mView, message, isConnected ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_INDEFINITE);

		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(color);
		snackbar.show();
	}

	public static void showToast(String text, Context mContext) {
		Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
	}
}
