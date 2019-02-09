package com.lpi.itineraires.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lucien on 13/02/2016.
 */
public class GenericDatabaseAdapter extends CursorAdapter
{
	private final String[] colonnes;

	public GenericDatabaseAdapter(Context context, @NonNull Cursor cursor)
	{
		super(context, cursor, 0);
		colonnes = cursor.getColumnNames();
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
	@NonNull
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{

		LinearLayout l = new LinearLayout(context);
		l.setOrientation(LinearLayout.HORIZONTAL);
		l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		lp.gravity = Gravity.LEFT | Gravity.TOP;

		for (int i = 0; i < colonnes.length; i++)
		{
			TextView v = new TextView(context);
			if (i % 2 == 0)
				v.setTextColor(Color.parseColor("#000000"));
			else
				v.setTextColor(Color.parseColor("#000088"));
			v.setText(colonnes[i]);
			v.setLayoutParams(lp);
			v.setId(i);
			v.setPadding(0, 0, 10, 0);
			l.addView(v);
		}
		return l;
	}

	/**
	 * Bind an existing view to the data pointed to by cursor
	 *
	 * @param view    Existing view, returned earlier by newView
	 * @param context Interface to application's global information
	 * @param cursor  The cursor from which to get the data. The cursor is already
	 */
	@Override
	public void bindView(View view, Context context, @NonNull Cursor cursor)
	{
		LinearLayout l = (LinearLayout) view;

		for (int i = 0; i < colonnes.length; i++)
		{
			TextView v = l.findViewById(i);
			if (v != null)
				v.setText(DatabaseHelper.getStringFromAnyColumn(cursor, i));
		}

	}


}
