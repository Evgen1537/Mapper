package com.evgenltd.mapper.mapviewer.common;

import java.util.Arrays;
import java.util.Objects;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 09-06-2016 02:32
 */
enum State {
	NONE,
	CLICK,
	SELECTION,
	MOVE,
	NAVIGATION,
	POINT_TARGET;

	boolean isNone()	{
		return Objects.equals(this, NONE);
	}

	boolean isSelection()	{
		return Objects.equals(this, SELECTION);
	}

	boolean isClick()	{
		return Objects.equals(this,CLICK);
	}

	boolean isAnySelection()	{
		return Arrays.asList(CLICK,SELECTION).contains(this);
	}

	boolean isMove()	{
		return Objects.equals(this,MOVE);
	}

	boolean isNavigation()	{
		return Objects.equals(this,NAVIGATION);
	}

	boolean isPointTarget()	{
		return Objects.equals(this, POINT_TARGET);
	}
}
