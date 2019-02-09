////////////////////////////////////////////////////////////////////////////////////////////////////
// Gestion du GPS dans un service pour continuer a recevoir les positions meme si l'activity
// principale n'est plus active
////////////////////////////////////////////////////////////////////////////////////////////////////
package com.lpi.itineraires.GPS;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.lpi.itineraires.database.DatabaseHelper;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Position;
import com.lpi.itineraires.utils.Preferences;
import com.lpi.itineraires.utils.Report;
import com.lpi.itineraires.utils.Utils;


public class GPSService extends Service implements LocationListener
{
	private static final String DEMARRAGE = GPSService.class.getCanonicalName() + ".action.DEMARRAGE";
	private static final String STOP = GPSService.class.getCanonicalName() + ".action.STOP";
	private static final int DELTA_TIME = 1000 * 60;//* 2;


	private Report _report;
	@Nullable
	private LocationManager _locationManager;
	private Location dernierePosition;

	public GPSService()
	{
	}

	/***
	 * Mise a jour du service: demarrage ou arret en fonction du nombre
	 * de randonnees qui enregistrent a ce moment
	 * @param context
	 */
	public static void update(Context context)
	{
		// Obtenir le nombre de randonnees en cours d'enregistrement
		ItinerairesDatabase database = ItinerairesDatabase.getInstance(context);
		final int nbRandonnees = database.getItinerairesEnregistrant();

		// Si 0 randonnees: arreter le GPS
		if (nbRandonnees == 0)
			GPSService.arrete(context);
		else
			if (!Utils.serviceEstDemarre(context, GPSService.class))
				GPSService.demarre(context);
	}

	/***
	 * Previent le service qu'il doit s'arreter
	 * @param context
	 */
	public static void arrete(@NonNull Context context)
	{
		Intent intent = new Intent(context, GPSService.class);
		intent.setAction(STOP);
		context.startService(intent);
	}

	/***
	 * Demarre le service
	 * @param context
	 */
	public static void demarre(@NonNull Context context)
	{
		Intent intent = new Intent(context, GPSService.class);
		intent.setAction(DEMARRAGE);
		context.startService(intent);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}


	/***
	 * receptiond de la commande de demarrage ou d'arret
	 * @param intent
	 * @param flags
	 * @param startId
	 * @return
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);

		String action = intent.getAction();
		if (DEMARRAGE.equals(action))
		{
			// Demarre le service
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(DEMARRAGE);
			intentFilter.addAction(STOP);
			// Enregistre un broadcastreceiver pour recevoir les commandes
			registerReceiver(new BroadcastReceiver()
			{
				@Override
				public void onReceive(Context context, Intent intent)
				{
					onServiceReceive(context, intent);
				}
			}, intentFilter);

			handleActionDemarre();
			return START_STICKY;
		}
		else
			if (STOP.equals(action))
			{
				// Arrete ce service
				handleActionStop();
			}

		return START_NOT_STICKY;
	}

	/**
	 * Reception d'un message a destination de ce service
	 *
	 * @param context : Context
	 * @param intent  : Intent recu
	 */
	protected void onServiceReceive(Context context, @Nullable Intent intent)
	{
		if (intent != null)
		{
			final String action = intent.getAction();
			if (DEMARRAGE.equals(action))
			{
				handleActionDemarre();
			}
			else
				if (STOP.equals(action))
				{
					handleActionStop();
				}
		}
	}

