package com.swindells.map;

import java.util.Observable;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationObservable extends Observable implements LocationListener
{
	private Location location;
	
	/* (non-Javadoc)
	 * @see java.util.Observable#notifyObservers()
	 */
	@Override
	public void notifyObservers()
	{
		super.setChanged();
		int o = countObservers();
		System.out.println(o);
		super.notifyObservers(location);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		this.location = location;
		notifyObservers();
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
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		notifyObservers();
	}

}
