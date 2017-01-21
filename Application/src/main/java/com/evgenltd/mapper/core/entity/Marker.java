package com.evgenltd.mapper.core.entity;

import com.evgenltd.mapper.core.enums.MarkerType;
import com.evgenltd.mapper.core.enums.Visibility;

import java.util.Collection;
import java.util.Set;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 22:36
 */
public interface Marker extends Identified,Movable {
	
	MarkerType getType();
	void setType(MarkerType type);

	MarkerIcon getMarkerIcon();
	Long getMarkerIconId();
	void setMarkerIcon(MarkerIcon markerIcon);

	Layer getLayer();
	Long getLayerId();
	void setLayer(Layer layer);

	Layer getExit();
	Long getExitId();
	void setExit(Layer exit);

	String getEssence();
	void setEssence(String essence);

	String getSubstance();
	void setSubstance(String substance);

	String getVitality();
	void setVitality(String vitality);

	String getName();

	String getComment();
	void setComment(String comment);

	Visibility getVisibility();
	boolean isVisible();

	Collection<MarkerPoint> getMarkerPointList();
	void setMarkerPointList(Set<MarkerPoint> markerPointList);
	
}
