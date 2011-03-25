package com.swindells.map;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddLocation extends Activity
{
	private SelectedLocationList mdbHelper;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mdbHelper = new SelectedLocationList(this);


		// mdbHelper = new LocationsDbAdapter(this);

		Bundle extras = getIntent().getExtras();
		Uri uri = getIntent().getData();
		
		int latitude, longitude;
		
		if (extras != null)
		{
			setContentView(R.layout.addlocation);
			TextView title = (TextView) findViewById(R.id.locationName);
			TextView more = (TextView) findViewById(R.id.locationName);
			
			String name = extras.getString(SelectedLocationList.KEY_NAME);
			String desc = extras.getString(SelectedLocationList.KEY_DESC);
			
			latitude = extras.getInt(SelectedLocationList.KEY_LATITUDE);
			longitude = extras.getInt(SelectedLocationList.KEY_LONGITUDE);
			title.setText(name);
			if (desc != null)
				more.setText(desc);
			
			final Location l = new Location(latitude, longitude);
			l.setText(name, desc);
			
			final Button yesButton = (Button) findViewById(R.id.add_confirm);
			yesButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mdbHelper.addSelection(l);
					finish();
				}
			});
		}
		else if (uri != null)
		{
			String lat = uri.getQueryParameter("lat");
			String lng = uri.getQueryParameter("lng");
			String name = uri.getQueryParameter("name");
			
			if (lat != null && lng != null && name != null) 
			{
				setContentView(R.layout.addlocation);
				latitude = (int) (Double.parseDouble(lat) * 1E6);
				longitude = (int) (Double.parseDouble(lng) * 1E6);
				
				name = name.replaceAll(Pattern.quote("+"), " ");
				
				TextView title = (TextView) findViewById(R.id.locationName);
				title.setText(name);
				
				final Location l = new Location(latitude, longitude);
				l.setText(name, null);
				
				final Button yesButton = (Button) findViewById(R.id.add_confirm);
				yesButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						mdbHelper.addSelection(l);
						finish();
					}
				});
			}
			else
			{
				AlertDialog.Builder ad = new AlertDialog.Builder(this);
				ad.setTitle("Invalid");
				ad.setMessage("Invalid Barcode!");
				ad.show();
			}
		}
		else
		{
			setContentView(R.layout.addlocation);
			finish();
		}
		
		/*final Button noButton = (Button) findViewById(R.id.add_decline);
		noButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				finish();				
			}
		});*/

	}

}
