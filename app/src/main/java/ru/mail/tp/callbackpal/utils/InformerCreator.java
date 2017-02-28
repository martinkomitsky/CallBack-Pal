package ru.mail.tp.callbackpal.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.mail.tp.callbackpal.R;

/**
 * Created by Martin on 23.02.2017.
 * martin00@yandex.ru
 */

public class InformerCreator {
	private static final int DURATION = 15000;
	private static final int TICK = 1000;

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

	public static void showTimerDialog(final Context mContext) {
		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.popup_window);
		dialog.setTitle(R.string.info_dial);

		final TextView mDialogText = (TextView) dialog.findViewById(R.id.text);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		new CountDownTimer(DURATION, TICK) {

			public void onTick(long millisUntilFinished) {
				int count =  (int) millisUntilFinished / 1000;
				mDialogText.setText(mContext.getResources().getQuantityString(R.plurals.info_next_call_plural, count, count));
			}

			public void onFinish() {
				mDialogText.setText("");
				dialog.dismiss();
			}
		}.start();
	}
}
