package com.lpi.itineraires.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lpi.itineraires.utils.Report;

public class GenericDatabase
{
	public static final int INVALID_ID = -1;
	/**
	 * Instance unique non préinitialisée
	 */
	@Nullable
	protected static GenericDatabase INSTANCE = null;
	protected SQLiteDatabase _database;
	protected DatabaseHelper _dbHelper;
	protected Report _report;


	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@Nullable
	public static synchronized GenericDatabase getInstance(Context context)
	{
		return  INSTANCE;
	}

	public @NonNull
	Cursor getTables()
	{
		return _database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
	}

	public void beginTransaction()
	{
		_database.beginTransaction();
	}

	public void endTransaction()
	{
		_database.endTransaction();
	}

	public void setTransactionSuccessful()
	{
		_database.setTransactionSuccessful();
	}

	@Nullable
	public Cursor getCursor(String nomTable)
	{
		try
		{
			return _database.query(nomTable, null, null, null, null, null, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
