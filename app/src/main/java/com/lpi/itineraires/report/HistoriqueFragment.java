package com.lpi.itineraires.report;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lpi.itineraires.MainActivity;
import com.lpi.itineraires.R;

public class HistoriqueFragment extends ReportFragment
{


HistoriqueAdapter _adapter;


public HistoriqueFragment()
{
	// Required empty public constructor
}

/**
 * Use this factory method to create a new instance of
 * this fragment using the provided parameters.
 *
 * @param param1 Parameter 1.
 * @param param2 Parameter 2.
 * @return A new instance of fragment HistoriqueFragment.
 */
// TODO: Rename and change types and number of parameters
public static HistoriqueFragment newInstance()
{
	return new HistoriqueFragment();
}


@Override
public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState)
{
	View v = inflater.inflate(R.layout.fragment_historique, container, false);

	ListView lv = v.findViewById(R.id.listView);
	_adapter = new HistoriqueAdapter(getActivity(), HistoriqueDatabase.getInstance(getActivity()).getCursor());
	lv.setAdapter(_adapter);
	return v;
}

@Override
public void Vide()
{
	HistoriqueDatabase db = HistoriqueDatabase.getInstance(getActivity());
	db.Vide();
	_adapter.changeCursor(db.getCursor());
	MainActivity.MessageNotification(getView(), "Historique effacé");
}


}
