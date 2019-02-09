package com.lpi.itineraires.linegraphview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.lpi.itineraires.R;

/**
 * TODO: document your custom view class.
 */
public class LinegraphView extends View
{

	// Attributs modifiable dans l'editeur de layout
	private int _CouleurCourbe = Color.RED;
	private float _zoomMin = 0.5f;
	private float _zoomMax = 5.0f;
	private boolean _traceAxes = true;

	float _decalageTexteAxes = 1.0f;

	private float _graphMinX = 0;
	private float _graphMinY = 0;
	private float _graphMaxX = 100.0f;
	private float _graphMaxY = 100.0f;
	private float _graphLargeur = _graphMaxX - _graphMinX;
	private float _graphHauteur = _graphMaxY - _graphMinY;
	private float _ScaleX = 1.0f;
	private float _ScaleY = 1.0f;

	private Paint _chartPaint;
	private Paint _axesPaint;
	private Paint _textPaint;

	private float[] _x;
	private float[] _y;
	private CoordLabel[] _coordLabelX;
	private CoordLabel[] _coordLabelY;
	private float _zoom = 1.0f;
	private float _scrollX = 0;
	private float _scrollY = 0;

	@Nullable
	private Path _chartPath;
	private int _largeurFenetre;
	private int _hauteurFenetre;
	private ScaleGestureDetector mScaleGestureDetector;
	private GestureDetectorCompat mGestureDetector;

	public LinegraphView(Context context)
	{
		super(context);
		init(null, 0);
	}

	public LinegraphView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public LinegraphView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	public void setValues(@Nullable float[] x, @Nullable float[] y)
	{
		_chartPath = null;
		_x = x;
		_y = y;
		invalidate();
	}

	public void setLabels(@NonNull CoordLabel[] x, @NonNull CoordLabel[] y)
	{
		_chartPath = null;
		_coordLabelX = x;
		_coordLabelY = y;
		invalidate();
	}

	private void createPath()
	{
		_chartPath = new Path();
		if (isInEditMode())
		{
			_x = new float[7];
			_y = new float[7];
			_coordLabelY = new CoordLabel[3];
			_coordLabelY[0] = new CoordLabel(-5, "-cinq");
			_coordLabelY[1] = new CoordLabel(0, "zero");
			_coordLabelY[2] = new CoordLabel(4, "quatre");

			_coordLabelX = new CoordLabel[3];
			_coordLabelX[0] = new CoordLabel(-0.5f, "-0.5");
			_coordLabelX[1] = new CoordLabel(1, "1");
			_coordLabelX[2] = new CoordLabel(4, "4");

			_x[0] = -2.0f;
			_y[0] = 0.0f;
			_x[1] = 1.0f;
			_y[1] = 1.0f;
			_x[2] = 2.0f;
			_y[2] = 2.5f;
			_x[3] = 3.0f;
			_y[3] = 7.0f;
			_x[4] = 4.0f;
			_y[4] = -0.5f;
			_x[5] = 5.0f;
			_y[5] = 0.0f;
			_x[6] = 6.0f;
			_y[6] = -6.0f;
		}

		if (_x != null && _y != null)
		{
			final int nbElements = Math.min(_x.length, _y.length);
			if (nbElements > 1)
			{
				float curX = _x[0];
				float curY = _y[0];
				_graphMinX = curX;
				_graphMinY = curY;
				_graphMaxX = _graphMinX;
				_graphMaxY = _graphMinY;

				for (int i = 1; i < nbElements; i++)
				{
					curX = _x[i];
					curY = _y[i];
					if (curX < _graphMinX)
						_graphMinX = curX;
					if (curX > _graphMaxX)
						_graphMaxX = curX;

					if (curY < _graphMinY)
						_graphMinY = curY;
					if (curY > _graphMaxY)
						_graphMaxY = curY;
				}

				_graphLargeur = _graphMaxX - _graphMinX;
				_graphHauteur = _graphMaxY - _graphMinY;
				_largeurFenetre = getWidth();
				_hauteurFenetre = getHeight();
				_ScaleX = ((float) _largeurFenetre) / _graphLargeur;
				_ScaleY = ((float) _hauteurFenetre) / _graphHauteur;


				_chartPath.moveTo(deplaceX(_x[0]), deplaceY(_y[0]));
				for (int i = 1; i < nbElements; i++)
				{

					_chartPath.lineTo(deplaceX(_x[i]), deplaceY(_y[i]));
					//Log.d("GRAPH", deplaceX(_x[i])+ "," + deplaceY(_y[i]));
				}
				//_chartPath.lineTo(deplaceX(_graphMaxX), deplaceY(0));

			}
		}
	}

