package com.lpi.itineraires;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Itineraire;
import com.lpi.itineraires.utils.Utils;

public class EditRandoActivity extends AppCompatActivity
{
	public static final int RESULT_EDIT_RANDO = 1;
	public static final String ACTION_EDIT_RANDO_FINISHED = EditRandoActivity.class.getCanonicalName() + ".EDITERANDO";
	public static final String EXTRA_OPERATION = EditRandoActivity.class.getCanonicalName() + ".OPERATION";
	public static final String EXTRA_OPERATION_AJOUTE = EditRandoActivity.class.getCanonicalName() + ".AJOUTE";
	public static final String EXTRA_OPERATION_MODIFIE = EditRandoActivity.class.getCanonicalName() + ".MODIFIE";
	private Spinner _spType;
	@Nullable
	private Itineraire _itineraire;
	@Nullable
	String _operation;
	EditText _eNom;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demarre l'activity pour modifier un itineraire
	 *
	 * @param activity, l'activity qui va recevoir la notification de fin de l'edition
	 * @param  itineraire
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	public static void startForEdit(@NonNull final Activity activity, @Nullable final Itineraire itineraire)
	{
		if (itineraire != null)
		{
			Intent intent = new Intent(activity, EditRandoActivity.class);
			Bundle b = new Bundle();
			itineraire.toBundle(b);
			b.putString(EditRandoActivity.EXTRA_OPERATION, EditRandoActivity.EXTRA_OPERATION_MODIFIE);
			intent.putExtras(b);
			activity.startActivityForResult(intent, RESULT_EDIT_RANDO);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creation de l'activity
	 *
	 * @param savedInstanceState
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		Utils.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_itineraire);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				onOK();
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		_eNom = findViewById(R.id.editTextNom);
		_spType = findViewById(R.id.spinnerTypeRando);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.types_itineraires, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		_spType.setAdapter(adapter);

		if (savedInstanceState == null)
			savedInstanceState = this.getIntent().getExtras();
		if (savedInstanceState != null)
		{
			_itineraire = new Itineraire(savedInstanceState);
			_operation = savedInstanceState.getString(EXTRA_OPERATION);
		}
		else

		{
			_itineraire = new Itineraire();
			_itineraire.Nom = this.getResources().getString(R.string.rando_sans_nom, ItinerairesDatabase.getInstance(this).nbItineraires() + 1);
			_operation = EXTRA_OPERATION_AJOUTE;
		}

		MajUI();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Mise a jour de l'interface
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	private void MajUI()
	{
		if (_itineraire != null)
		{
			_eNom.setText(_itineraire.Nom);
			_spType.setSelection(Itineraire.typeToInt(_itineraire.Type));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creation du menu de l'activity
	 *
	 * @param menu
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_dialog_box, menu);
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Selection dans le menu de l'activity
	 *
	 * @param item
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(@Nullable MenuItem item)
	{
		if (item != null)
			switch (item.getItemId())
			{
				case R.id.buttonOK:
					onOK();
					return true;

				case android.R.id.home:
					onAnnuler();
					return true;

				default:
			}
		// If we got here, the user's action was not recognized.
		// Invoke the superclass to handle it.
		return super.onOptionsItemSelected(item);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * OK: fermer l'ecran et renvoyer les donnees
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void onOK()
	{
		Intent returnIntent = new Intent();
		returnIntent.setAction(ACTION_EDIT_RANDO_FINISHED);
		returnIntent.putExtra("result", RESULT_EDIT_RANDO);

		if (_itineraire == null)
			_itineraire = new Itineraire();

		_itineraire.Nom = _eNom.getText().toString();
		_itineraire.Type = Itineraire.intToType(_spType.getSelectedItemPosition());

		boolean erreur = false;

		if (displayError("".equals(_itineraire.Nom), _eNom, "Donnez un nom à votre randonée"))
			erreur = true;

		if (erreur)
			return;


		Bundle bundle = new Bundle();
		_itineraire.toBundle(bundle);

		bundle.putString(EXTRA_OPERATION, _operation);
		returnIntent.putExtras(bundle);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Affiche une erreur en mettant l'accent sur le champ concerné
	 * @param error
	 * @param v
	 * @param message
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean displayError(boolean error, @NonNull View v, @NonNull String message)
	{
		if (error)
		{
			if (v instanceof EditText)
			{
				((TextView) v).setError(message);
			}
			else
			{
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				com.lpi.itineraires.MainActivity.MessageNotification(v, message);
			}
		}
		else
		{
			if (v instanceof TextView)
				((TextView) v).setError(null);
		}


		return error;
	}

	public void onAnnuler()
	{
		Intent returnIntent = new Intent();
		setResult(Activity.RESULT_CANCELED, returnIntent);
		finish();
	}
}
