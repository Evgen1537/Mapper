package com.evgenltd.mapper.ui.util;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.polygon.Rectangle2D;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 17-06-2016 19:13
 */
public class Geometry {

	public static Collection<LineSegment2D> getRectangleSegments(
			final double centerX,
			final double centerY,
			final double width,
			final double height
	)	{

		return getRectangle(
				centerX - width / 2,
				centerY - height / 2,
				width,
				height
		).edges();

	}

	public static Rectangle2D getRectangle(
			final double centerX,
			final double centerY,
			final double width,
			final double height
	)	{
		return new Rectangle2D(
				centerX - width / 2,
				centerY - height / 2,
				width,
				height
		);
	}

	public static boolean intersect(
			@NotNull final Collection<? extends LineSegment2D> first,
			@NotNull final Collection<? extends LineSegment2D> second
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

	public static boolean intersect(
			@NotNull final Rectangle2D firstRect,
			@NotNull final Rectangle2D secondRect
	)	{
		for(final Point2D firstVertex : firstRect.vertices()) {
			if(secondRect.contains(firstVertex))	{
				return true;
			}
		}
		for(final Point2D secondVertex : secondRect.vertices()) {
			if(firstRect.contains(secondVertex))	{
				return true;
			}
		}
		return intersect(firstRect.edges(), secondRect.edges());
	}

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
			@NotNull final Polygon2D polygon,
			@NotNull final Polyline2D polyline
	)	{
		for(final Point2D point : polyline.vertices()) {
			if(polygon.contains(point))	{
				return true;
			}
		}
		return intersect(polygon.edges(), polyline.edges());
	}

	public static int ccw(Point2D p0, Point2D p1, Point2D p2) {
		double x0 = p0.x();
		double y0 = p0.y();
		double dx1 = p1.x() - x0;
		double dy1 = p1.y() - y0;
		double dx2 = p2.x() - x0;
		double dy2 = p2.y() - y0;
		return dx1 * dy2 > dy1 * dx2?1:(dx1 * dy2 < dy1 * dx2?-1:(dx1 * dx2 >= 0.0D && dy1 * dy2 >= 0.0D?(Math.hypot(dx1, dy1) < Math.hypot(dx2, dy2)?1:0):-1));
	}
}
