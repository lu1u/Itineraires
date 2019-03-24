package com.lpi.itineraires.details;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lpi.itineraires.R;
import com.lpi.itineraires.database.DatabaseHelper;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Position;
import com.lpi.itineraires.linegraphview.CoordLabel;
import com.lpi.itineraires.utils.Report;

public class DetailsActivity extends AppCompatActivity
{
	private int _randoId;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle b = getIntent().getExtras();
		if (b != null)
			_randoId = b.getInt(CourbeActivity.EXTRA_RANDO_ID);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		/*
	  The {@link android.support.v4.view.PagerAdapter} that will provide
	  fragments for each of the sections. We use a
	  {@link FragmentPagerAdapter} derivative, which will keep every
	  loaded fragment in memory. If this becomes too memory intensive, it
	  may be best to switch to a
	  {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */ /**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), _randoId);

		// Set up the ViewPager with the sections adapter.
		mViewPager = findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = findViewById(R.id.tabs);
		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		float[] _longitudes;
		float[] _latitudes;
		float[] _altitudes;
		float[] _bearings;
		float[] _temps;
		float[] _vitesse;
		float[] _distances;


		public SectionsPagerAdapter(FragmentManager fm, int randoId)
		{
			super(fm);
			Report report = Report.getInstance(DetailsActivity.this);
			ItinerairesDatabase database = ItinerairesDatabase.getInstance(DetailsActivity.this);
			Cursor cursor = database.getPositions(randoId);
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
					_longitudes[i] = (float) position.getLongitude();
					_latitudes[i] = (float) position.getLatitude();
					_altitudes[i] = (float) position.getAltitude();
					_bearings[i] = position.getBearing();
					//_temps[i] = (float) position.getTime();
					_temps[i] = i;
					_vitesse[i] = position.getSpeed();
					_distances[i] = distance;

					if (precedente != null)
						distance += precedente.distanceTo(position);

					precedente = position;
					i++;
				}
			}
		}

		@Nullable
		@Override
		public Fragment getItem(int position)
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
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).

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
		public int getCount()
		{
			// Show 3 total pages.
			return 4;
		}

	}
}
