package com.swindells.map;

import com.google.android.maps.GeoPoint;

public class Location
{
	private GeoPoint location;
	private String title = null;
	private String description = null;
	
	public Location(int lat, int lng)
	{
		location = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
	}
	
	public int getLatitude()
	{
		return location.getLatitudeE6();
	}
	
	public int getLongitude()
	{
		return location.getLongitudeE6();
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
}
