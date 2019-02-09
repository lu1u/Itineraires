package com.lpi.itineraires.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lpi.itineraires.database.DatabaseHelper;


/**
 * Gestionnaire des preferences de l'application
 */
public class Preferences
{
	private static final String PREF_THEME = "Theme";
	private static final String PREF_GPS_MIN_TIME = "GPS Min Time";
	private static final String PREF_GPS_MIN_DISTANCE = "GPS Min Distance";
	private static final String PREF_LOCALISATION_GPS = "Localisation GPS";
	private static final String PREF_LOCALISATION_RESEAU = "Localisation Reseau";
	private static final String PREF_NIVEAU_ZOOM = "Niveau Zoom";
	private static final String PREF_AFFICHAGE_DETAILS = "Affichage details";

	@Nullable
	private static Preferences INSTANCE = null;
	private final SQLiteDatabase database;
	private Report _report;

	private Preferences(Context context)
	{
		_report = Report.getInstance(context);
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized Preferences getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Preferences(context);
		}
		return INSTANCE;
	}


	public void putString(String name, String s)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLONNE_PREF_STRING_NAME, name);
		values.put(DatabaseHelper.COLONNE_PREF_STRING_VALEUR, s);

		database.beginTransaction();
		boolean present = trouveId(DatabaseHelper.TABLE_PREFERENCES_STRING, DatabaseHelper.COLONNE_PREF_STRING_NAME, name);
		try
		{
			if (present)
				database.update(DatabaseHelper.TABLE_PREFERENCES_STRING, values, DatabaseHelper.COLONNE_PREF_STRING_NAME + "=?", new String[]{name});
			else
				database.insert(DatabaseHelper.TABLE_PREFERENCES_STRING, null, values);
			database.setTransactionSuccessful();
		} catch (Exception e)
		{
			Log.e("SAMBA", e.getMessage());
		} finally
		{
			database.endTransaction();
		}
	}

	private boolean trouveId(String tableName, String colonneID, String name)
	{
		Cursor c = database.query(tableName, new String[]{colonneID}, colonneID + " =?", new String[]{name}, null, null, null, null);
		boolean result = false;

		if (c != null)
		{
			if (c.moveToFirst()) //if the row exist then return the id
				result = true;
			c.close();
		}
		return result;
	}

	public String getString(String name, String defaut)
	{
		String result = defaut;
		try
		{
			String where = DatabaseHelper.COLONNE_PREF_STRING_NAME + " = \"" + name + "\"";
			Cursor cursor = database.query(DatabaseHelper.TABLE_PREFERENCES_STRING, null, where, null, null, null, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
					result = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLONNE_PREF_STRING_VALEUR));
				cursor.close();
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		_report.log(Report.NIVEAU.DEBUG, "Preferences: " + name + "=" + result);

		return result;
	}

	public void putInt(String name, int i)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLONNE_PREF_INT_NAME, name);
		values.put(DatabaseHelper.COLONNE_PREF_INT_VALEUR, i);

		database.beginTransaction();
		boolean present = trouveId(DatabaseHelper.TABLE_PREFERENCES_INT, DatabaseHelper.COLONNE_PREF_INT_NAME, name);
		try
		{
			if (present)
				database.update(DatabaseHelper.TABLE_PREFERENCES_INT, values, DatabaseHelper.COLONNE_PREF_INT_NAME + "=?", new String[]{name});
			else
				database.insert(DatabaseHelper.TABLE_PREFERENCES_INT, null, values);
			database.setTransactionSuccessful();
		} catch (Exception e)
		{
			Log.e("SAMBA", e.getMessage());
		} finally
		{
			database.endTransaction();
		}
	}

	public int getInt(String name, int defaut)
	{
		int result = defaut;
		try
		{
			String where = DatabaseHelper.COLONNE_PREF_INT_NAME + " = \"" + name + "\"";
			Cursor cursor = database.query(DatabaseHelper.TABLE_PREFERENCES_INT, null, where, null, null, null, null);
			if (cursor != null)
			{
				if (cursor.moveToFirst())
					result = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLONNE_PREF_INT_VALEUR));
				cursor.close();
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		_report.log(Report.NIVEAU.DEBUG, "Preferences: " + name + "=" + result);
		return result;
	}

	public void putBool(String name, boolean b)
	{
		putInt(name, b ? 1 : 0);
	}

	public boolean getBool(String name, boolean defaut)
	{
		int res = getInt(name, defaut ? 1 : 0);
		return (res != 0);
	}

	public float getFloat(String name, float defaut)
	{
		try
		{
			String s = getString(name, Float.toString(defaut));
			return Float.parseFloat(s);
		} catch (Exception e)
		{
			return defaut;
		}
	}

	public int getTheme()
	{
		return getInt(PREF_THEME, 0);
	}

	public void setTheme(int p)
	{
		putInt(PREF_THEME, p);
	}

	public int getGPSMinTime()
	{
		return getInt(PREF_GPS_MIN_TIME, 2);
	}

	public void setGPSMinTime(int v) { putInt(PREF_GPS_MIN_TIME, v);}

	public int getGPSMinDistance()
	{
		return getInt(PREF_GPS_MIN_DISTANCE, 1);
	}

	public void setGPSMinDistance(int v) { putInt(PREF_GPS_MIN_DISTANCE, v);}

	public boolean getLocalisationGPS() {return getBool(PREF_LOCALISATION_GPS, true);}

	public void setLocalisationGPS(boolean v) { putBool(PREF_LOCALISATION_GPS, v);}

	public boolean getLocalisationReseau() {return getBool(PREF_LOCALISATION_RESEAU, true);}

	public void setLocalisationReseau(boolean v) { putBool(PREF_LOCALISATION_RESEAU, v);}

	public float getNiveauZoom()
	{
		return getFloat(PREF_NIVEAU_ZOOM, 12.0f);
	}

	public void setAffichageDetails(final int value){putInt(PREF_AFFICHAGE_DETAILS, value);	}
	public int getAffichageDetails(){return getInt(PREF_AFFICHAGE_DETAILS, 0);	}
}
