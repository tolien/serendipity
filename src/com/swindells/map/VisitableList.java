package com.swindells.map;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class VisitableList extends ItemizedOverlay<OverlayItem>
{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private MapActivity ac;
	private PopupPanel panel;

	public VisitableList(Drawable marker, MapActivity ac)
	{
		super(boundCenterBottom(marker));
		this.ac = ac;
	}

	@Override
	protected OverlayItem createItem(int i)
	{
		return mOverlays.get(i);
	}

	@Override
	public int size()
	{
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay)
	{
		mOverlays.add(overlay);
		populate();
	}

	@Override
	public boolean onTap(int i)
	{
		OverlayItem oi = mOverlays.get(i);
		
		if (panel != null)
			panel.hide();
		
		panel = new PopupPanel(oi);		
		panel.show();
		
		return true;
	}

	public class PopupPanel
	{
		private ViewGroup parent;
		private View popup;
		private GeoPoint location;
		
		public PopupPanel(OverlayItem oi)
		{
			this.parent = (MapView) ac.findViewById(R.id.myMapView);
			
			LayoutInflater inflater = (LayoutInflater) ac
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			popup = inflater.inflate(R.layout.mapbubble, parent, false);
			
			View view = getView();
			
			((TextView) view.findViewById(R.id.bubbleTitle)).setText(oi.getTitle(), TextView.BufferType.SPANNABLE);
			((TextView) view.findViewById(R.id.bubbleText)).setText(oi.getSnippet(), TextView.BufferType.SPANNABLE);
			TextView bubbleText = (TextView) view.findViewById(R.id.bubbleText);
			Spannable str = (Spannable) bubbleText.getText();
			str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
					str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			if (oi.getSnippet() != null)
				bubbleText.setText(oi.getSnippet());
			
			location = oi.getPoint();
			
			final Intent addIntent = new Intent(ac, AddLocation.class);
			addIntent.putExtra(SelectedLocationsDbAdapter.KEY_NAME, oi.getTitle());
			addIntent.putExtra(SelectedLocationsDbAdapter.KEY_DESC, oi.getSnippet());
			addIntent.putExtra(SelectedLocationsDbAdapter.KEY_LATITUDE, oi.getPoint().getLatitudeE6());
			addIntent.putExtra(SelectedLocationsDbAdapter.KEY_LONGITUDE, oi.getPoint().getLongitudeE6());
			
			popup.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					hide();
					
			        ac.startActivityForResult(addIntent, MapInput.ACTIVITY_ADD);
				}
			});
		}
		
		public void show()
		{
			popup.setBackgroundColor(Color.WHITE);

			MapView.LayoutParams lp = new MapView.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					location,
					MapView.LayoutParams.CENTER_HORIZONTAL);

			parent.addView(popup, lp);
		}
		
		public void hide()
		{
			parent.removeView(popup);
		}
		
		public View getView()
		{
			return popup;
		}
	}

}
