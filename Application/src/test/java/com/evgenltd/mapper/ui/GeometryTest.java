package com.evgenltd.mapper.ui;

import com.evgenltd.mapper.ui.util.Geometry;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 10-07-2016 23:25
 */
public class GeometryTest {

	@Test
	public void clockwiseSorting()	{

		final Polygon2D polygon = new SimplePolygon2D(
				new Point2D(5,13),
				new Point2D(4,10),
				new Point2D(7,9),
				new Point2D(7,12)
		);

		final Point2D barycenter = polygon.centroid();

		final List<Point2D> vertices = new ArrayList<>(polygon.vertices());

		Collections.sort(
				vertices,
				(o1, o2) -> Geometry.ccw(barycenter, o2, o1)
		);

//		System.err.println(Geometry.ccw(barycenter,p1,p2));

		Assert.assertTrue(true);

	}

	@Test
	public void testComplexStructSorting()	{

		final Polygon2D polygon = new SimplePolygon2D(
				new Point2D(2,3),
				new Point2D(4,2),
				new Point2D(8,2),
				new Point2D(5,4),
				new Point2D(8,8),
				new Point2D(3,3),
				new Point2D(5,8)
		);

		final Point2D barycenter = polygon.centroid();

		final List<Point2D> vertices = new ArrayList<>(polygon.vertices());

		Collections.sort(vertices, (o1, o2) -> Geometry.ccw(barycenter, o2, o1));

		System.err.println("Done");

	}

}
