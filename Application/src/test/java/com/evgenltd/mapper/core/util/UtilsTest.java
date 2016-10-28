package com.evgenltd.mapper.core.util;

import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 16:29
 */
public class UtilsTest {

	@Test
	public void testToTilePosition()	{

		Assert.assertEquals(-5, Utils.toTilePosition(-450), 0);
		Assert.assertEquals(0, Utils.toTilePosition(50), 0);
		Assert.assertEquals(5, Utils.toTilePosition(560), 0);

	}

	@Test
	public void testAdjustPointToHigherLevel()   {
		Point2D actualResult = Utils.adjustPointToNextLevel(new Point2D(15, 15), ZLevel.Z1);
		Point2D expectedResult = new Point2D(14,14);
		Assert.assertEquals(expectedResult, actualResult);

		actualResult = Utils.adjustPointToNextLevel(new Point2D(15,15), ZLevel.Z2);
		expectedResult = new Point2D(12,12);
		Assert.assertEquals(expectedResult, actualResult);

		actualResult = Utils.adjustPointToNextLevel(new Point2D(15,15), ZLevel.Z3);
		expectedResult = new Point2D(8,8);
		Assert.assertEquals(expectedResult, actualResult);

		actualResult = Utils.adjustPointToNextLevel(new Point2D(15,15), ZLevel.Z4);
		expectedResult = new Point2D(0,0);
		Assert.assertEquals(expectedResult, actualResult);



		actualResult = Utils.adjustPointToNextLevel(new Point2D(-1,-1), ZLevel.Z1);
		expectedResult = new Point2D(-2,-2);
		Assert.assertEquals(expectedResult, actualResult);

		actualResult = Utils.adjustPointToNextLevel(new Point2D(-1,-1), ZLevel.Z2);
		expectedResult = new Point2D(-4,-4);
		Assert.assertEquals(expectedResult, actualResult);

		actualResult = Utils.adjustPointToNextLevel(new Point2D(-1,-1), ZLevel.Z3);
		expectedResult = new Point2D(-8,-8);
		Assert.assertEquals(expectedResult, actualResult);

		actualResult = Utils.adjustPointToNextLevel(new Point2D(-1,-1), ZLevel.Z4);
		expectedResult = new Point2D(-16,-16);
		Assert.assertEquals(expectedResult, actualResult);
	}

}
