package com.swindells.map;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class VisitableList extends ItemizedOverlay<OverlayItem>
{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	public VisitableList(Drawable marker)
	{
		super(boundCenterBottom(marker));
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

}
