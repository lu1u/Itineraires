package com.lpi.itineraires.details;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lpi.itineraires.R;
import com.lpi.itineraires.database.DatabaseHelper;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Position;
import com.lpi.itineraires.linegraphview.CoordLabel;

public class DetailsItinerairesActivity extends AppCompatActivity
{
	public static final String EXTRA_RANDO_ID = "com.lpi.itineraires.randoId";
	private int _randoId;
	float[] _longitudes;
	float[] _latitudes;
	float[] _altitudes;
	float[] _bearings;
	float[] _temps;
	float[] _vitesse;
	float[] _distances;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_itineraires);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		// Setup spinner
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		String[] pages = {
				"Détails",
				"Vitesse/Temps",
				"Distance/Temps",
				"Altitude/Temps",
		};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,pages );
		spinner.setAdapter(adapter);
		/*spinner.setAdapter(new MyAdapter(
				toolbar.getContext(),
				new String[]{
						"Détails",
						"Vitesse/Temps",
						"Distance/Temps",
						"Altitude/Temps",
				}));*/

		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// When the given dropdown item is selected, show its contents in the
				// container view.
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, getFragment(position))
						.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		/*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
*/
		Bundle b = getIntent().getExtras();
		if (b != null)
			_randoId = b.getInt(EXTRA_RANDO_ID);

		creerTableaux();
	}

	/***
	 * Creer les tableaux de données qui seront utilises dans les graphs
	 */
	private void creerTableaux()
	{
		ItinerairesDatabase database = ItinerairesDatabase.getInstance(this);
		Cursor cursor = database.getPositions(_randoId);
		if (null != cursor)
		{
			int nbElements = cursor.getCount();
			_longitudes = new float[nbElements];
			_latitudes = new float[nbElements];
			_altitudes = new float[nbElements];
			_bearings = new float[nbElements];
			_temps = new float[nbElements];
			_vitesse = new float[nbElements];
			_distances = new float[nbElements];

			//cursor.moveToFirst();
			int i = 0;
			float distance = 0.0f;
			Position precedente = null;

			while (cursor.moveToNext())
			{
				Position position = new Position(cursor);
				_longitudes[i] = (float) position.Longitude;
				_latitudes[i] = (float) position.Latitude;
				_altitudes[i] = (float) position.Altitude;
				_bearings[i] = position.Bearing;
				//_temps[i] = (float) position.Temps;
				_temps[i] = i;
				_vitesse[i] = position.Vitesse;
				_distances[i] = distance;

				if (precedente != null)
					distance += precedente.distanceTo(position);

				precedente = position;
				i++;
			}
		}
	}

	/***
	 * *********************************************************************************************
	 * Creer le fragment correspondant a la position dans les tabs
	 * @param position
	 * @return
	 * *********************************************************************************************
	 */
	private Fragment getFragment(final int position)
	{
		switch (position)
		{
			case 0:
				return DetailFragment.newInstance(_randoId);
			case 1:
				return CourbeRandoFragment.newInstance(_temps, _vitesse, creerAxeXTemps(), creerAxeYVitesses());       // Vitesse/Temps
			case 2:
				return CourbeRandoFragment.newInstance(_temps, _distances, null, null);      // Distance/Temps
			case 3:
				return CourbeRandoFragment.newInstance(_temps, _altitudes, null, null);       // Altitude/Temps

			default:
				return null;
		}
	}

	private float getMin(@Nullable float[] tableau)
	{
		float min = Float.MAX_VALUE;
		if (tableau != null)
			for (float f : tableau)
				if (f < min)
					min = f;
		return min;
	}

	private float getMax(@Nullable float[] tableau)
	{
		float max = Float.MIN_VALUE;
		if (tableau != null)
			for (float f : tableau)
				if (f > max)
					max = f;
		return max;
	}

	private CoordLabel[] creerAxeYVitesses()
	{
		if ( _vitesse==null)
			return null;
		if (_vitesse.length<2)
			return null;
		float min = getMin(_vitesse);
		float max = getMax(_vitesse);
		float echelle = getEchelle(min, max);
		int nb = (int)((max-min) / echelle);

		CoordLabel[] axe = new CoordLabel[nb];
		for (int i =0; i < nb; i++)
		{
			float v = min + (i*echelle);
			axe[i] = new CoordLabel(v, Float.toString(v)) ;
		}

		return axe;
	}

	private float getEchelle(float min, float max)
	{
		float place = max - min;

		double res = Math.log10(place);
		res = Math.floor(res)-1 ;
		res = Math.pow(10, res);
		return (float)res ;
	}

	private CoordLabel[] creerAxeXTemps()
	{
		if ( _temps ==null)
			return null;
		if (_temps.length<2)
			return null;
		float min = getMin(_temps);
		float max = getMax(_temps);
		float echelle = getEchelle(min, max);
		int nb = (int)((max-min) / echelle);

		CoordLabel[] axe = new CoordLabel[nb];
		for (int i =0; i < nb; i++)
		{
			float v = min + (i*echelle);
			axe[i] = new CoordLabel(v, DatabaseHelper.getTexteDateSecondes((long)v)) ;
		}

		return axe;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_details_itineraires, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter
	{
		private final Helper mDropDownHelper;

		public MyAdapter(Context context, String[] objects)
		{
			super(context, android.R.layout.simple_list_item_1, objects);
			mDropDownHelper = new Helper(context);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent)
		{
			View view;

			if (convertView == null)
			{
				// Inflate the drop down using the helper's LayoutInflater
				LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
				view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			} else
			{
				view = convertView;
			}

			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setText(getItem(position));

			return view;
		}

		@Override
		public Theme getDropDownViewTheme()
		{
			return mDropDownHelper.getDropDownViewTheme();
		}

		@Override
		public void setDropDownViewTheme(Theme theme)
		{
			mDropDownHelper.setDropDownViewTheme(theme);
		}
	}



}
