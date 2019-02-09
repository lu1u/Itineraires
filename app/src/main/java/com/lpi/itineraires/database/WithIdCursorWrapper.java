package com.lpi.itineraires.database;

import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Chris on 19/06/14.
 * If the cursor does not contain a column name _id, one will be added - either as the first column or the last.
 * The value will be the position of the row in the rowset, as returned by getPosition()
 */
public class WithIdCursorWrapper extends CursorWrapper
{
public static final int ID_FIRST = 0;
public static final int ID_LAST = 1;
private static final String TAG = "WithIdCursorWrapper";
private boolean mIsIdAdded = true;
private int mIdPosition;

/**
 * @param c
 * @param position Should be ID_LAST or ID_FIRST, to put the _id column either in first position or last position.
 *                 If some other value is passed, ID_FIRST will be assumed
 */
public WithIdCursorWrapper(@NonNull Cursor c, int position)
{
	super(c);
	if (position == ID_LAST)
	{
		mIdPosition = ID_LAST;
	}
	else
	{
		mIdPosition = ID_FIRST;
	}

	for (String name : c.getColumnNames())
	{
		if ("_id".equals(name))
		{
			mIsIdAdded = false;
			break;
		}
	}
}

/**
 * Constructor where position defaults to ID_FIRST
 *
 * @param c
 */
public WithIdCursorWrapper(@NonNull Cursor c)
{
	this(c, ID_FIRST);
}

@Override
public void copyStringToBuffer(int columnIndex, @NonNull CharArrayBuffer buffer)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		super.copyStringToBuffer(columnIndex, buffer);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		super.copyStringToBuffer(columnIndex - 1, buffer);
	}
	else
	{
		buffer.data = getString(columnIndex).toCharArray();
		buffer.sizeCopied = buffer.data.length;
	}
}

@Nullable
@Override
public byte[] getBlob(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getBlob(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getBlob(columnIndex - 1);
	}
	else
	{
		if (!isAfterLast() && !isBeforeFirst())
		{
			return null;
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

@Override
public int getColumnCount()
{
	if (mIsIdAdded)
	{
		return super.getColumnCount() + 1;
	}
	else
	{
		return super.getColumnCount();
	}
}

@Override
public int getColumnIndex(String columnName)
{
	return getColumnIndex(columnName, false);
}

@Override
public int getColumnIndexOrThrow(String columnName)
{
	return getColumnIndex(columnName, true);
}

public int getColumnIndex(String columnName, boolean throwException)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && !"_id".equals(columnName)))
	{
		if (throwException)
		{
			return super.getColumnIndexOrThrow(columnName);
		}
		else
		{
			return super.getColumnIndex(columnName);
		}
	}
	else if (mIdPosition == ID_FIRST && !"_id".equals(columnName))
	{
		if (throwException)
		{
			return super.getColumnIndexOrThrow(columnName) + 1;
		}
		else
		{
			// -1 means column not found
			int i = super.getColumnIndex(columnName);
			return i >= 0 ? i + 1 : i;
		}
	}
	else if (mIdPosition == ID_FIRST)
	{
		// by now, columnName must be _id, added as the first column:
		return 0;
	}
	else
	{
		// _id, added as last column:
		return super.getColumnCount();
	}
}

@Override
public String getColumnName(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getColumnName(columnIndex);
	}
	else if ((mIdPosition == ID_FIRST && columnIndex == 0) ||
			(mIdPosition == ID_LAST && columnIndex == super.getColumnCount()))
	{
		return "_id";
	}
	else
	{
		// ID_FIRST, columnIndex not 0
		return super.getColumnName(columnIndex - 1);
	}

}

@Override
public String[] getColumnNames()
{
	if (!mIsIdAdded)
	{
		return super.getColumnNames();
	}
	else if (mIdPosition == ID_FIRST)
	{
		int superColCount = super.getColumnCount();
		String[] names = new String[superColCount + 1];
		names[0] = "_id";
		System.arraycopy(super.getColumnNames(), 0, names, 1, superColCount);
		return names;
	}
	else
	{
		// ID_LAST
		int superColCount = super.getColumnCount();
		String[] names = new String[superColCount + 1];
		names[superColCount] = "_id";
		System.arraycopy(super.getColumnNames(), 0, names, 0, superColCount);
		return names;
	}
}

