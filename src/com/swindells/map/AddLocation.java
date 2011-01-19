package com.swindells.map;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddLocation extends Activity
{
	private LocationsDbAdapter mdbHelper;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// mdbHelper = new LocationsDbAdapter(this);

		setContentView(R.layout.addlocation);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{

			TextView title = (TextView) findViewById(R.id.locationName);
			String locName = extras.getString(LocationsDbAdapter.KEY_DESC);
			title.setText(locName);
		}
		
		final Button button = (Button) findViewById(R.id.add_decline);
		button.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				finish();				
			}
		});

	}

}
