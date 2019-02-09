////////////////////////////////////////////////////////////////////////////////////////////////////
// ReportDatabaseHelper pour les traces et historiques
// Stockage des traces dans une base separee pour ne pas interferer avec le stockage de l'application
////////////////////////////////////////////////////////////////////////////////////////////////////
package com.lpi.itineraires.report;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

public class ReportDatabaseHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "debug.db";
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table historique
	public static final String TABLE_HISTORIQUE = "HISTORIQUE";
	public static final String COLONNE_HISTORIQUE_DATE = "DATE";
	public static final String COLONNE_HISTORIQUE_LIGNE = "LIGNE";
	public static final String DATABASE_HISTORIQUE_CREATE = "create table "
			+ TABLE_HISTORIQUE + "("
			+ com.lpi.itineraires.database.DatabaseHelper.COLONNE_ITI_ID + " integer primary key autoincrement, "
			+ COLONNE_HISTORIQUE_DATE + " integer,"
			+ COLONNE_HISTORIQUE_LIGNE + " text not null"
			+ ");";
	public static final String COLONNE_HISTORIQUE_ID = "_id";
	public static final String TABLE_TRACES = "TRACES";
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table traces
	public static final String COLONNE_TRACES_ID = "_id";
	public static final String COLONNE_TRACES_DATE = "DATE";
	public static final String COLONNE_TRACES_NIVEAU = "NIVEAU";
	public static final String COLONNE_TRACES_LIGNE = "LIGNE";
	public static final String DATABASE_TRACES_CREATE = "create table "
			+ TABLE_TRACES + "("
			+ COLONNE_TRACES_ID + " integer primary key autoincrement, "
			+ COLONNE_TRACES_DATE + " integer,"
			+ COLONNE_TRACES_NIVEAU + " integer,"
			+ COLONNE_TRACES_LIGNE + " text not null"
			+ ");";

	public ReportDatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(@NonNull SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_HISTORIQUE_CREATE);
			database.execSQL(DATABASE_TRACES_CREATE);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			Log.w(this.getClass().getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIQUE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACES);

			onCreate(db);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
