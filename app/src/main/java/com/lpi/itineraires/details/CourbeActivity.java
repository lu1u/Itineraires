package com.lpi.itineraires.details;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lpi.itineraires.R;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Itineraire;
import com.lpi.itineraires.itineraire.Position;
import com.lpi.itineraires.linegraphview.ZoomableLinegraphView;

public class CourbeActivity extends AppCompatActivity
{
	public static final String EXTRA_RANDO_ID = "com.lpi.itineraires.randoId";

	/***
	 * Afficher l'activity en lui communicant les parametres necessaires
	 * @param context
	 * @param id : id de l'itineraire
	 */
	public static void start(final Context context, final int id)
	{
		Intent intent = new Intent(context, CourbeActivity.class);
		Bundle b = new Bundle();
		b.putInt(EXTRA_RANDO_ID, id);
		intent.putExtras(b);
		context.startActivity(intent);
	}

	private enum MODE
	{
		TEXTE, VITESSE, ALTITUDE, DISTANCE
	}

	private static final long MINUTE = 60;
	private static final long HEURE = MINUTE * 60;
	private static final long JOUR = HEURE * 24;

	private MODE _mode = MODE.TEXTE;

	private TextView _textDetails;
	private ZoomableLinegraphView _vitesseTemps, _altitudeTemps, _distanceTemps;
	private Spinner _spinner;

	private Itineraire _itineraire;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courbe);
		Bundle b = getIntent().getExtras();
		if (b != null)
		{
			int id = b.getInt(DetailsItinerairesActivity.EXTRA_RANDO_ID);
			_itineraire = ItinerairesDatabase.getInstance(this).getItineraire(id);
		}

		_textDetails = findViewById(R.id.textViewDescription);
		_textDetails.setText(getDescription(_itineraire));
		_vitesseTemps = findViewById(R.id.ilgvVitesseTemps);
		_altitudeTemps = findViewById(R.id.ilgvAltitudeTemps);
		_distanceTemps = findViewById(R.id.ilgvDistanceTemps);
		_spinner = findViewById(R.id.spinnerChoix);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.vues_details, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		_spinner.setAdapter(adapter);
		_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id)
			{
				switch (position)
				{
					case 0:
						_mode = MODE.TEXTE;
						break;
					case 1:
						_mode = MODE.VITESSE;
						break;
					case 2:
						_mode = MODE.ALTITUDE;

					case 3:
						_mode = MODE.DISTANCE;
				}
				switchInterface();
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {}
		});


	}

	private String getDescription(final Itineraire itineraire)
	{
		String texte = "pas de randonnée";

		if (_itineraire != null)
		{
			texte = "Nom: " + _itineraire.Nom + " (Id=" + _itineraire.Id + ")\n\n";
			texte += _itineraire.getDescription(this, true);

			ItinerairesDatabase database = ItinerairesDatabase.getInstance(this);
			final int nbPositions = database.getNbPositions(_itineraire.Id);

			if (nbPositions > 1)
			{
				Cursor cursor = database.getPositions(_itineraire.Id);
				if (null != cursor)
				{
					cursor.moveToFirst();
					Position precedente = new Position(cursor);
					float Distance = 0;
					float vitesseMin = precedente.Vitesse;
					float vitesseMax = precedente.Vitesse;
					long debut = precedente.Temps;

					while (cursor.moveToNext())
					{
						Position position = new Position(cursor);
						Distance += precedente.distanceTo(position);
						if (position.Vitesse < vitesseMin)
							vitesseMin = position.Vitesse;

						if (position.Vitesse > vitesseMax)
							vitesseMax = position.Vitesse;

						precedente = position;
					}
					long fin = precedente.Temps; // derniere
					long duree = (fin - debut) / 1000L;
					texte += "\n\nDurée totale " + formateDuree(duree);
					texte += " \n\nDistance parcourue " + Position.formateDistance(Distance);
					cursor.close();

					float vitesseMoyenne = Distance / (float) duree;
					texte += "\n\nVitesse moyenne " + formateVitesse(vitesseMoyenne);
					texte += "\nVitesse min " + formateVitesse(vitesseMin);
					texte += "\nVitesse max " + formateVitesse(vitesseMax);

				}
			}
		}
		return texte;
	}

	public static String formateVitesse(float vitesseMs)
	{
		float vitesseKm = vitesseMs * 3.6f;
		return String.format("%.02f", vitesseMs) + "m/s, " + String.format("%.02f", vitesseKm) + "km/h";
	}

	@NonNull
	public static String formateDuree(long dureeEnSecondes)
	{
		String res = "";
		if (dureeEnSecondes > JOUR) // Jours
		{
			res += (dureeEnSecondes / JOUR) + "j ";
			dureeEnSecondes = dureeEnSecondes % JOUR;
		}

		if (dureeEnSecondes > HEURE)
		{
			res += (dureeEnSecondes / HEURE) + "h ";
			dureeEnSecondes = dureeEnSecondes % HEURE;
		}

		if (dureeEnSecondes > MINUTE)
		{
			res += (dureeEnSecondes / MINUTE) + "m ";
			dureeEnSecondes = dureeEnSecondes % MINUTE;
		}

		res += dureeEnSecondes + "s";
		return res;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		switchInterface();
	}

	/***
	 * Affiche l'interface en fonction du mode choisi
	 */
	private void switchInterface()
	{
		switch (_mode)
		{
			case TEXTE:
				_textDetails.setVisibility(View.VISIBLE);
				_vitesseTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.GONE);
				break;

			case VITESSE:
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.VISIBLE);
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.GONE);
				break;

			case ALTITUDE:
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.VISIBLE);
				_distanceTemps.setVisibility(View.GONE);
				break;

			case DISTANCE:
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.VISIBLE);
				break;
			default:
		}
	}
}