	private float deplaceX(float x)
	{
		return (x - _graphMinX) * _ScaleX;
	}

	private float deplaceY(float y)
	{
		return (_graphMaxY - y) * _ScaleY;
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			//return super.onScroll(e1, e2, distanceX, distanceY);
			_scrollX = distanceX;
			_scrollY = distanceY;
			invalidate();
			return true;
		}
/*
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			_zoom *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			if (_zoom < _zoomMin)
				_zoom = _zoomMin;
			if (_zoom > _zoomMax)
				_zoom = _zoomMax;
			invalidate();
			return true;
		}
*/

	}

	private void init(AttributeSet attrs, int defStyle)
	{
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.LinegraphView, defStyle, 0);

		_CouleurCourbe = a.getColor(R.styleable.LinegraphView_LGcouleurCourbeHaut, _CouleurCourbe);
		_decalageTexteAxes = a.getDimension(R.styleable.LinegraphView_LGdecalageTexteAxes, _decalageTexteAxes);
		_traceAxes = a.getBoolean(R.styleable.LinegraphView_LGtraceAxes, _traceAxes);
/*
		if (a.hasValue(R.styleable.LinegraphView_exampleDrawable))
		{
			mExampleDrawable = a.getDrawable(
					R.styleable.LinegraphView_exampleDrawable);
			mExampleDrawable.setCallback(this);
		}
*/

		_chartPaint = new Paint();
		_chartPaint.setAntiAlias(true);
		_chartPaint.setColor(_CouleurCourbe);
		_chartPaint.setStyle(Paint.Style.STROKE);
		_chartPaint.setStrokeJoin(Paint.Join.ROUND);
		_chartPaint.setStrokeCap(Paint.Cap.ROUND);
		_chartPaint.setStrokeWidth(a.getDimension(R.styleable.LinegraphView_LGlargeurLigne, 1));

		_axesPaint = new Paint();
		_axesPaint.setAntiAlias(true);
		_axesPaint.setColor(a.getColor(R.styleable.LinegraphView_LGcouleurAxes, Color.GRAY));
		_axesPaint.setStyle(Paint.Style.STROKE);
		_axesPaint.setStrokeJoin(Paint.Join.ROUND);
		_axesPaint.setStrokeCap(Paint.Cap.ROUND);
		_axesPaint.setStrokeWidth(a.getDimension(R.styleable.LinegraphView_LGlargeurAxes, 1));

		_textPaint = new Paint();
		_textPaint.setAntiAlias(true);
		_textPaint.setColor(a.getColor(R.styleable.LinegraphView_LGcouleurLabels, Color.GRAY));
		_textPaint.setStyle(Paint.Style.STROKE);
		_textPaint.setStrokeJoin(Paint.Join.ROUND);
		_textPaint.setStrokeCap(Paint.Cap.ROUND);
		_textPaint.setTextSize(a.getDimension(R.styleable.LinegraphView_LGtailleTexte, 8));
		a.recycle();
		// Sets up interactions
		//mScaleGestureDetector = new ScaleGestureDetector(getContext(), mScaleGestureListener);
		//mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		_chartPath = null;
		invalidate();
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.save();
		canvas.scale(_zoom, _zoom);
		canvas.translate(-_scrollX, -_scrollY);

		if (_largeurFenetre == 0 || _hauteurFenetre == 0 || _chartPath == null)
			createPath();

		if (_traceAxes)
			traceAxes(canvas);

		if (_chartPaint != null)
			canvas.drawPath(_chartPath, _chartPaint);

