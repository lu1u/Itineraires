package com.lpi.itineraires.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.lpi.itineraires.R;
import com.lpi.itineraires.utils.Report;

public class PermissionsActivity extends AppCompatActivity
{
	public static final String PARAMETRE_PERMISSIONS_ARRAY_ID = "permissions_array_id";
	public static final String PARAMETRE_DESCRIPTIONS_ARRAY_ID = "descriptions_array_id";
	private static final String PARAMETRE_REQUEST_CODE = "permissions_request_code";

	private static final int REQUEST_CODE = 1;

	ListView _liste;
	int _idArrayPermissions;
	int _requestCode;

	/***
	 * Verifie si toutes les permissions Android requises par l'application lui sont accordées
	 * Si non, ouvrir l'écran de demande des autorisations
	 * @param activity
	 * @param arrayId
	 * @param descriptionsArrayId
	 * @param requestCode
	 * @return
	 */
	static public boolean checkPermissions(@NonNull final Activity activity, int arrayId, int descriptionsArrayId, int requestCode)
	{
		if (!checkPermissions(activity, arrayId))
		{
			// Demarrer l'activity qui va permettre d'accorder les permissions
			Intent intent = new Intent(activity, PermissionsActivity.class);
			intent.putExtra(PARAMETRE_PERMISSIONS_ARRAY_ID, arrayId);
			intent.putExtra(PARAMETRE_DESCRIPTIONS_ARRAY_ID, descriptionsArrayId);
			intent.putExtra(PARAMETRE_REQUEST_CODE, requestCode);
			activity.startActivityForResult(intent, requestCode);
		}
		return true;
	}

	/***
	 * Verifier que toutes les permissions demandées ont bien été accordées à l'application
	 * @param context
	 * @param arrayId
	 * @return
	 */
	private static boolean checkPermissions(Context context, final int arrayId)
	{
		String[] stringArray = context.getResources().getStringArray(arrayId);
		for (String s : stringArray)
		{
			if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED)
			{
				Report.getInstance(context).log(Report.NIVEAU.WARNING, "Permission " + s + " non accordée");
				return false;
			}
		}

		// Toutes les permissions ont été vérifiées
		return true;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permissions);

		_idArrayPermissions = getIntent().getIntExtra(PARAMETRE_PERMISSIONS_ARRAY_ID, R.array.permissions);
		_requestCode = getIntent().getIntExtra(PARAMETRE_REQUEST_CODE, 0);
		int descriptionId = getIntent().getIntExtra(PARAMETRE_DESCRIPTIONS_ARRAY_ID, R.array.description_permissions);


		//CustomAdapter adapter = new CustomAdapter(this, getResources().getStringArray(arrayId), getResources().getStringArray(descriptionId));
		PermissionsAdapter adapter = new PermissionsAdapter(this, _idArrayPermissions, descriptionId);
		_liste = findViewById(R.id.idListe);
		_liste.setAdapter(adapter);
	}

	public void onClickAccorderPermissions(View v)
	{
		String[] permissions = getResources().getStringArray(_idArrayPermissions);
		requestPermissions(permissions, REQUEST_CODE);
	}

	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		switch (requestCode)
		{
			case REQUEST_CODE:
			{
				if (checkPermissions(this, _idArrayPermissions))
				{
					setResult(Activity.RESULT_OK, new Intent());
					finish();
				}
			}
			break;
			// other 'case' lines to check for other
			// permissions this app might request.
		}
	}
}
