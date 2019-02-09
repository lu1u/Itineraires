package com.lpi.itineraires;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Itineraire;
import com.lpi.itineraires.itineraire.Position;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class ExportItineraire
{
	static int typeExport = R.id.radioButtonFormatTexte;

	public static void start(final Activity activity, final int idItineraire)
	{
		if ( ! checkPermissions(activity))
			return;

		final Itineraire itineraire = ItinerairesDatabase.getInstance(activity).getItineraire(idItineraire);
		final Dialog dialog = new Dialog(activity);
		dialog.setContentView(R.layout.dialog_export);
		dialog.setTitle("Title...");

		final EditText text = dialog.findViewById(R.id.editTextNomFichier);
		Button btnAnnuler = dialog.findViewById(R.id.buttonCancel);
		Button btnOK = dialog.findViewById(R.id.buttonOK);
		RadioGroup rg = dialog.findViewById(R.id.radioGroupFormat);
		RadioButton rgTexte = dialog.findViewById(R.id.radioButtonFormatTexte);
		RadioButton rgCSV = dialog.findViewById(R.id.radioButtonFormatCSV);
		RadioButton rgXML = dialog.findViewById(R.id.radioButtonFormatXML);
		text.setText(itineraire.Nom);

		btnAnnuler.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});

		btnOK.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String nom = text.getText().toString();
				exporter(activity, nom, itineraire);
				dialog.dismiss();
			}
		});

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(final RadioGroup group, final int checkedId)
			{
				typeExport = checkedId;
			}
		});

		dialog.show();
	}

	private static boolean checkPermissions(final Activity activity)
	{
		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
			return true;

		String[] p = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		ActivityCompat.requestPermissions(activity, p, 0);
		return false;
	}

	private static void exporter(Context context, String nom, Itineraire itineraire)
	{
		switch (typeExport)
		{
			case R.id.radioButtonFormatTexte:
				exportTexte(context, nom, itineraire);
				break;
			case R.id.radioButtonFormatCSV:
				exportCSV(nom, itineraire);
				break;
			case R.id.radioButtonFormatXML:
				exportXML(nom, itineraire);

		}
	}

	private static void exportXML(final String nom, final Itineraire itineraire)
	{
	}

	private static void exportCSV(final String nom, final Itineraire itineraire)
	{

	}

	private static void exportTexte(Context context, final String nom, final Itineraire itineraire)
	{
		final String state = Environment.getExternalStorageDirectory().getPath();
		String file = state + "/" + nom + ".txt";

		try
		{
			FileWriter stream = new FileWriter(file);

			BufferedWriter bw = new BufferedWriter(stream);
			Cursor c = ItinerairesDatabase.getInstance(context).getPositions(itineraire.Id);
			if (null != c)
			{
				bw.write("ID, LATITUDE, LONGITUDE, ALTITUDE, TEMPS, DATE, HEURE, VITESSE, PROVIDER, ACCURACY, BEARING\n");
				while (c.moveToNext())
				{
					Position position = new Position(c);
					bw.write( "" +
							position.IdRandonnee
							+ ","  + position.Latitude
							+ ", " + position.Longitude
							+ ", " + position.Altitude
							+ ", " + position.Temps
							+ ", " + formateDate(position.Temps)
							+ ", " + formateHeure(position.Temps)
							+ ", " + position.Vitesse
							+ ", " + position.Provider
							+ ", " + position.Accurary
							+ ", " + position.Bearing
							+"\n");

				}
			}
			stream.close();
		} catch (Exception e)
		{
		}

	}

	private static String formateDate(final long temps)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(temps);

		return new SimpleDateFormat("dd/MMM/yyyy").format(cal.getTime());
	}

	private static String formateHeure(final long temps)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(temps);

		return new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
	}
}