/*
		Rect r = new Rect();
		_textPaint.getTextBounds("0", 0, 1, r);
		float Y = r.height();
		canvas.drawText("Nb elements " + _x.length, 0, Y, _textPaint);
		Y += r.height();
		canvas.drawText("_graphMinX " + _graphMinX + ", _graphMaxX " + _graphMaxX, 0, Y, _textPaint);
		Y += r.height();
		canvas.drawText("_graphMinY " + _graphMinY + ", _graphMaxY " + _graphMaxY, 0, Y, _textPaint);
		Y += r.height();
		canvas.drawText("_graphLargeur " + _graphLargeur + ", _graphHauteur " + _graphHauteur, 0, Y, _textPaint);
		Y += r.height();
		canvas.drawText("largeurFenetre " + _largeurFenetre + ", hauteurFenetre " + _hauteurFenetre, 0, Y, _textPaint);
		Y += r.height();
		canvas.drawText("scaleX " + _ScaleX + ", scaleY " + _ScaleY, 0, Y, _textPaint);
		Y += r.height();*/

		canvas.restore();
	}

	private void traceAxes(Canvas canvas)
	{
		// Trace les axes
		float x = deplaceX(0);
		float y = deplaceY(0);

		// axe vertical
		canvas.drawLine(x, 0, x, _hauteurFenetre, _axesPaint);
		if (_coordLabelY != null)
		{
			final float X = x + _decalageTexteAxes;
			for (CoordLabel label : _coordLabelY)
			{
				final float Y = deplaceY(label.coord);
				canvas.drawLine(X - _decalageTexteAxes, Y, X + _decalageTexteAxes, Y, _axesPaint);
				canvas.drawText(label.label, X, Y, _textPaint);
			}
		}

		// axe horizontal
		canvas.drawLine(0, y, _largeurFenetre, y, _axesPaint);
		if (_coordLabelX != null)
		{
			float Y = y + _decalageTexteAxes;
			Rect r = new Rect();
			for (CoordLabel label : _coordLabelX)
			{
				float X = deplaceX(label.coord);
				canvas.drawLine(X, Y - _decalageTexteAxes, X, Y + _decalageTexteAxes, _axesPaint);

				_textPaint.getTextBounds(label.label, 0, label.label.length(), r);
				canvas.drawText(label.label, X, Y + r.height() + _decalageTexteAxes, _textPaint);
			}
		}
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		boolean retVal = mScaleGestureDetector.onTouchEvent(event);
//		retVal = mGestureDetector.onTouchEvent(event) || retVal;
//		return retVal || super.onTouchEvent(event);
//	}
//
//	/**
//	 * The scale listener, used for handling multi-finger scale gestures.
//	 */
//	private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
//			= new ScaleGestureDetector.SimpleOnScaleGestureListener()
//	{
//		/**
//		 * This is the active focal point in terms of the viewport. Could be a local
//		 * variable but kept here to minimize per-frame allocations.
//		 */
//
//		@Override
//		public boolean onScale(ScaleGestureDetector scaleGestureDetector)
//		{
//			_zoom *= scaleGestureDetector.getScaleFactor();
//			if (_zoom < _zoomMin)
//				_zoom = _zoomMin;
//
//			if (_zoom > _zoomMax)
//				_zoom = _zoomMax;
//
//			invalidate();
//			return true;
//		}
//	};
//
//	/**
//	 * The gesture listener, used for handling simple gestures such as double touches, scrolls,
//	 * and flings.
//	 */
//	private final GestureDetector.SimpleOnGestureListener mGestureListener
//			= new GestureDetector.SimpleOnGestureListener()
//	{
//
//		@Override
//		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
//		{
//			// Scrolling uses math based on the viewport (as opposed to math using pixels).
//			/**
//			 * Pixel offset is the offset in screen pixels, while viewport offset is the
//			 * offset within the current viewport. For additional information on surface sizes
//			 * and pixel offsets, see the docs for {@link computeScrollSurfaceSize()}. For
//			 * additional information about the viewport, see the comments for
//			 * {@link mCurrentViewport}.
//			 */
//			_scrollX += distanceX / _zoom;
//			_scrollY += distanceY / _zoom;
//
//			if (_scrollX < 0)
//				_scrollX = 0;
//
//			if (_scrollX > (_largeurFenetre - (_graphLargeur*_zoom)))
//				_scrollX = (_largeurFenetre - (_graphLargeur*_zoom));
//
//			if (_scrollY < 0)
//				_scrollY = 0;
//
//			invalidate();
//			return true;
//		}
//
//	};
}
