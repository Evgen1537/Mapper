package com.evgenltd.mapper.mapviewer.util;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Polygon2D;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-07-2016 21:22
 */
public class Geometry {

	public static boolean intersect(
			@NotNull final Polygon2D polygonFirst,
			@NotNull final Polygon2D polygonSecond
	)	{

		for(final Point2D point : polygonFirst.vertices()) {
			if(polygonSecond.contains(point))	{
				return true;
			}
		}

		for(final Point2D point : polygonSecond.vertices()) {
			if(polygonFirst.contains(point))	{
				return true;
			}
		}

		return intersect(polygonFirst.edges(), polygonSecond.edges());

	}

	public static boolean intersect(
			@NotNull final Collection<? extends LineSegment2D> first,
			final @NotNull Collection<? extends LineSegment2D> second
	)	{

		for(LineSegment2D firstSegment : first) {
			for(LineSegment2D secondSegment : second) {
				if(firstSegment.intersection(secondSegment) != null)	{
					return true;
				}
			}
		}
		return false;

	}

}
