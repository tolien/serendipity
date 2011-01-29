package com.swindells.map;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddLocation extends Activity
{
	private SelectedLocationsDbAdapter mdbHelper;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mdbHelper = new SelectedLocationsDbAdapter(this);
		mdbHelper.open();


		// mdbHelper = new LocationsDbAdapter(this);

		setContentView(R.layout.addlocation);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			TextView title = (TextView) findViewById(R.id.locationName);
			
			String name = extras.getString(SelectedLocationsDbAdapter.KEY_NAME);
			String desc = extras.getString(SelectedLocationsDbAdapter.KEY_DESC);
			
			int latitude = extras.getInt(SelectedLocationsDbAdapter.KEY_LATITUDE);
			int longitude = extras.getInt(SelectedLocationsDbAdapter.KEY_LONGITUDE);
			title.setText(desc);
			
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
		
		final Button noButton = (Button) findViewById(R.id.add_decline);
		noButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				finish();				
			}
		});

	}

}
