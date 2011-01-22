package com.swindells.map;

import java.util.HashMap;

import com.google.android.maps.GeoPoint;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class SerendipitousService extends Service
{
	private NotificationManager notificationManager;
	private HashMap<GeoPoint, String> locations = new HashMap<GeoPoint, String>();
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate()
	{
		showNotification();
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
		String svcName = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(svcName);
		
		int icon = R.drawable.marker;
		String tickerText = "Guide Service started";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		PendingIntent launchIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(SerendipitousService.this, MapDemo.class), 0);
		notification.setLatestEventInfo(getApplicationContext(), (CharSequence) tickerText, (CharSequence) "", launchIntent);
		
		notificationManager.notify(1, notification);
		
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
