package com.lpi.itineraires;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lpi.itineraires.database.ItinerairesDatabase;
import com.lpi.itineraires.details.ClickPositionHandler;
import com.lpi.itineraires.itineraire.Itineraire;
import com.lpi.itineraires.itineraire.Position;
import com.lpi.itineraires.linegraphview.ZoomableLinegraphView;
import com.lpi.itineraires.utils.DoubleHolder;
import com.lpi.itineraires.utils.FloatHolder;
import com.lpi.itineraires.utils.Preferences;
import com.lpi.itineraires.utils.Utils;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ClickPositionHandler.positionRefreshListener
{
	public static final String EXTRA_RANDO_ID = MapsActivity.class.getCanonicalName() + ".randoId";


	private TYPE_AFFICHAGE _typeAffichage;
	private Polyline _mapPolyline;
	final double GLOBE_WIDTH = 256; // a constant in Google's map projection
	final double LN2 = 0.6931471805599453;
	private static final long MINUTE = 60;
	private static final long HEURE = MINUTE * 60;
	private static final long JOUR = HEURE * 24;

	private GoogleMap mMap;
	private Itineraire _itineraire;
	SupportMapFragment mapFragment;
	private TextView _textDetails;
	private ZoomableLinegraphView _vitesseTemps, _altitudeTemps, _distanceTemps, _vitesseVertTemps;
	private FloatHolder _vitesseMin = new FloatHolder();
	private FloatHolder _vitesseMax = new FloatHolder();
	private DoubleHolder _altitudeMin = new DoubleHolder();
	private DoubleHolder _altitudeMax = new DoubleHolder();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Utils.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		Bundle b = getIntent().getExtras();
		if (b != null)
		{
			int id = b.getInt(EXTRA_RANDO_ID);
			_itineraire = ItinerairesDatabase.getInstance(this).getItineraire(id);
			toolbar.setTitle(_itineraire.Nom);
			setTitle(_itineraire.Nom);
		}


		mapFragment.getMapAsync(this);
		_textDetails = findViewById(R.id.textViewDescription);
		_textDetails.setText(_itineraire.getDetails(this, _vitesseMin, _vitesseMax, _altitudeMin, _altitudeMax));
		_vitesseTemps = findViewById(R.id.ilgvVitesseTemps);
		_vitesseVertTemps = findViewById(R.id.ilgvVitesseVTemps);
		_altitudeTemps = findViewById(R.id.ilgvAltitudeTemps);
		_distanceTemps = findViewById(R.id.ilgvDistanceTemps);

		_typeAffichage = TYPE_AFFICHAGE.fromInt(Preferences.getInstance(this).getAffichageDetails());
		majUI();

		Snackbar.make(mapFragment.getView(), R.string.map_activity_advice
				, Snackbar.LENGTH_LONG).show();
	}

	/***
	 * Demarre l'activity avec les parametres necessaires
	 * @param context
	 * @param id
	 */
	public static void start(final Context context, final int id)
	{
		Intent intent = new Intent(context, MapsActivity.class);
		Bundle b = new Bundle();
		b.putInt(EXTRA_RANDO_ID, id);
		intent.putExtras(b);
		context.startActivity(intent);
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;

		if (_itineraire != null)
		{
			Cursor cursor = ItinerairesDatabase.getInstance(this).getPositions(_itineraire.Id);
			if (null != cursor)
				if (cursor.getCount() > 0)
				{
					//PolylineOptions polyline = new PolylineOptions();
					double latMin = Double.MAX_VALUE;
					double latMax = Double.MIN_VALUE;
					double longMin = Double.MAX_VALUE;
					double longMax = Double.MIN_VALUE;
					Position precedente = null;

					while (cursor.moveToNext())
					{
						Position position = new Position(cursor);

						//polyline.add(position.toLatLng());

						if (position.getLatitude() < latMin) latMin = position.getLatitude();
						if (position.getLatitude() > latMax) latMax = position.getLatitude();
						if (position.getLongitude() < longMin)
							longMin = position.getLongitude();
						if (position.getLongitude() > longMax)
							longMax = position.getLongitude();

						Log.d("PROVIDER", position.getProvider());
						Marker m = googleMap.addMarker(new MarkerOptions()

								.position(position.toLatLng())
								.title(position.getAltitude() + "m")
								.icon(BitmapDescriptorFactory.defaultMarker(position.getProvider().equals("gps") ? BitmapDescriptorFactory.HUE_AZURE : BitmapDescriptorFactory.HUE_ROSE)));
						m.setTag(position);
						mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
						{
							@Override
							public boolean onMarkerClick(final Marker marker)
							{
								Object o = marker.getTag();
								if (o instanceof Position)
								{
									ClickPositionHandler.handleClickPosition(marker, (Position) o, MapsActivity.this, MapsActivity.this);
									return true;
								}

								return false;
							}
						});
						if (precedente != null)
						{
							PolylineOptions po = new PolylineOptions();
							po.add(precedente.toLatLng());
							po.add(position.toLatLng());
							po.color(getColor(position.getSpeed(), _vitesseMin.getValeur(), _vitesseMax.getValeur(), Color.RED, Color.GREEN));
							googleMap.addPolyline(po);
						}
						precedente = position;
					}
					//polyline.color(Color.BLUE);
					//polyline.width(7.5f);

					//_mapPolyline = googleMap.addPolyline(polyline);

					final LatLng centre = new LatLng((latMin + latMax) / 2.0, (longMin + longMax) / 2.0);
					float zoom = getNiveauZoom(latMin, longMin, latMax, longMax);//Preferences.getInstance(this).getNiveauZoom();
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centre, zoom));
				}
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		majUI();
	}

	private void majUI()
	{
		switch (_typeAffichage)
		{
			case CARTE:
				mapFragment.getView().setVisibility(View.VISIBLE);
				mapFragment.getView().setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_in));
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.GONE);
				_vitesseVertTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.GONE);
				break;
			case DETAILS:
				mapFragment.getView().setVisibility(View.GONE);
				_textDetails.setVisibility(View.VISIBLE);
				_textDetails.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_in));
				_vitesseTemps.setVisibility(View.GONE);
				_vitesseVertTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.GONE);
				break;
			case VITESSE_TEMPS:
				mapFragment.getView().setVisibility(View.GONE);
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.VISIBLE);
				_vitesseTemps.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_in));
				_vitesseVertTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.GONE);
				break;
			case DISTANCE_TEMPS:
				mapFragment.getView().setVisibility(View.GONE);
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.GONE);
				_vitesseVertTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.VISIBLE);
				_distanceTemps.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_in));
				break;
			case ALTITUDE_TEMPS:
				mapFragment.getView().setVisibility(View.GONE);
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.GONE);
				_vitesseVertTemps.setVisibility(View.GONE);
				_altitudeTemps.setVisibility(View.VISIBLE);
				_altitudeTemps.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_in));
				_distanceTemps.setVisibility(View.GONE);
				break;
			case VITESSE_V_TEMPS:
				mapFragment.getView().setVisibility(View.GONE);
				_textDetails.setVisibility(View.GONE);
				_vitesseTemps.setVisibility(View.GONE);
				_vitesseVertTemps.setVisibility(View.VISIBLE);
				_vitesseVertTemps.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_in));
				_altitudeTemps.setVisibility(View.GONE);
				_distanceTemps.setVisibility(View.GONE);
				break;
		}
	}

	private int getColor(final float speed, final float vitesseMin, final float vitesseMax, final int colorMin, final int colorMax)
	{
		int r = composante(speed, vitesseMin, vitesseMax, Color.red(colorMin), Color.red(colorMax));
		int g = composante(speed, vitesseMin, vitesseMax, Color.green(colorMin), Color.green(colorMax));
		int b = composante(speed, vitesseMin, vitesseMax, Color.blue(colorMin), Color.blue(colorMax));

		return Color.rgb(r, g, b);
	}

	private int composante(final float speed, final float vitesseMin, final float vitesseMax, final int min, final int max)
	{
		int res = (int) (min + (speed - vitesseMin) / (vitesseMax - vitesseMin) * (max - min));
		if (res < 0)
			res = 0;
		if (res > 255)
			res = 255;
		return res;
	}

	/***
	 * Calculer un niveau de zoom adequat pour afficher la totalite du parcours
	 * https://stackoverflow.com/questions/6048975/google-maps-v3-how-to-calculate-the-zoom-level-for-a-given-bounds
	 * @param south
	 * @param west
	 * @param north
	 * @param east
	 * @return
	 */
	private float getNiveauZoom(final double south, final double west, final double north, final double east)
	{
		double zoom;

		double angle = east - west;
		double angle2 = north - south;
		double delta = 0;

		if (angle2 > angle)
		{
			angle = angle2;
			delta = 3;
		}

		while (angle < 0)
			angle += 360;

		zoom = Math.floor(Math.log(960 * 360 / angle / GLOBE_WIDTH) / LN2) - 2 - delta;
		if (zoom < 1)
			zoom = 1;
		if (zoom > 15)
			zoom = 15;

		return (float) zoom;
	}

	/**
	 * Selection dans le menu de l'activity
	 *
	 * @param item
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(@Nullable MenuItem item)
	{
		if (item != null)
			switch (item.getItemId())
			{
				case R.id.action_affiche_carte_routes:
					_typeAffichage = TYPE_AFFICHAGE.CARTE;
					mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					break;
				case R.id.action_affiche_carte_satellite:
					_typeAffichage = TYPE_AFFICHAGE.CARTE;
					mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
					break;
				case R.id.action_affiche_carte_hybride:
					_typeAffichage = TYPE_AFFICHAGE.CARTE;
					mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					break;
				case R.id.action_affiche_carte_terrain:
					_typeAffichage = TYPE_AFFICHAGE.CARTE;
					mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
					break;
				case R.id.action_affiche_details:
					_typeAffichage = TYPE_AFFICHAGE.DETAILS;
					break;
				case R.id.action_affiche_vitesse_temps:
					_typeAffichage = TYPE_AFFICHAGE.VITESSE_TEMPS;
					break;
				case R.id.action_affiche_vitessevert_temps:
					_typeAffichage = TYPE_AFFICHAGE.VITESSE_V_TEMPS;
					break;
				case R.id.action_affiche_distance_temps:
					_typeAffichage = TYPE_AFFICHAGE.DISTANCE_TEMPS;
					break;
				case R.id.action_affiche_altitude_temps:
					_typeAffichage = TYPE_AFFICHAGE.ALTITUDE_TEMPS;
					break;

				case android.R.id.home:
					finish();
					return true;

				default:
					return super.onOptionsItemSelected(item);
			}
		majUI();
		Preferences.getInstance(this).setAffichageDetails(_typeAffichage.getValue());
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creation du menu de l'activity
	 *
	 * @param menu
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_map, menu);

		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onClickPositionHandlerPositionSupprimee(Marker marker, Position posSupprimee)
	{
		marker.remove();
		//_mapPolyline.
	}

	enum TYPE_AFFICHAGE
	{
		CARTE(0), DETAILS(1), VITESSE_TEMPS(2), DISTANCE_TEMPS(3), ALTITUDE_TEMPS(4), VITESSE_V_TEMPS(5);
		private int _value;

		TYPE_AFFICHAGE(final int i)
		{
			_value = i;
		}

		public int getValue()
		{
			return _value;
		}

		public static TYPE_AFFICHAGE fromInt(int i)
		{
			for (TYPE_AFFICHAGE b : TYPE_AFFICHAGE.values())
			{
				if (b.getValue() == i)
				{
					return b;
				}
			}
			return CARTE;
		}
	}

}
