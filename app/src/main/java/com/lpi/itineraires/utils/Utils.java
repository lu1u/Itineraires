package com.lpi.itineraires.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.lpi.itineraires.R;


/**
 * Fonctions utilitaires diverses
 */
public class Utils
{
public static void addHint(final Activity a, int id, @NonNull final String message)
{
	View v = a.findViewById(id);
	if (v == null)
		return;

	addHint(a, v, message);
}
public static void addHint(final Activity a, View v, @NonNull final String message)
{
	v.setOnFocusChangeListener(new View.OnFocusChangeListener()
	{
		@Override
		public void onFocusChange(@NonNull View v, boolean hasFocus)
		{
			if (hasFocus)
				//Toast.makeText(v.getContext(), ic_message, Toast.LENGTH_SHORT).set.show();
				displayToastAboveButton(a, v, message);
		}
	});
}

private static void displayToastAboveButton(Activity a, @NonNull View v, @NonNull String message)
{
	/*
	int xOffset = 0;
	int yOffset = 0;
	Rect gvr = new Rect();

	View parent = (View) v.getParent();
	int parentHeight = parent.getHeight();

	if (v.getGlobalVisibleRect(gvr))
	{
		View root = v.getRootView();

		int halfWidth = root.getRight() / 2;
		int halfHeight = root.getBottom() / 2;

		int parentCenterX = ((gvr.right - gvr.left) / 2) + gvr.left;

		int parentCenterY = ((gvr.bottom - gvr.top) / 2) + gvr.top;

		if (parentCenterY <= halfHeight)
		{
			yOffset = -(halfHeight - parentCenterY) - parentHeight;
		}
		else
		{
			yOffset = (parentCenterY - halfHeight) - parentHeight;
		}

		if (parentCenterX < halfWidth)
		{
			xOffset = -(halfWidth - parentCenterX);
		}

		if (parentCenterX >= halfWidth)
		{
			xOffset = parentCenterX - halfWidth;
		}
	}

	LayoutInflater inflater = a.getLayoutInflater();
	View layout = inflater.inflate(R.layout.hint_toast_layout, (ViewGroup) a.findViewById(R.id.layoutRoot));
	TextView text = (TextView) layout.findViewById(R.id.text);
	text.setText(message);

	Toast toast = Toast.makeText(a, message, Toast.LENGTH_SHORT);
	toast.setGravity(Gravity.CENTER, xOffset, yOffset);
	toast.setView(layout);
	toast.show();
	*/
	Snackbar.make(v, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
}

public static void setTheme(@NonNull Activity a)
{
	Preferences p = Preferences.getInstance(a);
	switch (p.getTheme())
	{

		case 1:
			a.setTheme(R.style.Theme2);
			break;

		case 2:
			a.setTheme(R.style.Theme3);
			break;

		case 3:
			a.setTheme(R.style.Theme4);
			break;

		case 4:
			a.setTheme(R.style.Theme5);
			break;

		case 5:
			a.setTheme(R.style.Theme6);
			break;
		case 0:
		default:
			a.setTheme(R.style.Theme1);
			break;

	}
}

//public static Bitmap getBitmap(Context context, int resId)
//{
//	/*
//}
//	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//		return context.getResources().getDrawable(resId, context.getApplicationContext().getTheme());
//else
//		return context.getResources().getDrawable(resId);      */
//	return BitmapFactory.decodeResource(context.getResources(), resId);
//}

public static void confirmDialog(@NonNull Activity a, @NonNull String titre, @NonNull String message, final int requestCode, final @NonNull ConfirmListener listener)
{
	new AlertDialog.Builder(a)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(titre)
			.setMessage(message)
			.setPositiveButton(a.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					listener.onConfirmOK(requestCode);
				}

			})
			.setNegativeButton(a.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					listener.onConfirmCancel(requestCode);
				}

			})
			.show();
}

	/***
	 * Retourne vrai si le service est demarre
	 * @param serviceClass
	 * @return
	 */
	public static boolean serviceEstDemarre(Context context, final Class serviceClass)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public interface ConfirmListener
{
	void onConfirmOK(int requestCode);

	void onConfirmCancel(int requestCode);
}
}
