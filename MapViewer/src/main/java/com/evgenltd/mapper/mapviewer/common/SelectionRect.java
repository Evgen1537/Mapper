package com.evgenltd.mapper.mapviewer.common;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 09-06-2016 02:37
 */
class SelectionRect {

	private static final double GAP = 3;

	private double firstX;
	private double firstY;
	private double secondX;
	private double secondY;

	public void setInitPosition(final double canvasX, final double canvasY)	{
		this.firstX = canvasX;
		this.firstY = canvasY;
		this.secondX = canvasX;
		this.secondY = canvasY;
	}

	public void updateSecondPosition(final double canvasX, final double canvasY)	{
		this.secondX = canvasX;
		this.secondY = canvasY;
	}
	
	public double getCanvasX() {
		return firstX;
	}

	public double getCanvasY() {
		return firstY;
	}

	public double getFirstX() {
		final double result = firstX < secondX
				? firstX
				: secondX;
		return result - GAP;
	}

	public double getFirstY() {
		final double result = firstY < secondY
				? firstY
				: secondY;
		return result - GAP;
	}

	public double getSecondX()	{
		final double result = firstX < secondX
				? secondX
				: firstX;
		return result + GAP;
	}

	public double getSecondY()	{
		final double result = firstY < secondY
				? secondY
				: firstY;
		return result + GAP;
	}

	public double getWidth()	{
		return getSecondX() - getFirstX();
	}

	public double getHeight()	{
		return getSecondY() - getFirstY();
	}
}
