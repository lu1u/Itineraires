package com.lpi.itineraires;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.lpi.itineraires.utils.Preferences;
import com.lpi.itineraires.utils.Utils;

public class PreferencesActivity extends AppCompatActivity
{
	Spinner _spinnerTheme;
	SeekBar _sbDelai, _sbDistances;
	TextView _textDelai, _textDistance;
	Switch _localisationGPS, _localisationReseau;
	String[] _delais;
	String[] _distances;
	int[] _distancesValeurs;
	int[] _delaisValeurs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Utils.setTheme(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		_spinnerTheme = findViewById(R.id.spinnerTheme);

		initTheme();
		initDelaiMin();
		initDistanceMin();
		initLocalisation();
	}

	private void initLocalisation()
	{
		_localisationGPS = findViewById(R.id.switchLocalisationGPS);
		_localisationReseau = findViewById(R.id.switchLocalisationReseau);
		final Preferences pref = Preferences.getInstance(this);

		_localisationGPS.setChecked(pref.getLocalisationGPS());
		_localisationReseau.setChecked(pref.getLocalisationReseau());

		_localisationGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
			{
				pref.setLocalisationGPS(isChecked);
			}
		});

		_localisationReseau.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
			{
				pref.setLocalisationReseau(isChecked);
			}
		});
	}

	/***
	 * Initialisation de l'interface pour choisir le theme de l'application
	 */
	private void initTheme()
	{
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.themes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinnerTheme.setAdapter(adapter);

		_spinnerTheme.setSelection(Preferences.getInstance(this).getTheme());
		_spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{

				Preferences pref = Preferences.getInstance(PreferencesActivity.this);
				if (position != pref.getTheme())
				{
					pref.setTheme(position);
					TaskStackBuilder.create(PreferencesActivity.this)
							.addNextIntent(new Intent(PreferencesActivity.this, com.lpi.itineraires.MainActivity.class))
							.addNextIntent(PreferencesActivity.this.getIntent())
							.startActivities();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});
	}

	/***
	 * Initialisation de l'interface pour choisir la distance GPS min
	 */
	private void initDistanceMin()
	{
		_sbDistances = findViewById(R.id.seekBarDistance);
		_textDistance = findViewById(R.id.textViewDistance);
		_distances = getResources().getStringArray(R.array.distances);
		_distancesValeurs = getResources().getIntArray(R.array.distances_valeurs);
		//_sbDistances.setMin(0);
		_sbDistances.setMax(_distances.length - 1);
		_sbDistances.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (progress < 0 || progress >= _distances.length)
					return;

				_textDistance.setText(_distances[progress]);
				if (fromUser)
					Preferences.getInstance(PreferencesActivity.this).setGPSMinDistance(_distancesValeurs[progress]);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{

			}
		});
		_sbDistances.setProgress(getIndiceDistances());
	}

	/***
	 * Initialisation de l'interface pour choisir le delai GPS minimum
	 */
	private void initDelaiMin()
	{
		_sbDelai = findViewById(R.id.seekBarDelai);
		_textDelai = findViewById(R.id.textViewDelai);
		_delais = getResources().getStringArray(R.array.delais);
		_delaisValeurs = getResources().getIntArray(R.array.delais_valeurs);
		//_sbDelai.setMin(0);
		_sbDelai.setMax(_delais.length - 1);
		_distancesValeurs = getResources().getIntArray(R.array.distances_valeurs);
		_sbDelai.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (progress < 0 || progress >= _delais.length)
					return;

				_textDelai.setText(_delais[progress]);
				if (fromUser)
				{
					Preferences.getInstance(PreferencesActivity.this).setGPSMinTime(_delaisValeurs[progress]);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{

			}
		});
		_sbDelai.setProgress(getIndiceDelai());
	}

	private int getIndiceDistances()
	{
		Preferences prefs = Preferences.getInstance(this);
		final int delai = prefs.getGPSMinDistance();
		for (int i = 0; i < _distancesValeurs.length; i++)
			if (_distancesValeurs[i] == delai)
				return i;

		return 0;
	}

	private int getIndiceDelai()
	{
		Preferences prefs = Preferences.getInstance(this);
		final int delai = prefs.getGPSMinTime();
		for (int i = 0; i < _delaisValeurs.length; i++)
			if (_delaisValeurs[i] == delai)
				return i;

		return 0;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		_sbDelai = findViewById(R.id.seekBarDelai);
		//_sbDelai.setMin(0);
		_sbDelai.setMax(_delais.length - 1);
		_sbDelai.setProgress(getIndiceDelai());

		_sbDistances = findViewById(R.id.seekBarDistance);
		//_sbDistances.setMin(0);
		_sbDistances.setMax(_distances.length - 1);
		_sbDistances.setProgress(getIndiceDistances());
	}
}
