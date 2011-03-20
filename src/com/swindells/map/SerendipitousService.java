package com.swindells.map;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

public class SerendipitousService extends Service implements OnSharedPreferenceChangeListener, Observer
{
	public static String RUNNING_PREF = "ServiceRunning";
	public static String PROXIMITY_ALERT = "com.swindells.map.ProximityAlert";
	
	private static Random rng = new Random();
	
	private static int CHECK_EVERY = 30 * 1000;
	
	private static long[] DEFAULT_VIB = new long[] {500, 500, 500, 500, 500};
	
	private NotificationManager notificationManager;
	
	private HashMap<Integer, Long> lastUpdate = new HashMap<Integer, Long>();
	private HashMap<Integer, Float[]> distance = new HashMap<Integer, Float[]>();
	private SharedPreferences prefs;
	
	private SelectedLocationsDbAdapter db;
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate()
	{
		db = new SelectedLocationsDbAdapter(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String svcName = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(svcName);
		
		setup();
	}
	
	private void setup()
	{
		Cursor c = db.fetchAll();		
		
		if (c.getCount() > 0)
		{
			c.moveToFirst();
			while (!c.isAfterLast())
			{				
				int id = c.getInt(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_ROWID));
				lastUpdate.put(id, (long) 0);
				Float[] x = {(float) Float.MAX_VALUE, (float) Float.MAX_VALUE};
				distance.put(id, x);
				c.moveToNext();
			}
			
			showNotification();
			Editor e = prefs.edit();
			e.putBoolean(RUNNING_PREF, true);
			e.commit();

			subscribe();
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
		distance = null;
		lastUpdate = null;

		unsubscribe();
		
		Editor e = prefs.edit();
		e.putBoolean(RUNNING_PREF, false);
		e.commit();
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
	
	public void notify(int id, boolean moving_towards)
	{
		boolean vibrate = prefs.getBoolean("notify_vibration", true);
		boolean audio = prefs.getBoolean("notify_audible", false);
		
		Float dist = distance.get(id)[0];
		int range = Integer.parseInt(prefs.getString("notify_range", "100"));
		long[] vibration_pattern = DEFAULT_VIB;
		
		for (int i = 0; i < vibration_pattern.length; i = i + 2)
		{
			vibration_pattern[i] = (long) (vibration_pattern[i] * (dist / range));
			
			if (!moving_towards)
				vibration_pattern[i] = (long) (vibration_pattern[i] * rng.nextDouble());
		}
		
		Cursor c = db.fetch(id);
		c.moveToFirst();
		
		String name = c.getString(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_NAME));
		int lat = c.getInt(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_LATITUDE));
		int lng = c.getInt(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_LONGITUDE));
	
		int icon = R.drawable.marker;
		String tickerText = getString(R.string.proximity_notification);
		long when = System.currentTimeMillis();
		
		Toast.makeText(this, tickerText, Toast.LENGTH_SHORT).show();
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_INSISTENT;
		
		if (vibrate)
			notification.vibrate = vibration_pattern;
		if (audio)
		{
			notification.sound = Settings.System.DEFAULT_ALARM_ALERT_URI;
		}
		
		Intent i = new Intent(this, RouteTo.class);
		i.putExtra(RouteTo.NAME_KEY, name);
		i.putExtra(RouteTo.LAT_KEY, lat);
		i.putExtra(RouteTo.LONG_KEY, lng);
		i.putExtra(RouteTo.ID_KEY, id);
		
		PendingIntent launchIntent = PendingIntent.getActivity(getApplicationContext(), 0, i , 0);
		notification.setLatestEventInfo(getApplicationContext(), (CharSequence) tickerText, (CharSequence) getString(R.string.notification_subtext) + "\n" + name, launchIntent);
		
		notificationManager.notify(1, notification);
	}
	
	@Override
	public void update(Observable observable, Object data)
	{
		android.location.Location currentLoc = (android.location.Location) data;

		Cursor c = db.fetchAll();
		
		int range = Integer.parseInt(prefs.getString("notify_range", "100"));
		c.moveToFirst();
		int toNotify = 0;
		boolean inProximity = false;
		float nearest = Float.MAX_VALUE;
		
		boolean[] towards = new boolean[getMaxID(c) + 1]; 
		
		while (!c.isAfterLast())
		{
			if (db.count() == 0)
			{
				unsubscribe();
				break;
			}
			
			int lat = c.getInt(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_LATITUDE));
			int lng = c.getInt(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_LONGITUDE));
			
			Location l = new Location(lat, lng);
			
			int id = c.getInt(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_ROWID));
			
			long lockDelta = currentLoc.getTime() - lastUpdate.get(id);
			
			if (lockDelta >= CHECK_EVERY)
			{
				float dist = l.distanceTo(currentLoc);
				
				lastUpdate.put(id, currentLoc.getTime());
				
				Float[] distances = distance.get(id);
				if (distances[1] != Float.MAX_VALUE)
				{
					if (dist > distances[0] && distances[0] > distances[1])
						towards[id] = false;
					else if (dist < distances[0] && distances[0] < distances[1])
						towards[id] = true;
					
					if (dist < range && dist < nearest)
					{
						toNotify = id;
						inProximity = true;
						nearest = dist;
					}
				}
				
				distances[1] = distances[0];
				distances[0] = dist;
				distance.put(id, distances);
			}
			
			c.moveToNext();
		}
		
		if (inProximity)
			notify(toNotify, towards[toNotify]);
	}
	
	public void subscribe()
	{
		LocationNotifier lo = new NotifierFactory().setContext(this).getInstance();
		lo.addObserver(this);
	}
	
	public void unsubscribe()
	{
		LocationNotifier lo = new NotifierFactory().setContext(this).getInstance();
		lo.deleteObserver(this);
	}
	
	public int getMaxID(Cursor c)
	{
		if (c.moveToLast())
			return c.getInt(c.getColumnIndex(SelectedLocationsDbAdapter.KEY_ROWID));
		else
			return -1;
	}
}
