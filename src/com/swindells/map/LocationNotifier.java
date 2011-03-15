package com.swindells.map;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class LocationNotifier extends Observable implements LocationListener
{
	private static int MIN_TIME = 1000;
	private static int MIN_DISTANCE = 0;
	private static String PROVIDER = LocationManager.GPS_PROVIDER;
	
	private LocationManager lm;
	
	public LocationNotifier()
	{
		
	}
	
	public void setContext(Context ctx)
	{
		if (lm == null)
		{
			String serviceString = Context.LOCATION_SERVICE;
			lm = (LocationManager) ctx.getSystemService(serviceString);
		}
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		setChanged();
		if (countObservers() > 0)
			notifyObservers(location);
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	/* (non-Javadoc)
	 * @see java.util.Observable#addObserver(java.util.Observer)
	 */
	@Override
	public void addObserver(Observer observer)
	{
		if (lm != null)
		{
			if (countObservers() == 0)
				lm.requestLocationUpdates(PROVIDER, MIN_TIME, MIN_DISTANCE, this);
			super.addObserver(observer);
		}		
	}

	/* (non-Javadoc)
	 * @see java.util.Observable#deleteObserver(java.util.Observer)
	 */
	@Override
	public synchronized void deleteObserver(Observer observer)
	{
		if (lm != null)
		{
			super.deleteObserver(observer);
			
			if (countObservers() == 0)
				lm.removeUpdates(this);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observable#deleteObservers()
	 */
	@Override
	public synchronized void deleteObservers()
	{
		if (lm != null)
		{
			super.deleteObservers();
			lm.removeUpdates(this);
		}
	}
	
}
