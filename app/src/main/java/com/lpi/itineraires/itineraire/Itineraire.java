////////////////////////////////////////////////////////////////////////////////////////////////////
// Gestion d'un itineraire: lecture dans la base, ecriture...
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.lpi.itineraires.itineraire;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lpi.itineraires.R;
import com.lpi.itineraires.database.DatabaseHelper;
import com.lpi.itineraires.database.ItinerairesDatabase;

import java.util.Calendar;

/**
 * Created by lucien on 26/01/2016.
 */
public class Itineraire
{
	enum TYPE
	{
		APIED, VTT, VELO, SKI_PISTE, SKI_FOND, SKI_RANDONNEE, MOTO, VOITURE, AUTRE
	}

	private static final long MINUTE = 60;
	private static final long HEURE = MINUTE * 60;
	private static final long JOUR = HEURE * 24;

	public static final int INVALID_ID = -1;
	public int Id = INVALID_ID;
	public String Nom = "";
	public TYPE Type = TYPE.AUTRE;
	public long DateCreation = 0;
	public long DateDebut = 0;
	public long DateFin = 0;
	public boolean Enregistre = false;

	public Itineraire()
	{
		Id = -1;
		Nom = "sans nom";
		Type = TYPE.AUTRE;
		DateCreation = Calendar.getInstance().getTimeInMillis();
		DateDebut = 0;
		DateFin = 0;
		Enregistre = false;
	}

	public Itineraire(int id, String nom, TYPE type, int dateCreation, int dateDebut, int dateFin, boolean enregistre)
	{
		Id = id;
		Nom = nom;
		Type = type;
		DateCreation = dateCreation;
		DateDebut = dateDebut;
		DateFin = dateFin;
		Enregistre = enregistre;
	}

