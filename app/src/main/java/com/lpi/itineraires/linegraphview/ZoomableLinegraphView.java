package com.lpi.itineraires.linegraphview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

import com.lpi.itineraires.R;

public class ZoomableLinegraphView extends ZoomableView
{
	/**
	 * The number of individual points (samples) in the chart series to draw onscreen.
	 */
	private static final int DRAW_STEPS = 30;
	private static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
	private final char[] mLabelBuffer = new char[100];
	// Buffers for storing current X and Y stops. See the computeAxisStops method for more details.
	private final AxisStops mXStopsBuffer = new AxisStops();
	private final AxisStops mYStopsBuffer = new AxisStops();
	// Current attribute values and Paints.
	private float mLabelTextSize = 1;
	private int mLabelSeparation = 1;
	private int mLabelTextColor = 1;
	private Paint mLabelTextPaint;
	private int mMaxLabelWidth;
	private int mLabelHeight;
	private float mGridThickness;
	private int mGridColor;
	private Paint mGridPaint;
	private float mAxisThickness;
	private int mAxisColor;
	private Paint mAxisPaint;
	private float mDataThickness;
	private int mDataColor;
	private Paint mDataPaint;
	private float[] mAxisXPositionsBuffer = new float[]{};
	private float[] mAxisYPositionsBuffer = new float[]{};
	private float[] mAxisXLinesBuffer = new float[]{};
	private float[] mAxisYLinesBuffer = new float[]{};
	private float[] mSeriesLinesBuffer = new float[(DRAW_STEPS + 1) * 4];

	public ZoomableLinegraphView(Context context)
	{
		this(context, null, 0);
	}

	public ZoomableLinegraphView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ZoomableLinegraphView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	/**
	 * Rounds the given number to the given number of significant digits. Based on an answer on
	 * <a href="http://stackoverflow.com/questions/202302">Stack Overflow</a>.
	 */
	private static float roundToOneSignificantFigure(double num)
	{
		final float d = (float) Math.ceil((float) Math.log10(num < 0 ? -num : num));
		final int power = 1 - (int) d;
		final float magnitude = (float) Math.pow(10, power);
		final long shifted = Math.round(num * magnitude);
		return shifted / magnitude;
	}

	/**
	 * Formats a float value to the given number of decimals. Returns the length of the string.
	 * The string begins at out.length - [return value].
	 */
	private static int formatFloat(final char[] out, float val, int digits)
	{
		boolean negative = false;
		if (val == 0)
		{
			out[out.length - 1] = '0';
			return 1;
		}
		if (val < 0)
		{
			negative = true;
			val = -val;
		}
		if (digits > POW10.length)
		{
			digits = POW10.length - 1;
		}
		val *= POW10[digits];
		long lval = Math.round(val);
		int index = out.length - 1;
		int charCount = 0;
		while (lval != 0 || charCount < (digits + 1))
		{
			int digit = (int) (lval % 10);
			lval = lval / 10;
			out[index--] = (char) (digit + '0');
			charCount++;
			if (charCount == digits)
			{
				out[index--] = '.';
				charCount++;
			}
		}
		if (negative)
		{
			out[index--] = '-';
			charCount++;
		}
		return charCount;
	}

