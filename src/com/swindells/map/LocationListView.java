package com.swindells.map;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LocationListView extends ListActivity
{
	private LocationList db;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.locationlist);
		
		db = new LocationList(this);
		db.open();
		Cursor c = db.fetchAll();
		startManagingCursor(c);
		
		ListAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, new String[] {SelectedLocationList.KEY_NAME}, new int[] {android.R.id.text1});
		
		setListAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		final Intent addIntent = new Intent(this, AddLocation.class);
		
		Cursor c = db.fetch((int) id);
		c.moveToFirst();

		int latIdx = c.getColumnIndex(LocationList.KEY_LATITUDE);
		int lat = c.getInt(latIdx);
		int lngIdx = c.getColumnIndex(LocationList.KEY_LONGITUDE);
		int lng = c.getInt(lngIdx);
		
		String name = c.getString(c.getColumnIndex(LocationList.KEY_NAME));
		String desc = c.getString(c.getColumnIndex(LocationList.KEY_DESC));
		
		addIntent.putExtra(SelectedLocationList.KEY_NAME, name);
		addIntent.putExtra(SelectedLocationList.KEY_DESC, desc);
		addIntent.putExtra(SelectedLocationList.KEY_LATITUDE, lat);
		addIntent.putExtra(SelectedLocationList.KEY_LONGITUDE, lng);
		
		startActivity(addIntent);
	}
}
