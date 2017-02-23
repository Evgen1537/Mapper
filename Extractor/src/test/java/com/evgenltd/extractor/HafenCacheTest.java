package com.evgenltd.extractor;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.Map;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 06-02-2017 00:18</p>
 */
public class HafenCacheTest {

	@Test
	public void loadMapIndexTest() {

		final HafenCache hafenCache = new HafenCache();
		final Map<Point2D,Long> index = hafenCache.loadMapIndex();
		System.err.println("Done");

	}

}
