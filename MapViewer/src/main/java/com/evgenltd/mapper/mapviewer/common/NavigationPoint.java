package com.evgenltd.mapper.mapviewer.common;

import org.jetbrains.annotations.Contract;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 09-06-2016 02:40
 */
class NavigationPoint {
	private double startX;
	private double startY;
	private double x;
	private double y;
	private double deltaX;
	private double deltaY;
	private double totalDeltaX;
	private double totalDeltaY;

	private double step = 1;

	NavigationPoint() {
		x = 0;
		y = 0;
	}

	void setStep(final double step) {
		this.step = step;
	}

	@Contract(pure = true)
	private double convertByStep(final double value)    {
		return ((int)(value / step)) * step;
	}

	void setInitPosition(final double newX, final double newY)	{
		startX = x = convertByStep(newX);
		startY = y = convertByStep(newY);
	}

	void updatePosition(final double newX, final double newY)	{

		final double steppedNewX = convertByStep(newX);
		final double steppedNewY = convertByStep(newY);

		this.deltaX = steppedNewX - this.x;
		this.deltaY = steppedNewY - this.y;
		this.x = steppedNewX;
		this.y = steppedNewY;

		this.totalDeltaX = steppedNewX - startX;
		this.totalDeltaY = steppedNewY - startY;
	}

	double getDeltaX() {
		return deltaX;
	}

	double getDeltaY() {
		return deltaY;
	}

	double getTotalDeltaX() {
		return totalDeltaX;
	}

	double getTotalDeltaY() {
		return totalDeltaY;
	}
}