	/**
	 * Demarrer le GPS
	 */
	private void handleActionDemarre()
	{
		// demarrer le GPS
		try
		{
			Context context = getApplicationContext();
			_report = Report.getInstance(context);
			Preferences prefs = Preferences.getInstance(context);

			_report.historique("Démarrage GPS");
			if (_locationManager == null)
			{
				// GPS
				// Les permissions ont ete verifiee au demarrage de l'application
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
						|| ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
				{
					String[] p = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
					ActivityCompat.requestPermissions(null, p, 0);
					return;
				}

				_report.log(Report.NIVEAU.DEBUG, "Création du GPS");
				_locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				final int GPSDelai = prefs.getGPSMinTime();
				final int GPSDistance = prefs.getGPSMinDistance();

				if (prefs.getLocalisationGPS())
				{
					_report.log(Report.NIVEAU.DEBUG, "Demarrage localisation GPS");
					_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPSDelai, GPSDistance, this);
				}
				if (prefs.getLocalisationReseau())
				{
					_report.log(Report.NIVEAU.DEBUG, "Demarrage localisation Reseau");
					_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPSDelai, GPSDistance, this);
				}

			}
			else
				_locationManager = null;
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, e);
		}
	}

	/**
	 * Handle action Baz in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionStop()
	{
		try
		{
			if (_locationManager != null)
			{
				_report.log(Report.NIVEAU.DEBUG, "Arret du GPS");
				_locationManager.removeUpdates(this);
				_locationManager = null;
			}

			// Arreter ce service
			this.stopSelf();
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, e);
		}
	}

	/***
	 * Reception d'une nouvelle position GPS
	 * @param ici : position GPS
	 */
	@Override
	public void onLocationChanged(@NonNull Location ici)
	{
		_report.log(Report.NIVEAU.DEBUG, "Nouvelle position GPS");
		if (!isBetterLocation(ici, dernierePosition))
		{
			_report.log(Report.NIVEAU.DEBUG, "position pas meilleure que l'ancienne");
			// Pas une meilleure localisation
			return;
		}

		// Ajouter cette position a toutes les randonnees qui enregistrent
		Position pos = new Position(ici);
		pos.Vitesse = getVitesse(ici);          // Truc pour le cas ou la vitesse n'est pas supportee par le GPS

		_report.log(Report.NIVEAU.DEBUG, String.format("Lat:%.03f, Lg:%.03f, V:%.02f, T:%s", (float) pos.Latitude, (float) pos.Longitude, pos.Vitesse, Long.toString(pos.Temps)));

		ItinerairesDatabase database = ItinerairesDatabase.getInstance(getApplicationContext());
		try
		{
			//database.beginTransaction();
			Cursor c = database.getItinerairesIdEnregistrant();
			if (c != null)
			{
				final int colonne = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_ID);
				// Ajouter cette position pour toutes les randonnees enregistrant
				while (c.moveToNext())
				{
					pos.IdRandonnee = c.getInt(colonne);
					_report.log(Report.NIVEAU.DEBUG, "ajout d'une position pour rando id=" + pos.IdRandonnee);
					database.ajoute(pos);
				}
				c.close();
			}
			//database.setTransactionSuccessful();
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur lors de l'ajout d'une nouvelle position");
			_report.log(Report.NIVEAU.ERROR, e);
		}

		//database.endTransaction();
		dernierePosition = ici;
	}

	/***
	 * Retrouve la vitesse sur la position, directement si supporté par le GPS, indirectement sinon
	 * @param loc
	 * @return
	 */
	private float getVitesse(@NonNull Location loc)
	{
		if (loc.hasSpeed())
		{
			_report.log(Report.NIVEAU.DEBUG, "Location has speed: " + loc.getSpeed() + "m/s");
			return loc.getSpeed();
		}

		if (dernierePosition == null)
		{
			_report.log(Report.NIVEAU.DEBUG, "Premiere position: vitesse 0");
			return 0;
		}

		double distance = loc.distanceTo(dernierePosition);
		double temps = loc.getTime() - dernierePosition.getTime();
		_report.log(Report.NIVEAU.DEBUG, "Location ne supporte pas vitesse: " + distance + "m / " + temps + "s =" + (float) (distance / temps) + "m/s");

		return (float) (distance / temps);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		String Status = status == LocationProvider.AVAILABLE ? "available" : status == LocationProvider.OUT_OF_SERVICE ? "out of service" : status == LocationProvider.TEMPORARILY_UNAVAILABLE ? "temporary unavailable" : ("inconnu " + status);
		_report.log(Report.NIVEAU.DEBUG, "GPS onStatusChanged " + Status);
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		_report.log(Report.NIVEAU.DEBUG, "GPS onProviderEnabled: " + provider);
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		_report.log(Report.NIVEAU.DEBUG, "GPS onStatusDisabled: " + provider);
	}

	/**
	 * Determines whether one Location reading is better than the current Location fix
	 *
	 * @param location            The new Location that you want to evaluate
	 * @param currentBestLocation The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(@NonNull Location location, @Nullable Location currentBestLocation)
	{
		if (currentBestLocation == null)
		{
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > DELTA_TIME;
		boolean isSignificantlyOlder = timeDelta < -DELTA_TIME;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer)
		{
			return true;
			// If the new location is more than two minutes older, it must be worse
		}
		else
			if (isSignificantlyOlder)
			{
				_report.log(Report.NIVEAU.DEBUG, "Position bien plus vieille");
				return false;
			}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate)
		{
			return true;
		}
		else
			if (isNewer && !isLessAccurate)
			{
				return true;
			}
			else
			{
				if (!isNewer)
				{
					_report.log(Report.NIVEAU.DEBUG, "Position plus vieille");
					return false;
				}

				if (isSignificantlyLessAccurate)
				{
					_report.log(Report.NIVEAU.DEBUG, "Position bien moins precise");
					return false;
				}


				return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
			}
	}

	/**
	 * Checks whether two providers are the same
	 */
	private boolean isSameProvider(@Nullable String provider1, @Nullable String provider2)
	{
		if (provider1 == null)
		{
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Rapporte une erreur detectee par le programme
	 *
	 * @param context
	 * @param e
	 */
	@SuppressWarnings("nls")
	public static void Erreur(Context context, @Nullable Exception e)
	{
		String message;//= ""; //$NON-NLS-1$
		if (e == null)
		{
			message = "null exception ?!?"; //$NON-NLS-1$
		}
		else
		{
			StringBuilder stack = new StringBuilder();

			StackTraceElement[] st = e.getStackTrace();
			for (int i = 0; i < st.length; i++)
			{
				String line = st[i].getMethodName() + ":" + st[i].getLineNumber(); //$NON-NLS-1$
				// Log.e(TAG, line);
				stack.append(line);
				stack.append("\n");
			}
			message = e.getLocalizedMessage() + "\n" + stack.toString(); //$NON-NLS-1$
		}

		Toast t = Toast.makeText(context, "Erreur\n" + message + "\n", Toast.LENGTH_LONG); //$NON-NLS-1$
		t.show();

	}

	/***
	 * Service detruit par le systeme
	 * On tente de le relancer si besoin
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		_report.log(Report.NIVEAU.DEBUG, "Service.OnDestroy");
		Context context = getApplicationContext();
		if (ItinerairesDatabase.getInstance(context).getItinerairesEnregistrant() > 0)
			GPSRelaunchReceiver.restartService(getApplicationContext());
	}
}
