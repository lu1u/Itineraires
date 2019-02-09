package com.lpi.itineraires;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Itineraire;
import com.lpi.itineraires.itineraire.ItineraireAdapter;
import com.lpi.itineraires.report.ReportActivity;
import com.lpi.itineraires.utils.Report;
import com.lpi.itineraires.utils.Utils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
	public static final String TAG = "SuiviRando";
	static public final int RESULT_EDIT_RANDO = 0;
	@Nullable
	private ItineraireAdapter _adapterRandos;
	private int _currentItemSelected = 0;

	/***
	 * Creation de l'activity et de son contenu
	 * @param savedInstanceState
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//_applicationActivity = this;
		Utils.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, EditRandoActivity.class);
				startActivityForResult(intent, RESULT_EDIT_RANDO);
			}
		});

		InitItineraires();

		// Les permissions ont ete verifiee au demarrage de l'application
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			String[] p = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
			ActivityCompat.requestPermissions(this, p, 0);
		}
	}


	/***
	 * Initialisation de la liste des profils a partir de la base de donnees
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	private void InitItineraires()
	{
		ListView listView = findViewById(R.id.listView);
		listView.setEmptyView(findViewById(R.id.textViewEmpty));

		_adapterRandos = new ItineraireAdapter(this, ItinerairesDatabase.getInstance(this).getCursor());
		listView.setAdapter(_adapterRandos);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id)
			{
				view.setSelected(true);
				_currentItemSelected = position;
				parent.showContextMenuForChild(view);
			}
		});
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}


	/***
	 * Menu principal
	 * @param item
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id)
		{
			case R.id.action_settings:
				startActivity(new Intent(this, PreferencesActivity.class));
				break;

			case R.id.action_database:
				ItinerairesDatabase.getInstance(this).dump();
				break;

			case R.id.action_rapport:
				startActivity(new Intent(this, ReportActivity.class));
				break;

			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	/***
	 * Signaler une erreur
	 *
	 * @param message
	 * @param e
	 */

	static public void SignaleErreur(String message, @NonNull Exception e)
	{
/*	LayoutInflater inflater = _applicationActivity.getLayoutInflater();
    View layout = inflater.inflate(R.layout.layout_toast_erreur,
			(ViewGroup) _applicationActivity.findViewById(R.id.layoutRoot));

	TextView tv = (TextView) layout.findViewById(R.id.textViewTextErreur);
	String m = String.format(tv.getText().toString(), message);
	tv.setText(m);

	m = e.getLocalizedMessage();
	int nbMax = 0;
	for (StackTraceElement s : e.getStackTrace())
	{
		m += "\n" + (s.getClassName() + '/' + s.getMethodName() + ':' + s.getLineNumber());
		nbMax++;
		if (nbMax > 2)
			break;
	}

	((TextView) layout.findViewById(R.id.textViewStackTrace)).setText(m);
	Toast toast = new Toast(_applicationActivity.getApplicationContext());
	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	toast.setDuration(Toast.LENGTH_LONG);
	toast.setView(layout);
	toast.show();      */
		//Toast.makeText(_applicationActivity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
		//Report.getInstance(_applicationActivity).log(Report.NIVEAU.ERROR, e);
	}

	public static void MessageNotification(@NonNull View v, @NonNull String message)
	{
		Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
	}

	/**
	 * Dispatch incoming result to the correct fragment.
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED)
			return;

		switch (requestCode)
		{
			case RESULT_EDIT_RANDO:
				if (data != null)
					onEditItineraire(data);
				break;
		}
	}

	/***
	 * Reception du résultat de l'activite d'edition d'un profil
	 *
	 * @param data
	 */
	private void onEditItineraire(Intent data)
	{
		String Operation = data.getExtras().getString(EditRandoActivity.EXTRA_OPERATION);
		Itineraire itineraire = new Itineraire(data.getExtras());

		ItinerairesDatabase database = ItinerairesDatabase.getInstance(this);
		if (EditRandoActivity.EXTRA_OPERATION_AJOUTE.equals(Operation))
		{
			// Ajouter le profil
			itineraire.DateCreation = (int) Calendar.getInstance().getTimeInMillis();
			database.ajoute(itineraire);
			_adapterRandos.changeCursor(database.getCursor());
			_currentItemSelected = -1;

			Report.getInstance(this).historique("Ajout '" + itineraire.Nom + "', Creation: " + itineraire.DateCreation);

		}
		else
			if (EditRandoActivity.EXTRA_OPERATION_MODIFIE.equals(Operation))
			{
				// Modifier le profil
				database.modifie(itineraire);
				_adapterRandos.changeCursor(database.getCursor());
				Report.getInstance(this).historique("Modification '" + itineraire.Nom);
			}
	}

	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.listView)
		{
			if (_currentItemSelected != -1)
			{
				Itineraire selectedItem = _adapterRandos.get(_currentItemSelected);
				if (selectedItem != null)
				{
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.menu_liste, menu);
					menu.setHeaderTitle(selectedItem.Nom);

					boolean details = (!selectedItem.Enregistre) && (ItinerairesDatabase.getInstance(this).getNbPositions(selectedItem.Id) > 0);

					menu.findItem(R.id.action_modifier).setEnabled(!selectedItem.Enregistre);
					menu.findItem(R.id.action_supprimer).setEnabled(!selectedItem.Enregistre);
					menu.findItem(R.id.action_details).setEnabled(details);
					//menu.findItem(R.id.action_carte).setEnabled(details );
					menu.findItem(R.id.action_exporter).setEnabled(details);
				}
			}
		}
	}

	@Override
	/***
	 * Choix d'un item dans le menu contextuel
	 */
	public boolean onContextItemSelected(@NonNull MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_modifier:
				EditRandoActivity.startForEdit(this, _adapterRandos.get(_currentItemSelected));
				return true;
			case R.id.action_supprimer:
				Supprime();
				return true;

			case R.id.action_details:
//				CourbeActivity.start(this, _adapterRandos.get(_currentItemSelected).Id );
//				return true;
//			case R.id.action_carte:
				MapsActivity.start(this, _adapterRandos.get(_currentItemSelected).Id);
				return true;

			case R.id.action_exporter:
				ExportItineraire.start(this, _adapterRandos.get(_currentItemSelected).Id);
				return true;


			default:
				return super.onContextItemSelected(item);
		}
	}

	private void Supprime()
	{
		if (_currentItemSelected == -1)
			return;

		final Itineraire objetASupprimer = _adapterRandos.get(_currentItemSelected);
		if (objetASupprimer != null)
		{
			final ItinerairesDatabase database = ItinerairesDatabase.getInstance(MainActivity.this);
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Supprimer");
			dialog.setMessage(getResources().getString(R.string.supprimer_itineraire, objetASupprimer.Nom, database.getNbPositions(objetASupprimer.Id)));
			dialog.setCancelable(false);
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int buttonId)
						{
							Report.getInstance(MainActivity.this).historique("Suppression de la randonnée " + objetASupprimer.Nom + ", id=" + objetASupprimer.Id);

							// Supprimer
							if (database.supprime(objetASupprimer.Id))
								MessageNotification(findViewById(R.id.listView), "Itinéraire " + objetASupprimer.Nom + " supprimé");
							else
								SignaleErreur("Erreur lors de la suppression de " + objetASupprimer.Nom + ", itinéraire non supprimé", null);
							_adapterRandos.changeCursor(database.getCursor());
							_currentItemSelected = -1;
						}
					});
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int buttonId)
						{
							// Ne rien faire
						}
					});
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
			dialog.show();
		}
	}


	/**
	 * Dispatch onResume() to fragments.  Note that for better inter-operation
	 * with older versions of the platform, at the point of this call the
	 * fragments attached to the activity are <em>not</em> resumed.  This means
	 * that in some cases the previous state may still be saved, not allowing
	 * fragment transactions that modify the state.  To correctly interact
	 * with fragments in their proper state, you should instead override
	 * {@link #onResumeFragments()}.
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		_adapterRandos.changeCursor(ItinerairesDatabase.getInstance(this).getCursor());
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}
}
