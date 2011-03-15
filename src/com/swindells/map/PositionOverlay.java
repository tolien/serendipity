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
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 */
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if (location != null)
		{
			Projection projection = mapView.getProjection();
			float accuracy = location.getAccuracy();
			accuracy = 3 * projection.metersToEquatorPixels(accuracy);
			
			Double lat = location.getLatitude() * 1E6;
			Double lon = location.getLongitude() * 1E6;
			
			GeoPoint geoPoint = new GeoPoint(lat.intValue(), lon.intValue());
			Point point = new Point();
			projection.toPixels(geoPoint, point);
			
			Paint paint = new Paint();
			paint.setARGB(50, 0, 0, 0);
			paint.setAntiAlias(true);
			
			canvas.drawCircle(point.x, point.y, Math.max(20, accuracy), paint);
		}
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
}
