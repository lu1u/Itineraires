package com.lpi.itineraires.itineraire;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lpi.itineraires.R;

public class TypeItineraire
{
	public static @NonNull
	String getTexteType(TYPE type)
	{
		switch (type)
		{
			case APIED:
				return "à pied";
			case VTT:
				return "vtt";
			case VELO:
				return "vélo";
			case SKI_PISTE:
				return "ski de piste";
			case SKI_RANDONNEE:
				return "ski de randonnée";
			case SKI_FOND:
				return "ski de fond";
			case MOTO:
				return "moto";
			case VOITURE:
				return "auto";
			default:
				return "autre";
		}
	}

	@NonNull
	static public TYPE intToType(int t)
	{
		switch (t)
		{
			case 0:
				return TYPE.APIED;
			case 1:
				return TYPE.VTT;
			case 2:
				return TYPE.VELO;
			case 3:
				return TYPE.SKI_PISTE;
			case 4:
				return TYPE.SKI_RANDONNEE;
			case 5:
				return TYPE.SKI_FOND;
			case 6:
				return TYPE.MOTO;
			case 7:
				return TYPE.VOITURE;
			default:
				return TYPE.AUTRE;
		}
	}

	static public int typeToInt(TYPE t)
	{
		switch (t)
		{
			case APIED:
				return 0;
			case VTT:
				return 1;
			case VELO:
				return 2;
			case SKI_PISTE:
				return 3;
			case SKI_RANDONNEE:
				return 4;
			case SKI_FOND:
				return 5;
			case MOTO:
				return 6;
			case VOITURE:
				return 7;
			default:
				return 8;
		}
	}

	static int couleurResourceId(TYPE type)
	{
		switch (type)
		{
			case APIED:
				return R.color.type_apied;
			case VTT:
				return R.color.type_vtt;
			case VELO:
				return R.color.type_velo;
			case SKI_PISTE:
				return R.color.type_skipiste;
			case SKI_FOND:
				return R.color.type_skifond;
			case SKI_RANDONNEE:
				return R.color.type_skirando;
			case MOTO:
				return R.color.type_moto;
			case VOITURE:
				return R.color.type_voiture;
			default:
				return R.color.type_autre;
		}
	}

	static int couleur(Context context, TYPE type)
	{
		return context.getResources().getColor(couleurResourceId(type), context.getTheme());
	}

	enum TYPE
	{
		APIED, VTT, VELO, SKI_PISTE, SKI_FOND, SKI_RANDONNEE, MOTO, VOITURE, AUTRE
	}
}
