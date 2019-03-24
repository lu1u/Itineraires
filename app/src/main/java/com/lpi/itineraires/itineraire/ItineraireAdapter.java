package com.lpi.itineraires.itineraire;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lpi.itineraires.GPS.GPSService;
import com.lpi.itineraires.GPSTrackingNotification;
import com.lpi.itineraires.MainActivity;
import com.lpi.itineraires.R;
import com.lpi.itineraires.database.DatabaseHelper;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.utils.Report;

import java.util.Calendar;


/**
 * Adapter pour afficher les Itineraires
 */
public class ItineraireAdapter extends CursorAdapter
{
	private final Context _context;

	public ItineraireAdapter(Context context, Cursor cursor)
	{
		super(context, cursor, 0);
		_context = context;
	}

	// The newView method is used to inflate a new view and return it,
	// you don't bind any data to the view at this point.
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return LayoutInflater.from(context).inflate(R.layout.element_liste_itineraire, parent, false);
	}

	// The bindView method is used to bind all data to a given view
	// such as setting the text on a TextView.
	@Override
	public void bindView(@NonNull final View view, final Context context, Cursor cursor)
	{

		// Find fields to populate in inflated template
		final Itineraire itineraire = new Itineraire(cursor);

		view.findViewById(R.id.layoutFond).setBackgroundColor(TypeItineraire.couleur(context, itineraire.Type));
		((TextView) view.findViewById(R.id.textViewNom)).setText(itineraire.Nom);
		((TextView) view.findViewById(R.id.textViewType)).setText(TypeItineraire.getTexteType(itineraire.Type));
		((TextView) view.findViewById(R.id.textViewDescription)).setText(itineraire.getDescription(_context, false));

		final View btnRecord = view.findViewById(R.id.imageViewRecord);
		final View btnStop = view.findViewById(R.id.imageViewStop);
		btnRecord.setVisibility(itineraire.Enregistre ? View.GONE : View.VISIBLE);
		btnStop.setVisibility(itineraire.Enregistre ? View.VISIBLE : View.GONE);

		btnStop.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				stoppeEnregistrement(view, itineraire.Id);
			}
		});

		btnRecord.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				demarreEnregistrementSiConfirme(view, itineraire.Id);
			}

		});
	}

	/***
	 * Verifie si un itineraire a deja des positions avant de demarrer son enregistrement
	 * @param view
	 * @param id
	 */
	private void demarreEnregistrementSiConfirme(@NonNull final View view, final int id)
	{
		final ItinerairesDatabase database = ItinerairesDatabase.getInstance(_context);
		// Relire la rando
		final Itineraire rnd = database.getItineraire(id);

		if (rnd != null)
		{
			if (database.getNbPositions(id) == 0)
			{
				// Pas besoin de confirmation
				demarreEnregistrement(view, rnd);
			}
			else
			{
				// La randonnee a deja des positions, confirmer le redemarrage
				AlertDialog dialog = new AlertDialog.Builder(_context).create();
				dialog.setTitle("Redémarrage");
				dialog.setMessage("La randonnée " + rnd.Nom + " a déjà des positions enregistrées, voulez-vous les effacer et redemarrer l'enregistrement ?");
				dialog.setCancelable(false);
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, _context.getResources().getString(android.R.string.ok),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int buttonId)
							{
								database.supprimePositions(id);
								demarreEnregistrement(view, rnd);
							}
						});
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, _context.getString(android.R.string.cancel),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int buttonId)
							{
								// Ne rien faire
							}
						});
				dialog.setIcon(android.R.drawable.ic_dialog_info);
				dialog.show();
			}
		}
	}

	private void demarreEnregistrement(@NonNull View view, Itineraire itineraire)
	{
		MainActivity.MessageNotification(view, _context.getString(R.string.debut_enregistrement, itineraire.Nom));
		Report.getInstance(_context).historique("Debut enregistrement " + itineraire.Nom);

		// Demarre l'enregistrement
		itineraire.DateDebut = Calendar.getInstance().getTimeInMillis();
		itineraire.Enregistre = true;
		ItinerairesDatabase.getInstance(_context).modifie(itineraire);
		GPSService.update(view.getContext());

		GPSTrackingNotification.notify(_context, "Enregistrement en cours", 1);
		// Maj de l'interface utilisateur
		((TextView) view.findViewById(R.id.textViewDescription)).setText(itineraire.getDescription(_context, false));
		final View btnRecord = view.findViewById(R.id.imageViewRecord);
		final View btnStop = view.findViewById(R.id.imageViewStop);

		btnRecord.setAnimation(AnimationUtils.loadAnimation(_context, R.anim.alpha_out));
		btnRecord.setVisibility(View.GONE);

		btnStop.setAnimation(AnimationUtils.loadAnimation(_context, R.anim.animation_bouton_stop));
		btnStop.setVisibility(View.VISIBLE);
	}

	private void stoppeEnregistrement(@NonNull View view, int id)
	{
		ItinerairesDatabase database = ItinerairesDatabase.getInstance(_context);
		// Relire la rando
		Itineraire rnd = database.getItineraire(id);
		if (rnd != null)
		{
			MainActivity.MessageNotification(view, _context.getString(R.string.arret_enregistrement, rnd.Nom));
			Report.getInstance(_context).historique("Fin enregistrement " + rnd.Nom);

			// Stoppe l'enregistrement
			rnd.DateFin = Calendar.getInstance().getTimeInMillis();
			rnd.Enregistre = false;
			database.modifie(rnd); // Supprimer l'etat AVANT de stopper le service, sinon il va tenter de se relancer
			GPSService.update(_context);
			if (database.getNbItinerairesEnregistrant() == 0)
				GPSTrackingNotification.cancel(_context);


			// Maj de l'interface utilisateur
			((TextView) view.findViewById(R.id.textViewDescription)).setText(rnd.getDescription(_context, false));
			final View btnRecord = view.findViewById(R.id.imageViewRecord);
			final View btnStop = view.findViewById(R.id.imageViewStop);
			btnStop.setAnimation(AnimationUtils.loadAnimation(_context, R.anim.alpha_out));
			btnStop.setVisibility(View.GONE);
			btnRecord.setAnimation(AnimationUtils.loadAnimation(_context, R.anim.alpha_in));
			btnRecord.setVisibility(View.VISIBLE);
		}
	}

	@Nullable
	public Itineraire get(int position)
	{
		try
		{
			Cursor cursor = getCursor();

			if (cursor.moveToPosition(position))
				return new Itineraire(cursor);
		} catch (Exception e)
		{
			Report.getInstance(_context).log(Report.NIVEAU.ERROR, e);
		}
		return null;
	}

	@Nullable
	public int getId(int position)
	{
		Cursor cursor = getCursor();

		if (cursor.moveToPosition(position))
		{
			return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ITI_ID));
		}
		return ItinerairesDatabase.INVALID_ID;
	}
}