@Override
public double getDouble(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getDouble(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getDouble(columnIndex - 1);
	}
	else
	{
		// we've added _id, and that's what we want.
		// we return the current cursor position:
		if (!isAfterLast() && !isBeforeFirst())
		{
			return (double) getPosition();
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

@Override
public float getFloat(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getFloat(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getFloat(columnIndex - 1);
	}
	else
	{
		// we've added _id, and that's what we want.
		// we return the current cursor position:
		if (!isAfterLast() && !isBeforeFirst())
		{
			return (float) getPosition();
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

@Override
public int getInt(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getInt(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getInt(columnIndex - 1);
	}
	else
	{
		// we've added _id, and that's what we want.
		// we return the current cursor position:
		if (!isAfterLast() && !isBeforeFirst())
		{
			return getPosition();
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

@Override
public long getLong(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getLong(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getLong(columnIndex - 1);
	}
	else
	{
		// we've added _id, and that's what we want.
		// we return the current cursor position:
		if (!isAfterLast() && !isBeforeFirst())
		{
			return (long) getPosition();
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

@Override
public short getShort(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getShort(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getShort(columnIndex - 1);
	}
	else
	{
		// we've added _id, and that's what we want.
		// we return the current cursor position:
		if (!isAfterLast() && !isBeforeFirst())
		{
			return (short) getPosition();
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

@Override
public String getString(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getString(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getString(columnIndex - 1);
	}
	else
	{
		// we've added _id, and that's what we want.
		// we return the current cursor position:
		if (!isAfterLast() && !isBeforeFirst())
		{
			return String.valueOf(getPosition());
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

@Override
public int getType(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.getType(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.getType(columnIndex - 1);
	}
	else
	{
		return Cursor.FIELD_TYPE_INTEGER;
	}
}

@Override
public boolean isNull(int columnIndex)
{
	if (!mIsIdAdded ||
			(mIdPosition == ID_LAST && columnIndex < super.getColumnCount()))
	{
		return super.isNull(columnIndex);
	}
	else if (mIdPosition == ID_FIRST && columnIndex > 0)
	{
		return super.isNull(columnIndex - 1);
	}
	else
	{
		// we've added _id, and that's what we want.
		if (!isAfterLast() && !isBeforeFirst())
		{
			return false;
		}
		else
		{
			throw new CursorIndexOutOfBoundsException("Index " + getPosition() + " requested, with a size of " + getCount());
		}
	}
}

/**
 * @return false if there was already an _id column in the query results
 * true if an _id column has been added
 */
public boolean isIdAdded()
{
	return mIsIdAdded;
}

/**
 * @return either ID_FIRST or ID_LAST, as passed to the constructor or defaulted
 */
public int getIdPosition()
{
	return mIdPosition;
}

public void log()
{
	Log.d(TAG, "_id column added is " + mIsIdAdded);
	Log.d(TAG, "if added, _id column postion is " + ((mIdPosition == ID_LAST) ? "ID_LAST" : "ID_FIRST"));
	Log.d(TAG, "No. of columns is " + getColumnCount());
	Log.d(TAG, "Column Names:");
	for (int i = 0; i < getColumnNames().length; i++)
	{
		Log.d(TAG, "\tindex " + i + ": getColumnNames()[i] is: " + getColumnNames()[i] + " getColumnName(i) is: " + getColumnName(i) +
				" getColumnIndex(getColumnName(i)) is: " + getColumnIndex(getColumnName(i)));
	}
	if (isBeforeFirst())
	{
		Log.d(TAG, "Cursor positioned before first. getPosition returns " + getPosition());
	}
	else if (isAfterLast())
	{
		Log.d(TAG, "Cursor positioned after last. getPosition returns " + getPosition());
	}
	else
	{
		Log.d(TAG, "Cursor position is " + getPosition());
	}
	Log.d(TAG, "Column Values:");
	for (int i = 0; i < getColumnCount(); i++)
	{
		try
		{
			Log.d(TAG, "\tValue returned by isNull(" + i + "): " + isNull(i) + " getType(i): " + getType(i) + " getString(i) is: " + getString(i) +
					" getInt(i) is: " + getInt(i));
		} catch (Exception e)
		{
			Log.d(TAG, "cought exception " + e);
		}
	}
	moveToFirst();
	if (isBeforeFirst())
	{
		Log.d(TAG, "Cursor positioned before first");
	}
	else if (isAfterLast())
	{
		Log.d(TAG, "Cursor positioned after last");
	}
	else
	{
		Log.d(TAG, "Cursor position is " + getPosition());
	}
	Log.d(TAG, "Column Values:");
	for (int i = 0; i < getColumnCount(); i++)
	{
		Log.d(TAG, "\tValue returned by isNull(" + i + "): " + isNull(i) + " getType(i): " + getType(i) + " getString(i) is: " + getString(i) +
				" getInt(i) is: " + getInt(i));
	}
	moveToLast();
	moveToNext();
	if (isBeforeFirst())
	{
		Log.d(TAG, "Cursor positioned before first. getPosition returns " + getPosition());
	}
	else if (isAfterLast())
	{
		Log.d(TAG, "Cursor positioned after last. getPosition returns " + getPosition());
	}
	else
	{
		Log.d(TAG, "Cursor position is " + getPosition());
	}
	Log.d(TAG, "Column Values:");
	for (int i = 0; i < getColumnCount(); i++)
	{
		try
		{
			Log.d(TAG, "\tValue returned by isNull(" + i + "): " + isNull(i) + " getType(i): " + getType(i) + " getString(i) is: " + getString(i) +
					" getInt(i) is: " + getInt(i));
		} catch (Exception e)
		{
			Log.d(TAG, "cought exception " + e);
		}
	}
}
}