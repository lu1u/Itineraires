package com.lpi.itineraires.report;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;


public class ReportDatabase
{

	@Nullable
	protected static ReportDatabase INSTANCE = null;
	protected SQLiteDatabase database;
	protected ReportDatabaseHelper dbHelper;
	protected String _tableName;

	protected ReportDatabase(String table, Context context)
	{
		_tableName = table ;

		dbHelper = new ReportDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	@Override
	public void finalize()
	{
		try
		{
			super.finalize();
		} catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
		dbHelper.close();
	}
	/***
	 * Retrouve le nombre de lignes d'une table
	 * @return nombre de lignes
	 */
	protected int getNbLignes()
	{
		Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " + _tableName, null);
		int count = 0;
		try
		{
			if (null != cursor)
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					count = cursor.getInt(0);
					cursor.close();
				}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return count;
	}

	public void Vide()
	{
		database.delete(_tableName, null, null);
	}
}
