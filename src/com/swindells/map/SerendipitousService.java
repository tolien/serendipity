package com.swindells.map;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class SerendipitousService extends Service implements OnSharedPreferenceChangeListener
{
	private NotificationManager notificationManager;
	private ArrayList<Location> locations = new ArrayList<Location>();
	private SharedPreferences prefs;
	
	private SelectedLocationsDbAdapter db;
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate()
	{
		db = new SelectedLocationsDbAdapter(this);
		db.open();
		Cursor c = db.fetchAll();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		int range = prefs.getInt("notify_range", 25);
		
		String svcName = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(svcName);
		
		if (c.getCount() > 0)
		{
			c.moveToFirst();
			while (!c.isAfterLast())
			{
				int latIdx = c.getColumnIndex(SelectedLocationsDbAdapter.KEY_LATITUDE);
				int lat = c.getInt(latIdx);
				int lngIdx = c.getColumnIndex(SelectedLocationsDbAdapter.KEY_LONGITUDE);
				int lng = c.getInt(lngIdx);
				
				String name = c.getString(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_NAME));
				String desc = c.getString(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_DESC));
				
				Location l = new Location(lat, lng);
				l.setText(name, desc);
				locations.add(l);
				
				c.moveToNext();
			}
			showNotification();			
		}
		else
		{
			shutDown();
			stopSelf();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;
	}

	public void showNotification()
	{
		int icon = R.drawable.marker;
		String tickerText = getString(R.string.service_started_notification_text);
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		PendingIntent launchIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(SerendipitousService.this, MapInput.class), 0);
		notification.setLatestEventInfo(getApplicationContext(), (CharSequence) tickerText, (CharSequence) "", launchIntent);
		
		notificationManager.notify(1, notification);
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void shutDown()
	{
		notificationManager.cancelAll();
		db.removeAll();
		db.close();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		shutDown();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
		prefs = sharedPreferences;
		
	}

}
