package com.swindells.map;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class Preferences extends PreferenceActivity
{

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.prefs);
	}

}