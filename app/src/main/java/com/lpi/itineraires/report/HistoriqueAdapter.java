package com.lpi.itineraires.report;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lpi.itineraires.R;
import com.lpi.itineraires.database.DatabaseHelper;

import java.util.Calendar;

/**
 * Adapter pour afficher l'historique
 * Created by lucien on 06/02/2016.
 */
public class HistoriqueAdapter extends CursorAdapter
{

	public HistoriqueAdapter(Context context, Cursor cursor)
	{
		super(context, cursor, 0);
	}

	public static String formatDate(Context context, int date)
	{
		Calendar c = DatabaseHelper.SQLiteDateToCalendar(date);
		return android.text.format.DateFormat.getDateFormat(context).format(c.getTime()) + ' '
				+ android.text.format.DateFormat.getTimeFormat(context).format(c.getTime());
	}

	/**
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @param context Interface to application's global information
	 * @param cursor  The cursor from which to get the data. The cursor is already
	 *                moved to the correct position.
	 * @param parent  The parent to which the new view is attached to
	 * @return the newly created view.
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return LayoutInflater.from(context).inflate(R.layout.ligne_rapport, parent, false);
	}

	/**
	 * Bind an existing view to the data pointed to by cursor
	 *
	 * @param view    Existing view, returned earlier by newView
	 * @param context Interface to application's global information
	 * @param cursor  The cursor from which to get the data. The cursor is already
	 */
	@Override
	public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor)
	{
		int date = cursor.getInt(cursor.getColumnIndex(ReportDatabaseHelper.COLONNE_HISTORIQUE_DATE));
		String ligne = cursor.getString(cursor.getColumnIndex(ReportDatabaseHelper.COLONNE_HISTORIQUE_LIGNE));

		TextView tv = view.findViewById(R.id.textView);

		tv.setText(formatDate(context, date) + ":" + ligne);
	}
}
