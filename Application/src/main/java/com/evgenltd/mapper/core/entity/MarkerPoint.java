package com.evgenltd.mapper.core.entity;

import java.util.Comparator;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 22:55
 */
public interface MarkerPoint extends Identified,Ordered {

	Comparator<MarkerPoint> MARKER_POINT_COMPARATOR = Comparator.comparing(MarkerPoint::getOrderNumber);

	Double getX();

	void setX(Double x);

	Double getY();

	void setY(Double y);

	Marker getMarker();

	void setMarker(Marker marker);
}
