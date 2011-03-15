package com.swindells.map;

import com.google.android.maps.GeoPoint;

public class Location
{
	private android.location.Location location;
	private String title = null;
	private String description = null;
	
	public Location(int lat, int lng)
	{
		location = new android.location.Location("");
		location.setLatitude(lat / 1E6);
		location.setLongitude(lng / 1E6);
	}
	
	public int getLatitude()
	{
		return (int) (location.getLatitude() * 1E6);
	}
	
	public int getLongitude()
	{
		return (int) (location.getLongitude() * 1E6);
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setText(String title, String desc)
	{
		this.title = title;
		this.description = desc;
	}
	
	public String toString()
	{
		return getTitle() + " at (" + getLatitude() + ", " + getLongitude() + ")";
	}
	
	public float distanceTo(android.location.Location l)
	{
		return location.distanceTo(l);
	}
}