	/***
	 * Creer un itineraire a partir de la base de donnee
	 * @param cursor
	 */
	public Itineraire(@Nullable Cursor cursor) throws IllegalArgumentException
	{
		if (cursor != null)
		{
			Id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_ID));
			Nom = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_NOM));
			Type = intToType(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_TYPE)));
			DateCreation = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_CREATION));
			DateDebut = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_DEBUT));
			DateFin = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_FIN));
			Enregistre = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_ENREGISTRE)) != 0;
		}
	}


	/***
	 * Creer un itineraire a partir d'un bundle
	 * @param bundle
	 */
	public Itineraire(@NonNull Bundle bundle)
	{
		Id = bundle.getInt(DatabaseHelper.COLONNE_ITI_ID, Id);
		Nom = bundle.getString(DatabaseHelper.COLONNE_ITI_NOM, Nom);
		Type = intToType(bundle.getInt(DatabaseHelper.COLONNE_ITI_TYPE, typeToInt(Type)));
		DateCreation = bundle.getLong(DatabaseHelper.COLONNE_ITI_CREATION, DateCreation);
		DateDebut = bundle.getLong(DatabaseHelper.COLONNE_ITI_DEBUT, DateDebut);
		DateFin = bundle.getLong(DatabaseHelper.COLONNE_ITI_FIN, DateFin);
		Enregistre = bundle.getInt(DatabaseHelper.COLONNE_ITI_ENREGISTRE, Enregistre ? 1 : 0) != 0;
	}


	public void Copie(@NonNull Itineraire p)
	{
		Id = p.Id;
		Nom = p.Nom;
		Type = p.Type;
		DateCreation = p.DateCreation;
		DateDebut = p.DateDebut;
		DateFin = p.DateFin;
		Enregistre = p.Enregistre;
	}

	public void toContentValues(@NonNull ContentValues content, boolean putId)
	{
		if (putId)
			content.put(DatabaseHelper.COLONNE_ITI_ID, Id);
		content.put(DatabaseHelper.COLONNE_ITI_NOM, Nom);
		content.put(DatabaseHelper.COLONNE_ITI_TYPE, typeToInt(Type));
		content.put(DatabaseHelper.COLONNE_ITI_CREATION, DateCreation);
		content.put(DatabaseHelper.COLONNE_ITI_DEBUT, DateDebut);
		content.put(DatabaseHelper.COLONNE_ITI_FIN, DateFin);
		content.put(DatabaseHelper.COLONNE_ITI_ENREGISTRE, Enregistre ? 1 : 0);
	}

	public void toBundle(@NonNull Bundle bundle)
	{
		bundle.putInt(DatabaseHelper.COLONNE_ITI_ID, Id);
		bundle.putString(DatabaseHelper.COLONNE_ITI_NOM, Nom);
		bundle.putInt(DatabaseHelper.COLONNE_ITI_TYPE, typeToInt(Type));
		bundle.putLong(DatabaseHelper.COLONNE_ITI_CREATION, DateCreation);
		bundle.putLong(DatabaseHelper.COLONNE_ITI_DEBUT, DateDebut);
		bundle.putLong(DatabaseHelper.COLONNE_ITI_FIN, DateFin);
		bundle.putInt(DatabaseHelper.COLONNE_ITI_ENREGISTRE, Enregistre ? 1 : 0);
	}

	/***
	 * Calcule une representation textuelle de l'itineraire
	 * @param context
	 * @return
	 */
	public String getDescription(@NonNull Context context, boolean avecType)
	{
		String texte;

		if (avecType)
			texte = getTexteType(Type) + "\n";
		else
			texte = "";

		// Texte descriptif
		if (Enregistre)
			texte += context.getResources().getString(R.string.enregistrement_en_cours, DatabaseHelper.getTexteDateSecondes(DateDebut));
		else
		{
			if (DateDebut > 0 && DateFin > 0)
			{
				texte += context.getResources().getString(R.string.debut_fin, DatabaseHelper.getTexteDateSecondes(DateDebut), DatabaseHelper.getTexteDateSecondes(DateFin));
			}
			else
				if (DateDebut > 0)
				{
					texte += context.getResources().getString(R.string.debut, DatabaseHelper.getTexteDateSecondes(DateDebut));
				}
				else
				{
					texte += context.getResources().getString(R.string.creation, DatabaseHelper.getTexteDateSecondes(DateCreation));
				}

			final int nbPositions = ItinerairesDatabase.getInstance(context).getNbPositions(Id);
			if (nbPositions > 0)
				texte += context.getResources().getString(R.string.nbPositions, nbPositions);
		}

		return texte;
	}

	public static @NonNull
	String getTexteType(TYPE type)
	{
		switch (type)
		{
			case APIED:
				return "à pied";
			case VTT:
				return "vtt";
			case VELO:
				return "vélo";
			case SKI_PISTE:
				return "ski de piste";
			case SKI_RANDONNEE:
				return "ski de randonnée";
			case SKI_FOND:
				return "ski de fond";
			case MOTO:
				return "moto";
			case VOITURE:
				return "auto";
			default:
				return "autre";
		}
	}

	@NonNull
	static public TYPE intToType(int t)
	{
		switch (t)
		{
			case 0:
				return TYPE.APIED;
			case 1:
				return TYPE.VTT;
			case 2:
				return TYPE.VELO;
			case 3:
				return TYPE.SKI_PISTE;
			case 4:
				return TYPE.SKI_RANDONNEE;
			case 5:
				return TYPE.SKI_FOND;
			case 6:
				return TYPE.MOTO;
			case 7:
				return TYPE.VOITURE;
			default:
				return TYPE.AUTRE;
		}
	}

	static public int typeToInt(TYPE t)
	{
		switch (t)
		{
			case APIED:
				return 0;
			case VTT:
				return 1;
			case VELO:
				return 2;
			case SKI_PISTE:
				return 3;
			case SKI_RANDONNEE:
				return 4;
			case SKI_FOND:
				return 5;
			case MOTO:
				return 6;
			case VOITURE:
				return 7;
			default:
				return 8;
		}
	}

	public String getDetails(Context context)
	{
		String texte = "pas de randonnée";

		texte = "Nom: " + Nom + " (Id=" + Id + ")\n\n";
		texte += getDescription(context, true);

		ItinerairesDatabase database = ItinerairesDatabase.getInstance(context);
		final int nbPositions = database.getNbPositions(Id);

		if (nbPositions > 1)
		{
			Cursor cursor = database.getPositions(Id);
			if (null != cursor)
			{
				cursor.moveToFirst();
				Position precedente = new Position(cursor);
				float Distance = 0;
				float vitesseMin = precedente.Vitesse;
				float vitesseMax = precedente.Vitesse;
				double altitudeMin = precedente.Altitude;
				double altitudeMax = precedente.Altitude;
				double denivellePos = 0;
				double denivelleNeg = 0;

				long debut = precedente.Temps;

				while (cursor.moveToNext())
				{
					Position position = new Position(cursor);
					Distance += precedente.distanceTo(position);
					if (position.Vitesse < vitesseMin)
						vitesseMin = position.Vitesse;

					if (position.Vitesse > vitesseMax)
						vitesseMax = position.Vitesse;

					if ( position.Altitude < altitudeMin)
						altitudeMin = position.Altitude;

					if ( position.Altitude> altitudeMax)
						altitudeMax = position.Altitude;

					if ( position.Altitude > precedente.Altitude)
						denivellePos += (position.Altitude-precedente.Altitude);
					else
						denivelleNeg += (position.Altitude-precedente.Altitude);

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

				texte += "\n\nAltitude min " + altitudeMin + "m";
				texte += "\nAltitude max " + altitudeMax + "m";
				texte += "\nDénivelé " + (altitudeMax-altitudeMin) + "m" ;
				texte += "\nCumul dénivellé positif " + denivellePos + "m";
				texte += "\nCumul dénivellé negatif " + denivelleNeg + "m";
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
}
