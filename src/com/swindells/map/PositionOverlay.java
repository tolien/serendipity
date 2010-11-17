package com.swindells.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PositionOverlay extends Overlay
{
	Location location;
	private static int radius = 5;
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if (location != null)
		{
			Projection projection = mapView.getProjection();
			float accuracy = 50;
			accuracy = projection.metersToEquatorPixels(accuracy);
			
			Double lat = location.getLatitude() * 1E6;
			Double lon = location.getLongitude() * 1E6;
			
			GeoPoint geoPoint = new GeoPoint(lat.intValue(), lon.intValue());
			Point point = new Point();
			projection.toPixels(geoPoint, point);
			
			Paint paint = new Paint();
			paint.setARGB(50, 0, 0, 0);
			paint.setAntiAlias(true);
			
			canvas.drawCircle(point.x, point.y, accuracy + radius, paint);
			
			super.draw(canvas, mapView, shadow);
		}
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
	 */
	@Override
	public boolean onTap(GeoPoint p, MapView mapView)
	{
		// TODO Auto-generated method stub
		return super.onTap(p, mapView);
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
}
