package com.swindells.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class RouteTo extends Activity
{
	public static String NAME_KEY = "LOCATION_NAME";
	public static String LAT_KEY = "LATITUDE";
	public static String LONG_KEY = "LONGITUDE";
	public static String ID_KEY = "ID";
	
	private int lat;
	private int lng;
	private int id;
	
	private Context ctx = this;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		String name = extras.getString(NAME_KEY);
		lat = extras.getInt(LAT_KEY);
		lng = extras.getInt(LONG_KEY);
		id = extras.getInt(ID_KEY);
		
		builder.setMessage(getString(R.string.directions_question) + " " + name + "?");
		builder.setTitle(name);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:ll=" + lat / 1E6 + "," + lng / 1E6 + "&mode=w"));
				startActivity(i);
			}
		});
		
		builder.setNeutralButton("No", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();				
			}
		});
		
		builder.setNegativeButton("Stop notifications", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SelectedLocationsDbAdapter db = new SelectedLocationsDbAdapter(ctx);
				db.remove(id);
				finish();				
			}
		});
		
		builder.show();
	}
}
