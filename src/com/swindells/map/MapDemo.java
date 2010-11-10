package com.swindells.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.*;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MapDemo extends MapActivity
{

	private static final int INSERT_ID = Menu.FIRST;
	private static final int CLEAR_ID = Menu.FIRST + 1;
	
	private MapController mapController;
	private Double lat;
	private Double lng;
	private TextView myLocationText;
	private PositionOverlay positionOverlay;
	
	private LocationManager locationManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		MapView myMapView = (MapView) findViewById(R.id.myMapView);
		myLocationText = (TextView)findViewById(R.id.myLocationText);
		
		mapController = myMapView.getController();
		List<Overlay> overlays = myMapView.getOverlays();
		
		positionOverlay = new PositionOverlay();
		overlays.add(positionOverlay);
		myMapView.postInvalidate();
	
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, myMapView);
		overlays.add(myLocationOverlay);
		//myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		
		String serviceString = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(serviceString);
		LocationListener myLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location)
			{
				updateLocation();
			}

			@Override
			public void onProviderDisabled(String provider)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras)
			{
				updateLocation();
				
			}
			
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, myLocationListener);
		updateLocation();
	}
	public void updateLocation() {
		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		
		if (location != null)
		{
			lat = location.getLatitude();
			lng = location.getLongitude();
			positionOverlay.setLocation(location);
			
		}
		else
		{
			lat = 0.0;
			lng = 0.0;
		}
		
		Double latitude = lat * 1E6;
		Double longitude = lng * 1E6;
		GeoPoint point = new GeoPoint(latitude.intValue(), longitude.intValue());
		mapController.setCenter(point);
		mapController.setZoom(20);
		Geocoder gc = new Geocoder(this, Locale.UK);
		List<Address> addresses = null;		
		
		StringBuilder sb = new StringBuilder();
		
		 try {
			addresses = gc.getFromLocation(lat, lng, 10);
		}
		catch(IOException e) {
			sb.append("Lookup failed").append("\n").append(e.getMessage());
		}
		if (addresses != null && addresses.size() > 0)
		{
			Address address = addresses.get(0);
			
			for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
			{
				sb.append(address.getAddressLine(i)).append("\n");
			}
			
		//	sb.append(address.getLocality()).append("\n");
		//	sb.append(address.getPostalCode()).append("\n");
			sb.append(location.getAccuracy()).append("\n");
		}
		
		String latLongString = "Latitude: " + lat + "\nLongitude: " + lng;
		myLocationText.setText("Current Pos:\n" + latLongString + "\n" + sb.toString());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_loc_insert);
		menu.add(0, CLEAR_ID, 0, R.string.menu_loc_clear);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
}