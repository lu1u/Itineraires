package com.lpi.itineraires.details;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lpi.itineraires.R;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.itineraire.Itineraire;
import com.lpi.itineraires.itineraire.Position;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment
{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "Id";
	private static final long MINUTE = 60 ;
	private static final long HEURE = MINUTE * 60 ;
	private static final long JOUR = HEURE * 24 ;

	// TODO: Rename and change types of parameters
	@Nullable
	private Itineraire _itineraire;

	//@Nullable private OnFragmentInteractionListener mListener;

	public DetailFragment()
	{
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment DetailFragment.
	 */
	// TODO: Rename and change types and number of parameters
	@NonNull
	public static DetailFragment newInstance(int randoId)
	{
		DetailFragment fragment = new DetailFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, randoId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			int Id = getArguments().getInt(ARG_PARAM1);
			_itineraire = ItinerairesDatabase.getInstance(getContext()).getItineraire(Id);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_detail, container, false);

		String texte = "pas de randonnée";

		if ( _itineraire !=null)
		{
			texte = "Nom: " + _itineraire.Nom + " (Id=" + _itineraire.Id + ")\n\n";
			texte += _itineraire.getDescription(getContext(), true);

			ItinerairesDatabase database = ItinerairesDatabase.getInstance(getContext());
			final int nbPositions = database.getNbPositions(_itineraire.Id);

			if (nbPositions > 1)
			{
				Cursor cursor = database.getPositions(_itineraire.Id);
				if (null != cursor)
				{
					cursor.moveToFirst();
					Position precedente = new Position(cursor);
					float Distance = 0;
					float vitesseMin = precedente.getSpeed();
					float vitesseMax = precedente.getSpeed();
					long debut = precedente.getTime();

					while (cursor.moveToNext())
					{
						Position position = new Position(cursor);
						Distance += precedente.distanceTo(position);
						if (position.getSpeed() < vitesseMin)
							vitesseMin = position.getSpeed();

						if (position.getSpeed() > vitesseMax)
							vitesseMax = position.getSpeed();

						precedente = position;
					}
					long fin = precedente.getTime(); // derniere
					long duree = (fin - debut) / 1000L;
					texte += "\n\nDurée totale " + formateDuree(duree);
					texte += " \n\nDistance parcourue " + Position.formateDistance(Distance);
					cursor.close();

					float vitesseMoyenne = Distance / (float) duree;
					texte += "\n\nVitesse moyenne " + formateVitesse(vitesseMoyenne);
					texte += "\nVitesse min " + formateVitesse(vitesseMin);
					texte += "\nVitesse max " + formateVitesse(vitesseMax);

				}
			}
		}

		((TextView) v.findViewById(R.id.idTextView)).setText(texte);
		return v;
	}

	public static String formateVitesse(float vitesseMs)
	{
		float vitesseKm = vitesseMs * 3.6f;
		return String.format("%.02f", vitesseMs) + "m/s, " + String.format("%.02f", vitesseKm) + "km/h";
	}

	@NonNull
	public static String formateDuree(long dureeEnSecondes)
	{
		String res = "";
		if ( dureeEnSecondes > JOUR) // Jours
		{
			res += (dureeEnSecondes/JOUR) + "j ";
			dureeEnSecondes = dureeEnSecondes%JOUR;
		}

		if ( dureeEnSecondes > HEURE)
		{
			res += (dureeEnSecondes/HEURE) + "h ";
			dureeEnSecondes = dureeEnSecondes%HEURE;
		}

		if ( dureeEnSecondes > MINUTE)
		{
			res += (dureeEnSecondes/MINUTE) + "m ";
			dureeEnSecondes = dureeEnSecondes%MINUTE;
		}

		res += dureeEnSecondes + "s";
		return res;
	}

//	// TODO: Rename method, update argument and hook method into UI event
//	public void onButtonPressed(Uri uri)
//	{
//		if (mListener != null)
//		{
//			mListener.onFragmentInteraction(uri);
//		}
//	}
//
//	@Override
//	public void onAttach(Context context)
//	{
//		super.onAttach(context);
//		if (context instanceof OnFragmentInteractionListener)
//		{
//			mListener = (OnFragmentInteractionListener) context;
//		}
//	}
//
//	@Override
//	public void onDetach()
//	{
//		super.onDetach();
//		mListener = null;
//	}

//	/**
//	 * This interface must be implemented by activities that contain this
//	 * fragment to allow an interaction in this fragment to be communicated
//	 * to the activity and potentially other fragments contained in that
//	 * activity.
//	 * <p>
//	 * See the Android Training lesson <a href=
//	 * "http://developer.android.com/training/basics/fragments/communicating.html"
//	 * >Communicating with Other Fragments</a> for more information.
//	 */
//	public interface OnFragmentInteractionListener
//	{
//		// TODO: Update argument type and name
//		void onFragmentInteraction(Uri uri);
//	}
}
