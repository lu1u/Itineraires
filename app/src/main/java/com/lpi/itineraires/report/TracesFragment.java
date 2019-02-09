package com.lpi.itineraires.report;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.lpi.itineraires.MainActivity;
import com.lpi.itineraires.R;
import com.lpi.itineraires.utils.Report;

@SuppressWarnings("RedundantCast")
public class TracesFragment extends ReportFragment
{

@NonNull
Report.NIVEAU _niveau = Report.NIVEAU.DEBUG;
TracesAdapter _adapter;


public TracesFragment()
{
	// Required empty public constructor
}

/**
 * Use this factory method to create a new instance of
 * this fragment using the provided parameters.
 *
 * @return A new instance of fragment TracesFragment.
 */
// TODO: Rename and change types and number of parameters
public static TracesFragment newInstance()
{
	return new TracesFragment();
}

@Override
public void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);

}


@Override
public void onResume()
{
	super.onResume();
	_adapter.changeCursor(TracesDatabase.getInstance(getActivity()).getCursor(Report.toInt(_niveau)));
}

@Override
public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState)
{
	View v = inflater.inflate(R.layout.fragment_traces, container, false);

	//noinspection RedundantCast
	ListView lv = (ListView) v.findViewById(R.id.listView);
	_adapter = new TracesAdapter(getActivity(), TracesDatabase.getInstance(getActivity()).getCursor(Report.toInt(_niveau)));
	lv.setAdapter(_adapter);

	Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
			R.array.niveauxRapport, android.R.layout.simple_spinner_item);

	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinner.setAdapter(adapter);
	spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			_niveau = Report.toNIVEAU(position);
			_adapter.changeCursor(TracesDatabase.getInstance(getActivity()).getCursor(Report.toInt(_niveau)));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{

		}
	});
	return v;
}

@Override
public void Vide()
{
	TracesDatabase db = TracesDatabase.getInstance(getActivity());
	db.Vide();
	_adapter.changeCursor(db.getCursor(Report.toInt(_niveau)));
	MainActivity.MessageNotification(getView(), "Traces effacées");
}


}
