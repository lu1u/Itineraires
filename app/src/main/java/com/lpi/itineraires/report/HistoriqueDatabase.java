package com.lpi.itineraires.report;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Gestionnaire de l'historique
 */
public class HistoriqueDatabase extends ReportDatabase
{
	private static final int MAX_LIGNES = 200;
	@Nullable
	private static HistoriqueDatabase INSTANCE = null;

	private HistoriqueDatabase(Context context)
	{
		super(ReportDatabaseHelper.TABLE_HISTORIQUE, context);
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized HistoriqueDatabase getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new HistoriqueDatabase(context);
		}
		return INSTANCE;
	}

	public void ajoute(int Date, String ligne)
	{
		try
		{
			if (getNbLignes() > MAX_LIGNES)
			{
				// Supprimer les 10 premieres pour eviter que la table des historiques ne grandisse trop
				database.execSQL("DELETE FROM " + ReportDatabaseHelper.TABLE_HISTORIQUE + " WHERE " + ReportDatabaseHelper.COLONNE_HISTORIQUE_ID
						+ " IN (SELECT " + ReportDatabaseHelper.COLONNE_HISTORIQUE_ID + " FROM " + ReportDatabaseHelper.TABLE_HISTORIQUE + " ORDER BY " + ReportDatabaseHelper.COLONNE_HISTORIQUE_ID + " LIMIT 5)");
			}
			ContentValues initialValues = new ContentValues();
			initialValues.put(ReportDatabaseHelper.COLONNE_HISTORIQUE_DATE, Date);
			initialValues.put(ReportDatabaseHelper.COLONNE_HISTORIQUE_LIGNE, ligne);

			database.insert(ReportDatabaseHelper.TABLE_HISTORIQUE, null, initialValues);
		} catch (Exception e)
		{
			//MainActivity.SignaleErreur("ajout d'une ligne d'historique", e);
			e.printStackTrace();
		}
	}

	public Cursor getCursor()
	{
		return database.query(ReportDatabaseHelper.TABLE_HISTORIQUE, null, null, null, null, null, ReportDatabaseHelper.COLONNE_HISTORIQUE_ID + " DESC");
	}


}
