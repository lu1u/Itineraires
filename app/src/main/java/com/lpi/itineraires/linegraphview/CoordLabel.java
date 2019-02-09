package com.lpi.itineraires.linegraphview;

import android.os.Parcel;
import android.os.Parcelable;

public class CoordLabel implements Parcelable
{
	public CoordLabel()
	{
		coord = 0;
		label = "";
	}

	public CoordLabel(float c, String l)
	{
		coord = c;
		label = l;
	}

	public float coord;
	public String label;

	protected CoordLabel(Parcel in)
	{
		coord = in.readFloat();
		label = in.readString();
	}

	public static final Creator<CoordLabel> CREATOR = new Creator<CoordLabel>()
	{
		@Override
		public CoordLabel createFromParcel(Parcel in)
		{
			return new CoordLabel(in);
		}

		@Override
		public CoordLabel[] newArray(int size)
		{
			return new CoordLabel[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeFloat(coord);
		dest.writeString(label);
	}
}
