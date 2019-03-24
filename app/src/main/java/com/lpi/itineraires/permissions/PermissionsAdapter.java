package com.lpi.itineraires.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lpi.itineraires.R;

class PermissionsAdapter extends BaseAdapter
{
	String[] _permissions;
	String[] _descriptions;
	Context _context;

	public PermissionsAdapter(Context context, int idPermissions, int idDescriptions)
	{
		_context = context;
		_permissions = context.getResources().getStringArray(idPermissions);
		_descriptions = context.getResources().getStringArray(idDescriptions);
	}

	@Override
	public int getCount()
	{
		return _permissions.length;
	}

	@Override
	public Object getItem(final int position)
	{
		return _permissions[position];
	}

	@Override
	public long getItemId(final int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		View v = LayoutInflater.from(_context).inflate(R.layout.element_liste_permissions, parent, false);
		TextView tv = v.findViewById(R.id.textviewDescription);
		tv.setText(_descriptions[position]);

		final boolean permis = checkPermission(_permissions[position]);

		((ImageView) v.findViewById(R.id.idImagePermission)).setImageResource(permis ? R.drawable.ic_check : R.drawable.ic_cancel);
		return v;
	}

	private boolean checkPermission(final String permission)
	{
		return ActivityCompat.checkSelfPermission(_context, permission) == PackageManager.PERMISSION_GRANTED;
	}
}
