package com.lpi.itineraires.itineraire;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.lpi.itineraires.database.DatabaseHelper;

public class Position extends Location
{
	public int IdRandonnee; // La randonnee a laquelle est associee cette position

	public Position()
	{
		super("GPS");
	}

	public Position(Location ici)
	{
		super(ici);
	}

	public Position(@Nullable Cursor cursor)
	{
		super("");
		if (cursor != null)
		{

			IdRandonnee = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_IDRANDO));
			setTime(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_TEMPS)));
			setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_LATITUDE)));
			setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_LONGITUDE)));
			setAltitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_ALTITUDE)));
			setSpeed(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_VITESSE)));
			setAccuracy(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_ACCURACY)));
			setBearing(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_BEARING)));
			setProvider(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLONNE_POS_PROVIDER)));
		}
	}

	public Position(@NonNull Bundle bundle)
	{
		super("");

		IdRandonnee = bundle.getInt(DatabaseHelper.COLONNE_POS_IDRANDO);
		setTime(bundle.getLong(DatabaseHelper.COLONNE_POS_TEMPS));
		setLatitude(bundle.getDouble(DatabaseHelper.COLONNE_POS_LATITUDE));
		setLongitude(bundle.getDouble(DatabaseHelper.COLONNE_POS_LONGITUDE));
		setAltitude(bundle.getDouble(DatabaseHelper.COLONNE_POS_ALTITUDE));
		setSpeed(bundle.getFloat(DatabaseHelper.COLONNE_POS_VITESSE));
		setAccuracy(bundle.getFloat(DatabaseHelper.COLONNE_POS_ACCURACY));
		setBearing(bundle.getFloat(DatabaseHelper.COLONNE_POS_BEARING));
		setProvider(bundle.getString(DatabaseHelper.COLONNE_POS_PROVIDER));
	}

	public static String formateDistance(float distance)
	{
		if (distance < 1000)
			return String.format("%.02f", distance) + "m";
		else
			return String.format("%.02f", distance / 1000.0) + "km";
	}


	public void toContentValues(@NonNull ContentValues content)
	{
		content.put(DatabaseHelper.COLONNE_POS_IDRANDO, IdRandonnee);
		content.put(DatabaseHelper.COLONNE_POS_TEMPS, getTime());
		content.put(DatabaseHelper.COLONNE_POS_LATITUDE, getLatitude());
		content.put(DatabaseHelper.COLONNE_POS_LONGITUDE, getLongitude());
		content.put(DatabaseHelper.COLONNE_POS_ALTITUDE, getAltitude());
		content.put(DatabaseHelper.COLONNE_POS_VITESSE, getSpeed());
		content.put(DatabaseHelper.COLONNE_POS_ACCURACY, getAccuracy());
		content.put(DatabaseHelper.COLONNE_POS_BEARING, getBearing());
		content.put(DatabaseHelper.COLONNE_POS_PROVIDER, getProvider());
	}


	public LatLng toLatLng()
	{
		return new LatLng(getLatitude(), getLongitude());
	}


	public String getDescription()
	{
		return String.format("Latitude: %.4f\nLongitude: %.4f\nAltitude: %4.2fm\nVitesse: %sm/s\nTemps: %s\nPrécision: %4.2fm\nProvider: %s",
				getLatitude(), getLongitude(), getAltitude(), Itineraire.formateVitesse(getSpeed()), DatabaseHelper.getTexteDateSecondes(getTime()), getAccuracy(), getProvider());
	}
}


