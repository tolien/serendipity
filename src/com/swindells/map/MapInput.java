package com.swindells.map;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.google.android.maps.*;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapInput extends MapActivity implements Observer
{
	private static final int GO_ID = Menu.FIRST;
	private static final int STOP_ID = Menu.FIRST;
	private static final int CENTER_ID = Menu.FIRST + 1;
	
	public static final int ACTIVITY_ADD = 0;

	private MapController mapController;
	private PositionOverlay positionOverlay;

	private LocationManager locationManager;
	private LocationObservable myLocationListener;
	
	private boolean starting;

	private boolean serviceRunning;
	
	private int zoomLevel = 20;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map);

		MapView myMapView = (MapView) findViewById(R.id.myMapView);
		mapController = myMapView.getController();
		List<Overlay> overlays = myMapView.getOverlays();

		positionOverlay = new PositionOverlay();
		overlays.add(positionOverlay);
		myMapView.setSatellite(false);
		myMapView.postInvalidate();

		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this,
				myMapView);
		overlays.add(myLocationOverlay);
		myMapView.setBuiltInZoomControls(true);
		mapController.setZoom(zoomLevel);

		String serviceString = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(serviceString);
		myLocationListener = new LocationObservable();
		myLocationListener.addObserver(this);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				120000, 100, myLocationListener);

		Location lastKnownLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		updateLocation(lastKnownLocation);

		VisitableList places = new VisitableList(getResources().getDrawable(
				R.drawable.marker), this);
		
		if (lastKnownLocation != null)
		{
			GeoPoint location = new GeoPoint(
					(int) (lastKnownLocation.getLatitude() * 1E6),
					(int) (lastKnownLocation.getLongitude() * 1E6));
			String snippet = location.getLatitudeE6() + ", " + location.getLongitudeE6();
			snippet += "\nLast known location at " + lastKnownLocation.getTime();
			OverlayItem i = new OverlayItem(location, "Location", snippet);
			places.addOverlay(i);
			overlays.add(places);
		}
	}

	public void updateLocation(Location location)
	{
		if (location != null)
		{
			positionOverlay.setLocation(location);
			
			if (!starting)
			{
				GeoPoint p = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
				mapController.animateTo(p);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		locationManager.removeUpdates(myLocationListener);
		zoomLevel = ((MapView) findViewById(R.id.myMapView)).getZoomLevel();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#onPause()
	 */
	@Override
	protected void onPause()
	{
		locationManager.removeUpdates(myLocationListener);
		zoomLevel = ((MapView) findViewById(R.id.myMapView)).getZoomLevel();
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	@Override
	protected void onResume()
	{
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				120000, 100, myLocationListener);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(0, CENTER_ID, 0, R.string.menu_map_center);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if (!serviceRunning)
		{
			menu.removeItem(GO_ID);
			menu.add(0, GO_ID, 1, R.string.menu_start_service);
		}
		else
		{
			menu.removeItem(GO_ID);
			menu.add(0, STOP_ID, 1, R.string.menu_stop_service);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		int id = item.getItemId();
		if (id == CENTER_ID)
		{
			Location lastKnownLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			updateLocation(lastKnownLocation);
		}
		else if (id == GO_ID && !serviceRunning)
		{
			startService(new Intent(this, SerendipitousService.class));
			serviceRunning = true;
		}
		else if (id == STOP_ID && serviceRunning)
		{
			stopService(new Intent(this, SerendipitousService.class));
			serviceRunning = false;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		updateLocation((Location) arg1);
	}

}