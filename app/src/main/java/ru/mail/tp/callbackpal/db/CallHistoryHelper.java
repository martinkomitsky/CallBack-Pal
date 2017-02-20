package ru.mail.tp.callbackpal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Martin on 20.02.2017.
 */

public class CallHistoryHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ru.mail.tp.callbackpal.database.name.HISTORY_STORAGE";
	private static final String DATABASE_TABLENAME = "storage";
	private static final int DATABASE_VERSION = 1;

	private static final String LOG_TAG = "[HistoryDBHelper]";

	public CallHistoryHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "Database Schema creation");

		db.execSQL(String.format("create table %s (id integer primary key autoincrement, phone text, unixtime integer);", DATABASE_TABLENAME));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion != oldVersion) {
			Log.d(LOG_TAG, "Database Schema update");

			db.execSQL(String.format("drop table if exists %s;", DATABASE_TABLENAME));
			onCreate(db);
		}
	}

	public void putRecord(String phone, Date date) {
		final long unixtime = date.getTime();

		ContentValues cv = new ContentValues();

		cv.put("phone", phone);
		cv.put("unixtime", unixtime);

		SQLiteDatabase db = this.getWritableDatabase();

		final long insertedRowId = db.insert(DATABASE_TABLENAME, null, cv);

		Log.d(LOG_TAG, String.format("Row %d inserted (phone %s at %s)", insertedRowId, phone, date.toString()));

		db.close();
	}

	public ArrayList<ContentValues> getAllRecords(long limit, long offset) {
		Log.d(LOG_TAG, String.format("Extract rows from %d with LIMIT %d", offset, limit));

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(DATABASE_TABLENAME, null, null, null, null, null, "unixtime DESC", String.format(Locale.US, "%d,%d", offset, limit));

		final ArrayList<ContentValues> result = new ArrayList<>(cursor.getCount());

		if (cursor.moveToFirst()) {
			final int phoneColumnIndex = cursor.getColumnIndex("phone");
			final int unixtimeColuntIndex = cursor.getColumnIndex("unixtime");

			do {
				ContentValues cv = new ContentValues();

				cv.put("phone", cursor.getString(phoneColumnIndex));
				cv.put("unixtime", cursor.getLong(unixtimeColuntIndex));
				result.add(cv);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();

		return result;
	}

	public ArrayList<ContentValues> getAllRecords(long limit) {
		return getAllRecords(limit, 0);
	}

	public ArrayList<ContentValues> getAllRecords() {
		return getAllRecords(0, 0);
	}
}