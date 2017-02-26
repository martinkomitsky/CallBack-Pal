package ru.mail.tp.callbackpal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Martin on 20.02.2017.
 * martin00@yandex.ru
 */

public class CallHistoryHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ru.mail.tp.callbackpal.database.name.HISTORY_STORAGE";
	private static final String DATABASE_TABLE = "calls";
	private static final int DATABASE_VERSION = 1;

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_PHONENUMBER = "phone_number";
	private static final String KEY_DATE = "unixtime";

	private static final String SELECT_QUERY = "SELECT * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_DATE + " DESC";

	private static final String CREATE_TABLE_CALLS = "CREATE TABLE "
			+ DATABASE_TABLE + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
			+ KEY_PHONENUMBER + " TEXT, " + KEY_DATE + " INTEGER);";

	private static final String LOG_TAG = "[HistoryDBHelper]";

	public CallHistoryHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "Database Schema creation");

		db.execSQL(CREATE_TABLE_CALLS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion != oldVersion) {
			Log.d(LOG_TAG, "onUpgrade");

			db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CALLS);
			onCreate(db);
		}
	}

	public long addHistoryRecord(ru.mail.tp.callbackpal.contacts.Call callModel) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PHONENUMBER, callModel.ContactNumber);
		values.put(KEY_NAME, callModel.ContactName);
		values.put(KEY_DATE, callModel.date.getTime());

		final long insertedRowId = db.insert(DATABASE_TABLE, null, values);

		Log.d(LOG_TAG, String.format("Row %d inserted (phone %s at %s)", insertedRowId, callModel.ContactNumber, callModel.date.toString()));
		db.close();

		return insertedRowId;
	}

	private ArrayList<ru.mail.tp.callbackpal.contacts.Call> getAllRecords(long limit, long offset) {
		Log.d(LOG_TAG, String.format("Select rows from %d with LIMIT %d", offset, limit));
		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.query(DATABASE_TABLE, null, null, null, null, null, "unixtime DESC", String.format(Locale.US, "%d,%d", offset, limit));
		Cursor cursor = db.rawQuery(SELECT_QUERY, null);
		ArrayList<ru.mail.tp.callbackpal.contacts.Call> callsHistoryArrayList = parseCursor(cursor);
		db.close();
		return callsHistoryArrayList;
	}

	public ArrayList<ru.mail.tp.callbackpal.contacts.Call> parseCursor(Cursor cursor) {
		final ArrayList<ru.mail.tp.callbackpal.contacts.Call> callsHistoryArrayList = new ArrayList<>(cursor.getCount());

		if (cursor.moveToFirst()) {
			do {
				ru.mail.tp.callbackpal.contacts.Call callModel = new ru.mail.tp.callbackpal.contacts.Call();
				callModel.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
				callModel.ContactNumber = cursor.getString(cursor.getColumnIndex(KEY_PHONENUMBER));
				callModel.ContactName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
				long unix_time = cursor.getLong(cursor.getColumnIndex(KEY_DATE));
				Date date = new Date(unix_time);
				callModel.date = date;
				Log.d(LOG_TAG, date.toString());

				callsHistoryArrayList.add(callModel);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return callsHistoryArrayList;
	}

	public ArrayList<ru.mail.tp.callbackpal.contacts.Call> getAllRecords(long limit) {
		return getAllRecords(limit, 0);
	}

	public ArrayList<ru.mail.tp.callbackpal.contacts.Call> getAllRecords() {
		return getAllRecords(0, 0);
	}

	public Cursor getCursor() {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery(SELECT_QUERY, null);
	}
}
