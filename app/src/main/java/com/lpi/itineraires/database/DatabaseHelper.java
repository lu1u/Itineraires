package com.lpi.itineraires.database;

/*
 * Utilitaire de gestion de la base de donnees
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 5;
	public static final String DATABASE_NAME = "itineraires.db";
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table des randonnees
	public static final String TABLE_ITINERAIRE = "RANDOS";
	public static final String COLONNE_ITI_ID = "_id";
	public static final String COLONNE_ITI_NOM = "NOM";
	public static final String COLONNE_ITI_CREATION = "CREATION";
	public static final String COLONNE_ITI_DEBUT = "DEBUT";
	public static final String COLONNE_ITI_FIN = "FIN";
	public static final String COLONNE_ITI_ENREGISTRE = "ENREGISTRE";
	public static final String COLONNE_ITI_TYPE = "TYPE";

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table des positions
	public static final String TABLE_POSITIONS = "POSITIONS" ;
	public static final String COLONNE_POS_IDRANDO = "IDRANDO" ;
	public static final String COLONNE_POS_TEMPS = "TEMPS" ;
	public static final String COLONNE_POS_LATITUDE = "LATITUDE" ;
	public static final String COLONNE_POS_LONGITUDE = "LONGITUDE";
	public static final String COLONNE_POS_ALTITUDE = "ALTITUDE" ;
	public static final String COLONNE_POS_VITESSE = "VITESSE";
	public static final String COLONNE_POS_ACCURACY = "ACCURACY";
	public static final String COLONNE_POS_BEARING= "BEARING";
	public static final String COLONNE_POS_PROVIDER= "PROVIDER";

	// Table preferences bool et int
	public static final String TABLE_PREFERENCES_INT = "PREFERENCES_INT";
	public static final String COLONNE_PREF_INT_NAME = "NAME";
	public static final String COLONNE_PREF_INT_VALEUR = "VALEUR";
	// Table preferences string
	public static final String TABLE_PREFERENCES_STRING = "PREFERENCES_STRING";
	public static final String COLONNE_PREF_STRING_NAME = "NAME";
	public static final String COLONNE_PREF_STRING_VALEUR = "VALEUR";
	private static final String DATABASE_RANDOS_CREATE = "create table "
			+ TABLE_ITINERAIRE + "("
			+ COLONNE_ITI_ID + " integer primary key autoincrement, "
			+ COLONNE_ITI_NOM + " text not null,"
			+ COLONNE_ITI_CREATION + " integer, "
			+ COLONNE_ITI_DEBUT + " integer, "
			+ COLONNE_ITI_FIN + " integer, "
			+ COLONNE_ITI_ENREGISTRE + " integer, "
			+ COLONNE_ITI_TYPE + " integer "
			+ ");";

	private static final String DATABASE_POSITIONS_CREATE = "create table "
			+ TABLE_POSITIONS + "("
			+ COLONNE_POS_IDRANDO + " integer, "
			+ COLONNE_POS_TEMPS+ " integer,"
			+ COLONNE_POS_LATITUDE + " real, "
			+ COLONNE_POS_LONGITUDE + " real, "
			+ COLONNE_POS_ALTITUDE + " real, "
			+ COLONNE_POS_VITESSE + " real, "
			+ COLONNE_POS_ACCURACY + " real, "
			+ COLONNE_POS_BEARING + " real, "
			+ COLONNE_POS_PROVIDER + " text not null "
			+ ");";
	private static final String DATABASE_PREF_INT_CREATE = "create table "
			+ TABLE_PREFERENCES_INT + "("
			+ COLONNE_PREF_INT_NAME + " text primary key not null, "
			+ COLONNE_PREF_INT_VALEUR + " integer "
			+ ");";
	private static final String DATABASE_PREF_STRING_CREATE = "create table "
			+ TABLE_PREFERENCES_STRING + "("
			+ COLONNE_PREF_INT_NAME + " text primary key not null, "
			+ COLONNE_PREF_INT_VALEUR + " text "
			+ ");";

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	static public int CalendarToSQLiteDate(@Nullable Calendar cal)
	{
		if (cal == null)
			cal = Calendar.getInstance();
		return (int) (cal.getTimeInMillis()/1000L );
	}

	@NonNull
	static public Calendar SQLiteDateToCalendar(int date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis((long) date * 1000L);
		return cal;
	}

	@NonNull
	public static String getStringFromAnyColumn(@NonNull Cursor cursor, int colonne)
	{
		Object o = getObjectFromAnyColumn(cursor, colonne);
		if (o != null)
			return o.toString();
		else
			return "Impossible de lire la colonne " + cursor.getColumnName(colonne);
	}

	@Nullable
	public static Object getObjectFromAnyColumn(@NonNull Cursor cursor, int colonne)
	{
		try
		{
			return cursor.getInt(colonne);
		} catch (Exception e)
		{
			try
			{
				return cursor.getShort(colonne);
			} catch (Exception e1)
			{
				try
				{
					return cursor.getLong(colonne);
				} catch (Exception e2)
				{
					try
					{
						return cursor.getDouble(colonne);
					} catch (Exception e3)
					{
						try
						{
							return cursor.getFloat(colonne);
						} catch (Exception e4)
						{
							try
							{
								return cursor.getString(colonne);
							} catch (Exception e5)
							{
								Log.e("Dabase", "impossible de lire la colonne " + colonne);
							}
						}
					}
				}
			}
		}

		return null;
	}

	public static String getTexteDateSecondes(long dateCreationEnSecondes)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateCreationEnSecondes);

		return new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(cal.getTime());
	}

	@Override
	public void onCreate(@NonNull SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_RANDOS_CREATE);
			database.execSQL(DATABASE_POSITIONS_CREATE);
			database.execSQL(DATABASE_PREF_INT_CREATE);
			database.execSQL(DATABASE_PREF_STRING_CREATE);
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
			Log.w(DatabaseHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERAIRE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITIONS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES_INT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES_STRING);

			onCreate(db);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


}
