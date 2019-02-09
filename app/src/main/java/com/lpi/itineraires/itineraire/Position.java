package com.lpi.itineraires.itineraire;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.lpi.itineraires.database.DatabaseHelper;

public class Position
{
	public int IdRandonnee ; // La randonnee a laquelle est associee cette position
	public long Temps;       // l'heure-date de reception de cette position
	public double Latitude;
	public double Longitude;
	public double Altitude;
	public float Vitesse;
	public float Accurary;
	public float Bearing;
	@Nullable
	public String Provider;
	public Position()
	{
	}

	public Position(Location ici)
	{

		Temps = ici.getTime();
		Latitude = ici.getLatitude();
		Longitude = ici.getLongitude();
		Altitude = ici.getAltitude();
		Vitesse = ici.getSpeed();
		Provider = ici.getProvider();
		Bearing = ici.getBearing();
		Accurary = ici.getAccuracy();
	}

	public Position(int idrandonnee, int temps, double latitude, double longitude, double altitude, float vitesse)
	{
		IdRandonnee = idrandonnee;
		Temps = temps;
		Latitude = latitude;
		Longitude = longitude;
		Altitude = altitude;
		Vitesse = vitesse;
	}

	public Position(@Nullable Cursor cursor)
	{
		if (cursor != null)
		{
			IdRandonnee = cursor.getInt     (cursor.getColumnIndex       (DatabaseHelper.COLONNE_POS_IDRANDO));
			Temps       = cursor.getLong    (cursor.getColumnIndex       (DatabaseHelper.COLONNE_POS_TEMPS));
			Latitude    = cursor.getDouble  (cursor.getColumnIndex    (DatabaseHelper.COLONNE_POS_LATITUDE));
			Longitude   = cursor.getDouble  (cursor.getColumnIndex    (DatabaseHelper.COLONNE_POS_LONGITUDE));
			Altitude    = cursor.getDouble  (cursor.getColumnIndex    (DatabaseHelper.COLONNE_POS_ALTITUDE));
			Vitesse     = cursor.getFloat   (cursor.getColumnIndex     (DatabaseHelper.COLONNE_POS_VITESSE));
			Accurary    = cursor.getFloat   (cursor.getColumnIndex     (DatabaseHelper.COLONNE_POS_ACCURACY));
			Bearing     = cursor.getFloat   (cursor.getColumnIndex     (DatabaseHelper.COLONNE_POS_BEARING));
			Provider    = cursor.getString  (cursor.getColumnIndex     (DatabaseHelper.COLONNE_POS_PROVIDER));
		}
	}

	public Position(Bundle bundle)
	{
		IdRandonnee = bundle.getInt     (DatabaseHelper.COLONNE_POS_IDRANDO);
		Temps       = bundle.getLong    (DatabaseHelper.COLONNE_POS_TEMPS);
		Latitude    = bundle.getDouble  (DatabaseHelper.COLONNE_POS_LATITUDE);
		Longitude   = bundle.getDouble  (DatabaseHelper.COLONNE_POS_LONGITUDE);
		Altitude    = bundle.getDouble  (DatabaseHelper.COLONNE_POS_ALTITUDE);
		Vitesse     = bundle.getFloat   (DatabaseHelper.COLONNE_POS_VITESSE);
		Accurary    = bundle.getFloat   (DatabaseHelper.COLONNE_POS_ACCURACY);
		Bearing     = bundle.getFloat   (DatabaseHelper.COLONNE_POS_BEARING);
		Provider    = bundle.getString  (DatabaseHelper.COLONNE_POS_PROVIDER);
	}

	public static String formateDistance(float distance)
	{
		if ( distance < 1000)
			return  String.format("%.02f", distance) + "m" ;
		else
			return String.format("%.02f", distance/1000.0) + "km";
	}


	public void Copie(@NonNull Position p)
	{
		IdRandonnee = p.IdRandonnee ;
		Temps       = p.Temps       ;
		Latitude    = p.Latitude    ;
		Longitude   = p.Longitude   ;
		Altitude    = p.Altitude    ;
		Vitesse     = p.Vitesse     ;
		Accurary    =  p.Accurary   ;
		Bearing     =  p.Bearing    ;
		Provider    =  p.Provider   ;
	}

	public void toContentValues(@NonNull ContentValues content)
	{
		content.put(DatabaseHelper.COLONNE_POS_IDRANDO,     IdRandonnee);
		content.put(DatabaseHelper.COLONNE_POS_TEMPS, Temps      );
		content.put(DatabaseHelper.COLONNE_POS_LATITUDE, Latitude   );
		content.put(DatabaseHelper.COLONNE_POS_LONGITUDE, Longitude  );
		content.put(DatabaseHelper.COLONNE_POS_ALTITUDE, Altitude   );
		content.put(DatabaseHelper.COLONNE_POS_VITESSE, Vitesse    );
		content.put(DatabaseHelper.COLONNE_POS_ACCURACY, Accurary);
		content.put(DatabaseHelper.COLONNE_POS_BEARING, Bearing );
		content.put(DatabaseHelper.COLONNE_POS_PROVIDER, Provider);
	}

	public void toBundle(@NonNull Bundle bundle)
	{
		bundle.putInt(DatabaseHelper.COLONNE_POS_IDRANDO,     IdRandonnee);
		bundle.putLong(DatabaseHelper.COLONNE_POS_TEMPS, Temps      );
		bundle.putDouble(DatabaseHelper.COLONNE_POS_LATITUDE, Latitude   );
		bundle.putDouble(DatabaseHelper.COLONNE_POS_LONGITUDE, Longitude  );
		bundle.putDouble(DatabaseHelper.COLONNE_POS_ALTITUDE, Altitude   );
		bundle.putFloat(DatabaseHelper.COLONNE_POS_VITESSE, Vitesse    );
		bundle.putFloat(DatabaseHelper.COLONNE_POS_ACCURACY, Accurary);
		bundle.putFloat(DatabaseHelper.COLONNE_POS_BEARING, Bearing );
		bundle.putString(DatabaseHelper.COLONNE_POS_PROVIDER, Provider);
	}

	public Location toLocation()
	{
		Location loc;
		loc = new Location(LocationManager.GPS_PROVIDER) ;
		loc.setAccuracy(Accurary);
		loc.setAltitude(Altitude);
		loc.setBearing(Bearing);
		loc.setLatitude(Latitude);
		loc.setLongitude(Longitude);
		loc.setProvider(Provider);
		loc.setSpeed(Vitesse);
		loc.setTime(Temps);
		return loc;
	}


	public float distanceTo( @NonNull Position pos)
	{
		return toLocation().distanceTo(pos.toLocation());
	}


	public LatLng toLatLng()
	{
		return new LatLng(Latitude, Longitude);
	}
}


