package com.lpi.itineraires.details;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.lpi.itineraires.MainActivity;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Position;

public class ClickPositionHandler
{
	public static void handleClickPosition(@NonNull final Marker marker, @NonNull final Position position, @NonNull final Context context, final positionRefreshListener listener)
	{
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle("Marker");
		dialog.setMessage(position.getDescription());
		dialog.setCancelable(true);
		dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Supprimer",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int buttonId)
					{
						// Supprimer cette position
						AlertDialog dialogSupprimer = new AlertDialog.Builder(context).create();
						dialogSupprimer.setTitle("Supprimer? ");
						dialogSupprimer.setMessage("Etes-vous sur de vouloir supprimer cette position?\nOpération non réversible");
						dialogSupprimer.setCancelable(false);
						dialogSupprimer.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(android.R.string.ok),
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int buttonId)
									{
										ItinerairesDatabase database = ItinerairesDatabase.getInstance(context);
										// Supprimer
										if (database.supprime(position))
										{
											messageNotification(context, "Position supprimée");
											if (listener != null)
												listener.onClickPositionHandlerPositionSupprimee(marker, position);
										}
										else
											MainActivity.SignaleErreur("Erreur lors de la suppression de la position", null);

									}
								});
						dialogSupprimer.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(android.R.string.cancel),
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int buttonId)
									{
										// Ne rien faire
									}
								});
						dialogSupprimer.setIcon(android.R.drawable.ic_dialog_alert);
						dialogSupprimer.show();

					}
				});
		dialog.setIcon(android.R.drawable.ic_dialog_info);
		dialog.show();
	}

	private static void messageNotification(Context context, final String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public interface positionRefreshListener
	{
		void onClickPositionHandlerPositionSupprimee(Marker marker, Position pos);
	}
}
