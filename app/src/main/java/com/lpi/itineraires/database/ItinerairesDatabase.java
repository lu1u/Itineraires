package com.lpi.itineraires.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lpi.itineraires.MainActivity;
import com.lpi.itineraires.itineraire.Itineraire;
import com.lpi.itineraires.itineraire.Position;
import com.lpi.itineraires.utils.Report;


/**
 * Created by lucien on 26/01/2016.
 */
@SuppressWarnings("ALL")
public class ItinerairesDatabase extends GenericDatabase
{
	static public final String TAG = "ItinerairesDatabase";

	private ItinerairesDatabase(@NonNull Context context)
	{
		try
		{
			_report = Report.getInstance(context);
			_report.log(Report.NIVEAU.DEBUG, "Creation ItinerairesDatabase");
			_dbHelper = new DatabaseHelper(context);
			_database = _dbHelper.getWritableDatabase();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized ItinerairesDatabase getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ItinerairesDatabase(context);
		}
		return (ItinerairesDatabase) INSTANCE;
	}

	public void dump()
	{
		// Table RANDOS
		Cursor c = getCursor(DatabaseHelper.TABLE_ITINERAIRE);
		if (c != null)
		{
			final int id = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_ID);
			final int nom = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_NOM);
			final int type = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_TYPE);
			final int creation = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_CREATION);
			final int debut = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_DEBUT);
			final int fin = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_FIN);
			final int enregistre = c.getColumnIndex(DatabaseHelper.COLONNE_ITI_ENREGISTRE);
			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, DatabaseHelper.TABLE_ITINERAIRE);
			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, "Id, Nom, Type, Creation, Debut, Fin, Enregistre");
			while (c.moveToNext())
			{
				Log.d(TAG, c.getInt(id) + "," + c.getString(nom) + "," + c.getInt(type) + "," + c.getLong(creation) + "," + c.getLong(debut) + "," + c.getLong(fin) + "," + c.getInt(enregistre));
			}
			c.close();
			Log.d(TAG, "-----------------------------------------------");
		}

		// Table positions
		c = getCursor(DatabaseHelper.TABLE_POSITIONS);
		if (c != null)
		{
			final int id = c.getColumnIndex(DatabaseHelper.COLONNE_POS_IDRANDO);
			final int latitude = c.getColumnIndex(DatabaseHelper.COLONNE_POS_LATITUDE);
			final int longitude = c.getColumnIndex(DatabaseHelper.COLONNE_POS_LONGITUDE);
			final int altitude = c.getColumnIndex(DatabaseHelper.COLONNE_POS_ALTITUDE);
			final int vitesse = c.getColumnIndex(DatabaseHelper.COLONNE_POS_VITESSE);
			final int bearing = c.getColumnIndex(DatabaseHelper.COLONNE_POS_BEARING);
			final int precision = c.getColumnIndex(DatabaseHelper.COLONNE_POS_ACCURACY);
			final int provider = c.getColumnIndex(DatabaseHelper.COLONNE_POS_PROVIDER);

			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, DatabaseHelper.TABLE_POSITIONS);
			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, "Id, Latitude, Longitude, Altitude, Vitesse, Bearing, Precision, Provider");
			while (c.moveToNext())
			{
				Log.d(TAG, c.getInt(id) + "," + c.getFloat(latitude) + "," + c.getFloat(longitude) + "," + c.getFloat(altitude) + "," + c.getFloat(vitesse) + "," + c.getFloat(bearing) + "," + c.getFloat(precision) + "," + c.getString(provider));
			}
			c.close();
			Log.d(TAG, "-----------------------------------------------");
		}

		// Table PREFERENCES_INT
		c = getCursor(DatabaseHelper.TABLE_PREFERENCES_INT);
		if (c != null)
		{
			final int name = c.getColumnIndex(DatabaseHelper.COLONNE_PREF_INT_NAME);
			final int valeur = c.getColumnIndex(DatabaseHelper.COLONNE_PREF_INT_VALEUR);

			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, DatabaseHelper.TABLE_PREFERENCES_INT);
			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, "Name, Value");
			while (c.moveToNext())
			{
				Log.d(TAG, c.getString(name) + "," + c.getInt(valeur));
			}
			c.close();
			Log.d(TAG, "-----------------------------------------------");

		}

		// Table PREFERENCES_STRING
		c = getCursor(DatabaseHelper.TABLE_PREFERENCES_STRING);
		if (c != null)
		{
			final int name = c.getColumnIndex(DatabaseHelper.COLONNE_PREF_STRING_NAME);
			final int valeur = c.getColumnIndex(DatabaseHelper.COLONNE_PREF_STRING_VALEUR);

			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, DatabaseHelper.TABLE_PREFERENCES_STRING);
			Log.d(TAG, "-----------------------------------------------");
			Log.d(TAG, "Name, Value");
			while (c.moveToNext())
			{
				Log.d(TAG, c.getString(name) + "," + c.getString(valeur));
			}
			c.close();
			Log.d(TAG, "-----------------------------------------------");

		}
	}

	@Override
	public void finalize()
	{
		_dbHelper.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * ajoute une randonnee
	 *
	 * @param itineraire
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void ajoute(@NonNull Itineraire itineraire)
	{
		try
		{
			ContentValues initialValues = new ContentValues();
			itineraire.toContentValues(initialValues, false);
			int id = (int) _database.insert(DatabaseHelper.TABLE_ITINERAIRE, null, initialValues);
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur ajout itineraire");
			_report.log(Report.NIVEAU.ERROR, e);
			MainActivity.SignaleErreur("Ajout de la itineraire", e);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Ajoute une position
	 * @param position
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void ajoute(@NonNull Position position)
	{
		try
		{
			ContentValues initialValues = new ContentValues();
			position.toContentValues(initialValues);
			int id = (int) _database.insert(DatabaseHelper.TABLE_POSITIONS, null, initialValues);
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur ajout position");
			_report.log(Report.NIVEAU.ERROR, e);
			MainActivity.SignaleErreur("ajout de la position", e);
		}
	}

	/***
	 * Retourne une randonnee cree a partir de la base de donnnees
	 *
	 * @param Id
	 * @return profil
	 */
	public @Nullable
	Itineraire getItineraire(int Id)
	{
		Itineraire profil = null;
		Cursor cursor = null;
		try
		{
			String[] colonnes = null;
			String where = DatabaseHelper.COLONNE_ITI_ID + " = " + Id;
			cursor = _database.query(DatabaseHelper.TABLE_ITINERAIRE, colonnes, where, null, null, null, null);
			cursor.moveToFirst();
			profil = new Itineraire(cursor);
			return profil;
		} catch (SQLException e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur lecture rando, id=" + Id);
			_report.log(Report.NIVEAU.ERROR, e);
			MainActivity.SignaleErreur("Impossible de lire la rando dans la base de donnee", e);
		} finally
		{
			if (cursor != null)
				cursor.close();
		}

		return profil;
	}

	public void modifie(@NonNull Itineraire itineraire)
	{
		try
		{
			ContentValues valeurs = new ContentValues();
			itineraire.toContentValues(valeurs, true);
			//_database.beginTransactionNonExclusive();
			_database.update(DatabaseHelper.TABLE_ITINERAIRE, valeurs, DatabaseHelper.COLONNE_ITI_ID + " = " + itineraire.Id, null);
			// _database.setTransactionSuccessful();
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur modification itineraire, id=" + itineraire.Id);
			_report.log(Report.NIVEAU.ERROR, e);
			MainActivity.SignaleErreur("modification du profil", e);
		} finally
		{
			//_database.endTransaction();
		}
	}

	/***
	 * Supprime une transaction et ses positions
	 * @param rando
	 * @return true si operation ok, false si erreur
	 */
	public boolean supprime(int Id)
	{
		try
		{
			// Operation effectuee dans une transaction sqlite pour garantir la coherence de la base
			_database.beginTransaction();
			// supprimer les positions associees a cette rando
			_database.delete(DatabaseHelper.TABLE_POSITIONS, DatabaseHelper.COLONNE_POS_IDRANDO + " = " + Id, null);
			// supprimer la rando
			_database.delete(DatabaseHelper.TABLE_ITINERAIRE, DatabaseHelper.COLONNE_ITI_ID + " = " + Id, null);
			_database.setTransactionSuccessful();
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur suppresion rando, id=" + Id);
			_report.log(Report.NIVEAU.ERROR, e);
			_database.endTransaction();
			return false;
		}

		_database.endTransaction();
		return true;
	}

	/***
	 * Supprime une position
	 * @param position a supprimer
	 * @return true si operation ok, false si erreur
	 */
	public boolean supprime(Position position)
	{
		try
		{
			// Operation effectuee dans une transaction sqlite pour garantir la coherence de la base
			_database.beginTransaction();
			// supprimer les positions associees a cette rando
			_database.delete(DatabaseHelper.TABLE_POSITIONS,
					DatabaseHelper.COLONNE_POS_IDRANDO + " = " + position.IdRandonnee +
							" AND " + DatabaseHelper.COLONNE_POS_TEMPS + " = " + position.getTime() +
							" AND " + DatabaseHelper.COLONNE_POS_PROVIDER + " = \"" + position.getProvider() + "\"", null);

			_database.setTransactionSuccessful();
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, e);
			_database.endTransaction();
			return false;
		}

		_database.endTransaction();
		return true;
	}

	public @Nullable
	Cursor getCursor()
	{
		try
		{
			return _database.query(DatabaseHelper.TABLE_ITINERAIRE, null, null, null, null, null, null);
		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur getCursor Randos");
			_report.log(Report.NIVEAU.ERROR, e);
		}

		return null;
	}

	/***
	 * Retourne le nombre de randonnees dans la base
	 * @return
	 */
	public int nbItineraires()
	{
		Cursor cursor = _database.rawQuery("SELECT COUNT (*) FROM " + DatabaseHelper.TABLE_ITINERAIRE, null);
		int count = 0;
		if (null != cursor)
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				count = cursor.getInt(0);
			}
		cursor.close();

		return count;
	}

	/***
	 * Retourne le nombre de randonnees en cours d'enregistrement
	 * @return
	 */
	public int getNbItinerairesEnregistrant()
	{
		Cursor cursor = _database.rawQuery("SELECT COUNT (*) FROM " + DatabaseHelper.TABLE_ITINERAIRE + " WHERE " + DatabaseHelper.COLONNE_ITI_ENREGISTRE + " <> 0", null);
		int count = 0;
		if (null != cursor)
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				count = cursor.getInt(0);
			}
		cursor.close();

		return count;
	}

	public @Nullable
	Cursor getCursorItinerairesEnregistrant()
	{
		return _database.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ITINERAIRE + " WHERE " + DatabaseHelper.COLONNE_ITI_ENREGISTRE + " <> 0", null);
	}

	public @Nullable
	Cursor getItinerairesIdEnregistrant()
	{
		return _database.rawQuery("SELECT " + DatabaseHelper.COLONNE_ITI_ID + " FROM " + DatabaseHelper.TABLE_ITINERAIRE + " WHERE " + DatabaseHelper.COLONNE_ITI_ENREGISTRE + " <> 0", null);
	}

	/***
	 * Retourne le nombre de positions associees a un itineraire
	 * @param id
	 * @return
	 */
	public int getNbPositions(int id)
	{
		Cursor cursor = _database.rawQuery("SELECT COUNT (*) FROM " + DatabaseHelper.TABLE_POSITIONS + " WHERE " + DatabaseHelper.COLONNE_POS_IDRANDO + " == " + id, null);
		int count = 0;
		if (null != cursor)
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				count = cursor.getInt(0);
			}
		cursor.close();

		return count;
	}

	public @Nullable
	Cursor getPositions(int randoId)
	{
		return _database.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_POSITIONS + " WHERE " + DatabaseHelper.COLONNE_POS_IDRANDO + " == " + randoId + " ORDER BY " + DatabaseHelper.COLONNE_POS_TEMPS, null);
	}


	public boolean supprimePositions(int id)
	{
		try
		{
			// supprimer les positions associees a cette rando
			_database.delete(DatabaseHelper.TABLE_POSITIONS, DatabaseHelper.COLONNE_POS_IDRANDO + " = " + id, null);

		} catch (Exception e)
		{
			_report.log(Report.NIVEAU.ERROR, "Erreur suppresion rando, id=" + id);
			_report.log(Report.NIVEAU.ERROR, e);
			return false;
		}
		return true;
	}

}
