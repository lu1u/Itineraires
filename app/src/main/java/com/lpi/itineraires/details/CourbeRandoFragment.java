package com.lpi.itineraires.details;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lpi.itineraires.R;
import com.lpi.itineraires.linegraphview.CoordLabel;
import com.lpi.itineraires.linegraphview.LinegraphView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourbeRandoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourbeRandoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourbeRandoFragment extends Fragment
{
	private static final String LINEGRAPH_VIEW_X = "linegraphView.x";
	private static final String LINEGRAPH_VIEW_Y = "linegraphView.y";
	private static final String AXE_VIEW_X = "axeView.x";
	private static final String AXE_VIEW_Y = "axeView.y";

	@Nullable private float[] x;
	@Nullable private float[] y;
	@Nullable private CoordLabel[] labelsX;
	@Nullable private CoordLabel[] labelsY;

	@Nullable
	private OnFragmentInteractionListener mListener;

	public CourbeRandoFragment()
	{
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment CourbeRandoFragment.
	 */
	// TODO: Rename and change types and number of parameters
	@NonNull
	public static CourbeRandoFragment newInstance(@Nullable float[] x, @Nullable float[] y, @Nullable CoordLabel[] axeX, @Nullable CoordLabel[] axeY)
	{
		CourbeRandoFragment fragment = new CourbeRandoFragment();
		Bundle args = new Bundle();
		args.putFloatArray(LINEGRAPH_VIEW_X, x);
		args.putFloatArray(LINEGRAPH_VIEW_Y, y);
		args.putParcelableArray(AXE_VIEW_X, axeX);
		args.putParcelableArray(AXE_VIEW_Y, axeY);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			x = getArguments().getFloatArray(LINEGRAPH_VIEW_X);
			y = getArguments().getFloatArray(LINEGRAPH_VIEW_Y);
			labelsX = (CoordLabel[])getArguments().getParcelableArray(AXE_VIEW_X);
			labelsY = (CoordLabel[])getArguments().getParcelableArray(AXE_VIEW_Y);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_courbe, container, false);
		LinegraphView lnv = v.findViewById(R.id.linegraphView);

		lnv.setValues(x, y);
		if ( labelsX!=null && labelsY !=null)
			lnv.setLabels(labelsX, labelsY);
		return v;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri)
	{
		if (mListener != null)
		{
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener)
		{
			mListener = (OnFragmentInteractionListener) context;
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener
	{
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
}
