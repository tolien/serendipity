package com.swindells.map;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.google.android.maps.*;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MapDemo extends MapActivity implements Observer
{

	private static final int INSERT_ID = Menu.FIRST;
	private static final int CLEAR_ID = Menu.FIRST + 1;
	private static final int CENTER_ID = Menu.FIRST + 2;

	private MapController mapController;
	private Double lat;
	private Double lng;
	private TextView myLocationText;
	private PositionOverlay positionOverlay;

	private LocationManager locationManager;
	private LocationObservable myLocationListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		MapView myMapView = (MapView) findViewById(R.id.myMapView);
		myLocationText = (TextView) findViewById(R.id.myLocationText);

		mapController = myMapView.getController();
		List<Overlay> overlays = myMapView.getOverlays();

		positionOverlay = new PositionOverlay(this.getResources().getDrawable(R.drawable.marker));
		overlays.add(positionOverlay);
		myMapView.postInvalidate();

		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this,
				myMapView);
		overlays.add(myLocationOverlay);
		myMapView.setBuiltInZoomControls(true);

		String serviceString = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(serviceString);
		myLocationListener = new LocationObservable();
		myLocationListener.addObserver(this);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				120000, 100, myLocationListener);
		
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		updateLocation(lastKnownLocation);
		
		VisitableList places = new VisitableList(getResources().getDrawable(R.drawable.marker));
		GeoPoint location = new GeoPoint((int) (lastKnownLocation.getLatitude() * 1E6), (int) (lastKnownLocation.getLongitude() * 1E6));
		OverlayItem i = new OverlayItem(location, "", "");
		places.addOverlay(i);
		overlays.add(places);
	}

	public void updateLocation(Location location)
	{
		if (location != null)
		{
			lat = location.getLatitude();
			lng = location.getLongitude();
			positionOverlay.setLocation(location);

			Double latitude = lat * 1E6;
			Double longitude = lng * 1E6;
			GeoPoint point = new GeoPoint(latitude.intValue(),
					longitude.intValue());
			mapController.setCenter(point);
			mapController.setZoom(17);

			StringBuilder sb = new StringBuilder();

			sb.append(location.getAccuracy() + " metres").append("\n");
			float lastUpdated = (System.currentTimeMillis() - location.getTime()) / 1000;
			
			if (lastUpdated >= 60 * 60)
			{
				lastUpdated /= (60 * 60);
				sb.append("Last Updated: " + lastUpdated + " hours ago").append("\n");
			}
			else if (lastUpdated >= 60)
			{
				lastUpdated /= 60.;
				sb.append("Last Updated: " + lastUpdated + " minutes ago").append("\n");
			}
			else
			{
				sb.append("Last Updated: " + lastUpdated + " seconds ago").append("\n");				
			}

			String latLongString = "Latitude: " + lat + "\nLongitude: " + lng;
			myLocationText.setText(latLongString + "\n"
					+ sb.toString());
		}

	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		locationManager.removeUpdates(myLocationListener);
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onPause()
	 */
	@Override
	protected void onPause()
	{
		locationManager.removeUpdates(myLocationListener);
		super.onPause();
	}

	/* (non-Javadoc)
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
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, CENTER_ID, 0, R.string.menu_map_center);
		menu.add(0, INSERT_ID, 0, R.string.menu_loc_insert);
		menu.add(0, CLEAR_ID, 0, R.string.menu_loc_clear);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		int id = item.getItemId();
		if (id == CENTER_ID)
		{
			Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			updateLocation(lastKnownLocation);			
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
		updateLocation((Location)arg1);
	}

}