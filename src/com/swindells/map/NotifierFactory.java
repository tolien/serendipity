package com.swindells.map;

import android.content.Context;

public class NotifierFactory
{
	private static final LocationNotifier ln = new LocationNotifier();
	
	public NotifierFactory setContext(Context c)
	{
		ln.setContext(c);
		return this;
	}
	
	public LocationNotifier getInstance()
	{
		return ln;
	}
}