	/**
	 * Computes the set of axis labels to show given start and stop boundaries and an ideal number
	 * of stops between these boundaries.
	 *
	 * @param start    The minimum extreme (e.g. the left edge) for the axis.
	 * @param stop     The maximum extreme (e.g. the right edge) for the axis.
	 * @param steps    The ideal number of stops to create. This should be based on available screen
	 *                 space; the more space there is, the more stops should be shown.
	 * @param outStops The destination {@link AxisStops} object to populate.
	 */
	private static void computeAxisStops(float start, float stop, int steps, AxisStops outStops)
	{
		double range = stop - start;
		if (steps == 0 || range <= 0)
		{
			outStops.stops = new float[]{};
			outStops.numStops = 0;
			return;
		}
		double rawInterval = range / steps;
		double interval = roundToOneSignificantFigure(rawInterval);
		double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
		int intervalSigDigit = (int) (interval / intervalMagnitude);
		if (intervalSigDigit > 5)
		{
			// Use one order of magnitude higher, to avoid intervals like 0.9 or 90
			interval = Math.floor(10 * intervalMagnitude);
		}
		double first = Math.ceil(start / interval) * interval;
		double last = Math.nextUp(Math.floor(stop / interval) * interval);
		double f;
		int i;
		int n = 0;
		for (f = first; f <= last; f += interval)
		{
			++n;
		}
		outStops.numStops = n;
		if (outStops.stops.length < n)
		{
			// Ensure stops contains at least numStops elements.
			outStops.stops = new float[n];
		}
		for (f = first, i = 0; i < n; f += interval, ++i)
		{
			outStops.stops[i] = (float) f;
		}
		if (interval < 1)
		{
			outStops.decimals = (int) Math.ceil(-Math.log10(interval));
		}
		else
		{
			outStops.decimals = 0;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//     Methods and objects related to drawing
	//
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// Draws axes and text labels
		drawAxes(canvas);
		// Clips the next few drawing operations to the content area
		int clipRestoreCount = canvas.save();
		canvas.clipRect(mContentRect);
		dessineDonneesUnclipped(canvas);
		drawEdgeEffectsUnclipped(canvas);
		// Removes clipping rectangle
		canvas.restoreToCount(clipRestoreCount);
		// Draws chart container
		canvas.drawRect(mContentRect, mAxisPaint);
	}

	@Override
	protected void initAttributs(TypedArray a)
	{
		mLabelTextColor = a.getColor(R.styleable.ZoomableView_ILGV_labelTextColor, mLabelTextColor);
		mLabelTextSize = a.getDimension(R.styleable.ZoomableView_ILGV_labelTextSize, mLabelTextSize);
		mLabelSeparation = a.getDimensionPixelSize(R.styleable.ZoomableView_ILGV_labelSeparation, mLabelSeparation);
		mGridThickness = a.getDimension(R.styleable.ZoomableView_ILGV_gridThickness, mGridThickness);
		mGridColor = a.getColor(R.styleable.ZoomableView_ILGV_gridColor, mGridColor);
		mAxisThickness = a.getDimension(R.styleable.ZoomableView_ILGV_axisThickness, mAxisThickness);
		mAxisColor = a.getColor(R.styleable.ZoomableView_ILGV_axisColor, mAxisColor);
		mDataThickness = a.getDimension(R.styleable.ZoomableView_ILGV_dataThickness, mDataThickness);
		mDataColor = a.getColor(R.styleable.ZoomableView_ILGV_dataColor, mDataColor);
	}

	/**
	 * (Re)initializes {@link Paint} objects based on current attribute values.
	 */
	@Override
	protected void initPaints()
	{
		mLabelTextPaint = new Paint();
		mLabelTextPaint.setAntiAlias(true);
		mLabelTextPaint.setTextSize(mLabelTextSize);
		mLabelTextPaint.setColor(mLabelTextColor);
		mLabelHeight = (int) Math.abs(mLabelTextPaint.getFontMetrics().top);
		mMaxLabelWidth = (int) mLabelTextPaint.measureText("0000");
		mGridPaint = new Paint();
		mGridPaint.setStrokeWidth(mGridThickness);
		mGridPaint.setColor(mGridColor);
		mGridPaint.setStyle(Paint.Style.STROKE);
		mAxisPaint = new Paint();
		mAxisPaint.setStrokeWidth(mAxisThickness);
		mAxisPaint.setColor(mAxisColor);
		mAxisPaint.setStyle(Paint.Style.STROKE);
		mDataPaint = new Paint();
		mDataPaint.setStrokeWidth(mDataThickness);
		mDataPaint.setColor(mDataColor);
		mDataPaint.setStyle(Paint.Style.STROKE);
		mDataPaint.setAntiAlias(true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mContentRect.set(getPaddingLeft() + mMaxLabelWidth + mLabelSeparation, getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom() - mLabelHeight - mLabelSeparation);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int minChartSize = getResources().getDimensionPixelSize(R.dimen.min_chart_size);
		setMeasuredDimension(
				Math.max(getSuggestedMinimumWidth(),
						resolveSize(minChartSize + getPaddingLeft() + mMaxLabelWidth + mLabelSeparation + getPaddingRight(),
								widthMeasureSpec)),
				Math.max(getSuggestedMinimumHeight(),
						resolveSize(minChartSize + getPaddingTop() + mLabelHeight + mLabelSeparation + getPaddingBottom(),
								heightMeasureSpec)));
	}

	/**
	 * Draws the chart axes and labels onto the canvas.
	 */
	private void drawAxes(Canvas canvas)
	{
		// Computes axis stops (in terms of numerical value and position on screen)
		int i;
		computeAxisStops(mCurrentViewport.left, mCurrentViewport.right, mContentRect.width() / mMaxLabelWidth / 2, mXStopsBuffer);
		computeAxisStops(mCurrentViewport.top, mCurrentViewport.bottom, mContentRect.height() / mLabelHeight / 2, mYStopsBuffer);

		// Avoid unnecessary allocations during drawing. Re-use allocated
		// arrays and only reallocate if the number of stops grows.
		if (mAxisXPositionsBuffer.length < mXStopsBuffer.numStops)
			mAxisXPositionsBuffer = new float[mXStopsBuffer.numStops];

		if (mAxisYPositionsBuffer.length < mYStopsBuffer.numStops)
			mAxisYPositionsBuffer = new float[mYStopsBuffer.numStops];

		if (mAxisXLinesBuffer.length < mXStopsBuffer.numStops * 4)
			mAxisXLinesBuffer = new float[mXStopsBuffer.numStops * 4];

		if (mAxisYLinesBuffer.length < mYStopsBuffer.numStops * 4)
			mAxisYLinesBuffer = new float[mYStopsBuffer.numStops * 4];

		// Compute positions
		for (i = 0; i < mXStopsBuffer.numStops; i++)
		{
			mAxisXPositionsBuffer[i] = getDrawX(mXStopsBuffer.stops[i]);
		}
		for (i = 0; i < mYStopsBuffer.numStops; i++)
		{
			mAxisYPositionsBuffer[i] = getDrawY(mYStopsBuffer.stops[i]);
		}
		// Draws grid lines using drawLines (faster than individual drawLine calls)
		for (i = 0; i < mXStopsBuffer.numStops; i++)
		{
			mAxisXLinesBuffer[i * 4 + 0] = (float) Math.floor(mAxisXPositionsBuffer[i]);
			mAxisXLinesBuffer[i * 4 + 1] = mContentRect.top;
			mAxisXLinesBuffer[i * 4 + 2] = (float) Math.floor(mAxisXPositionsBuffer[i]);
			mAxisXLinesBuffer[i * 4 + 3] = mContentRect.bottom;
		}
		canvas.drawLines(mAxisXLinesBuffer, 0, mXStopsBuffer.numStops * 4, mGridPaint);
		for (i = 0; i < mYStopsBuffer.numStops; i++)
		{
			mAxisYLinesBuffer[i * 4 + 0] = mContentRect.left;
			mAxisYLinesBuffer[i * 4 + 1] = (float) Math.floor(mAxisYPositionsBuffer[i]);
			mAxisYLinesBuffer[i * 4 + 2] = mContentRect.right;
			mAxisYLinesBuffer[i * 4 + 3] = (float) Math.floor(mAxisYPositionsBuffer[i]);
		}
		canvas.drawLines(mAxisYLinesBuffer, 0, mYStopsBuffer.numStops * 4, mGridPaint);
		// Draws X labels
		int labelOffset;
		int labelLength;
		mLabelTextPaint.setTextAlign(Paint.Align.CENTER);
		for (i = 0; i < mXStopsBuffer.numStops; i++)
		{
			// Do not use String.format in high-performance code such as onDraw code.
			labelLength = formatFloat(mLabelBuffer, mXStopsBuffer.stops[i], mXStopsBuffer.decimals);
			labelOffset = mLabelBuffer.length - labelLength;
			canvas.drawText(
					mLabelBuffer, labelOffset, labelLength,
					mAxisXPositionsBuffer[i],
					mContentRect.bottom + mLabelHeight + mLabelSeparation,
					mLabelTextPaint);
		}
		// Draws Y labels
		mLabelTextPaint.setTextAlign(Paint.Align.RIGHT);
		for (i = 0; i < mYStopsBuffer.numStops; i++)
		{
			// Do not use String.format in high-performance code such as onDraw code.
			labelLength = formatFloat(mLabelBuffer, mYStopsBuffer.stops[i], mYStopsBuffer.decimals);
			labelOffset = mLabelBuffer.length - labelLength;
			canvas.drawText(
					mLabelBuffer, labelOffset, labelLength,
					mContentRect.left - mLabelSeparation,
					mAxisYPositionsBuffer[i] + mLabelHeight / 2.0f,
					mLabelTextPaint);
		}
	}

	/**
	 * Computes the pixel offset for the given X chart value. This may be outside the view bounds.
	 */
	private float getDrawX(float x)
	{
		return mContentRect.left
				+ mContentRect.width()
				* (x - mCurrentViewport.left) / mCurrentViewport.width();
	}

	/**
	 * Computes the pixel offset for the given Y chart value. This may be outside the view bounds.
	 */
	private float getDrawY(float y)
	{
		return mContentRect.bottom
				- mContentRect.height()
				* (y - mCurrentViewport.top) / mCurrentViewport.height();
	}

	/**
	 * Draws the currently visible portion of the data series defined by {@link #fun(float)} to the
	 * canvas. This method does not clip its drawing, so users should call {@link Canvas#clipRect
	 * before calling this method.
	 */
	private void dessineDonneesUnclipped(Canvas canvas)
	{
		mSeriesLinesBuffer[0] = mContentRect.left;
		mSeriesLinesBuffer[1] = getDrawY(fun(mCurrentViewport.left));
		mSeriesLinesBuffer[2] = mSeriesLinesBuffer[0];
		mSeriesLinesBuffer[3] = mSeriesLinesBuffer[1];
		float x;
		for (int i = 1; i <= DRAW_STEPS; i++)
		{
			mSeriesLinesBuffer[i * 4 + 0] = mSeriesLinesBuffer[(i - 1) * 4 + 2];
			mSeriesLinesBuffer[i * 4 + 1] = mSeriesLinesBuffer[(i - 1) * 4 + 3];
			x = (mCurrentViewport.left + (mCurrentViewport.width() / DRAW_STEPS * i));
			mSeriesLinesBuffer[i * 4 + 2] = getDrawX(x);
			mSeriesLinesBuffer[i * 4 + 3] = getDrawY(fun(x));
		}
		canvas.drawLines(mSeriesLinesBuffer, mDataPaint);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//     Methods related to custom attributes
	//
	////////////////////////////////////////////////////////////////////////////////////////////////
	public float getLabelTextSize()
	{
		return mLabelTextSize;
	}

	public void setLabelTextSize(float labelTextSize)
	{
		mLabelTextSize = labelTextSize;
		initPaints();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public int getLabelTextColor()
	{
		return mLabelTextColor;
	}

	public void setLabelTextColor(int labelTextColor)
	{
		mLabelTextColor = labelTextColor;
		initPaints();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public float getGridThickness()
	{
		return mGridThickness;
	}

	public void setGridThickness(float gridThickness)
	{
		mGridThickness = gridThickness;
		initPaints();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public int getGridColor()
	{
		return mGridColor;
	}

	public void setGridColor(int gridColor)
	{
		mGridColor = gridColor;
		initPaints();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public float getAxisThickness()
	{
		return mAxisThickness;
	}

	public void setAxisThickness(float axisThickness)
	{
		mAxisThickness = axisThickness;
		initPaints();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public int getAxisColor()
	{
		return mAxisColor;
	}

	public void setAxisColor(int axisColor)
	{
		mAxisColor = axisColor;
		initPaints();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public float getDataThickness()
	{
		return mDataThickness;
	}

	public void setDataThickness(float dataThickness)
	{
		mDataThickness = dataThickness;
	}

	public int getDataColor()
	{
		return mDataColor;
	}

	public void setDataColor(int dataColor)
	{
		mDataColor = dataColor;
	}

	/**
	 * A simple class representing axis label values.
	 *
	 * @see #computeAxisStops
	 */
	private static class AxisStops
	{
		float[] stops = new float[]{};
		int numStops;
		int decimals;
	}
}
