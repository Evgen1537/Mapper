package com.evgenltd.mapper.core.importer;

import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 16-04-2016 19:08
 */
@XStreamAlias("MarkerCollection")
@ParametersAreNonnullByDefault
public class MarkerOld {

	private final List<MarkerIcon> markerIconList = new ArrayList<>();
	private final List<Marker> markerList = new ArrayList<>();

	public void addMarkerIcon(final MarkerIcon markerIcon)	{
		markerIconList.add(markerIcon);
	}

	public void addMarker(final Marker marker)	{
		markerList.add(marker);
	}

	public List<MarkerIcon> getMarkerIconList() {
		return Collections.unmodifiableList(markerIconList);
	}

	public List<Marker> getMarkerList() {
		return Collections.unmodifiableList(markerList);
	}
}
