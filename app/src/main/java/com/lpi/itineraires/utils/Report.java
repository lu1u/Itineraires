/**
 *
 */
package com.lpi.itineraires.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lpi.itineraires.database.DatabaseHelper;
import com.lpi.itineraires.report.HistoriqueDatabase;
import com.lpi.itineraires.report.TracesDatabase;

import java.util.Calendar;
import java.util.Locale;


/**
 * @author lucien
 */
@SuppressWarnings("nls")
public class Report
{
	// Niveaux de trace
	public enum NIVEAU
	{
		DEBUG,
		WARNING,
		ERROR
	}

	private static final int MAX_BACKTRACE = 10;
	@Nullable
	private static Report INSTANCE = null;

	final HistoriqueDatabase _historiqueDatabase;
	final TracesDatabase _tracesDatabase;


	private Report(Context context)
	{
		_historiqueDatabase = HistoriqueDatabase.getInstance(context);
		_tracesDatabase = TracesDatabase.getInstance(context);

	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 *
	 * @param context: le context habituel d'ANdroid, peut être null si l'objet a deja ete utilise
	 */
	@NonNull
	public static synchronized Report getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Report(context);
		}
		return INSTANCE;
	}

	public static int toInt(NIVEAU n)
	{
		switch (n)
		{
			case DEBUG:
				return 0;
			case WARNING:
				return 1;
			case ERROR:
				return 2;
			default:
				return 0;
		}
	}

	@NonNull
	public static NIVEAU toNIVEAU(int n)
	{
		switch (n)
		{
			case 0:
				return NIVEAU.DEBUG;
			case 1:
				return NIVEAU.WARNING;
			case 2:
				return NIVEAU.ERROR;
			default:
				return NIVEAU.DEBUG;
		}
	}

	@SuppressWarnings("boxing")
	public static String getLocalizedDate(long date)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date);

		return String.format(Locale.getDefault(), "%02d/%02d/%02d %02d:%02d:%02d",
				c.get(Calendar.DAY_OF_MONTH),
				(c.get(Calendar.MONTH) + 1), c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND)); // + ":" + c.get(Calendar.MILLISECOND) ;
	}

	/*public static String getLocalizedDate()
{
	return getLocalizedDate(System.currentTimeMillis());
}*/

	public void log(@NonNull NIVEAU niv, @NonNull String message)
	{
		_tracesDatabase.Ajoute(DatabaseHelper.CalendarToSQLiteDate(null), toInt(niv), message);
	}

	public void log(@NonNull NIVEAU niv, @NonNull Exception e)
	{
		log(niv, e.getLocalizedMessage());
		for (int i = 0; i < e.getStackTrace().length && i < MAX_BACKTRACE; i++)
			log(niv, e.getStackTrace()[i].getClassName() + '/' + e.getStackTrace()[i].getMethodName() + ':' + e.getStackTrace()[i].getLineNumber());

	}

	public void historique(@NonNull String message)
	{
		_historiqueDatabase.ajoute(DatabaseHelper.CalendarToSQLiteDate(null), message);
	}


}
