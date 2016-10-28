package com.evgenltd.mapper.mapviewer.common;

import javafx.beans.property.DoubleProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 09-06-2016 00:21
 */
class Viewport {

	private static final long DELAY = 1000;

	private double x;
	private double y;
	private double width;
	private double height;
	private Level level = new Level(1,5);

	private long lastChanged = 0L;

	public double getX() {
		return x;
	}

	public boolean setX(double x) {
		final boolean invalidated = this.x != x;
		this.x = x;
		return invalidated && checkAndUpdateTime();
	}
	
	public boolean increaseX(double deltaX)	{
		return setX(getX() - deltaX);
	}

	public double getY() {
		return y;
	}

	public boolean setY(double y) {
		final boolean invalidated = this.y != y;
		this.y = y;
		return invalidated && checkAndUpdateTime();
	}

	public boolean increaseY(double deltaY)	{
		return setY(getY() - deltaY);
	}
	
	public double getWidth() {
		return width;
	}

	public boolean setWidth(double width) {
		final boolean invalidated = this.width != width;
		this.width = width;
		return invalidated && checkAndUpdateTime();
	}

	public double getHeight() {
		return height;
	}

	public boolean setHeight(double height) {
		final boolean invalidated = this.height != height;
		this.height = height;
		return invalidated && checkAndUpdateTime();
	}

	double getCenterX()	{
		return x + width / 2;
	}

	double getCenterY()	{
		return y + height / 2;
	}

	// level

	public DoubleProperty levelProperty()	{
		return level.property();
	}

	public ZLevel getLevel() {
		return level.getZLevel();
	}

	public void incrementLevel() {
		level.increment();
	}

	public void decrementLevel() {
		level.decrement();
	}

	//

	private boolean checkAndUpdateTime()	{
		final long currentTime = System.currentTimeMillis();
		final long currentDelay = currentTime - lastChanged;
		if(currentDelay > DELAY)	{
			lastChanged = currentTime;
			return true;
		}
		return false;
	}

	//

	public double toCanvasSize(final double worldSize)	{
		return worldSize / getLevel().getMeasure();
	}

	public double toWorldSize(final double canvasSize)	{
		return canvasSize * getLevel().getMeasure();
	}

	public double toCanvasX(final double worldX)	{
		return toCanvasSize(worldX - this.x);
	}

	public double toCanvasY(final double worldY)	{
		return toCanvasSize(worldY - this.y);
	}

	public double[] toCanvasX(@NotNull final double[] worldXPoints)	{
		if(worldXPoints.length == 0)	{
			return worldXPoints;
		}
		final double[] canvasXPoints = new double[worldXPoints.length];
		for(int i = 0; i < worldXPoints.length; i++) {
			canvasXPoints[i] = toCanvasX(worldXPoints[i]);
		}
		return canvasXPoints;
	}

	public double[] toCanvasY(@NotNull final double[] worldYPoints)	{
		if(worldYPoints.length == 0)	{
			return worldYPoints;
		}
		final double[] canvasYPoints = new double[worldYPoints.length];
		for(int i = 0; i < worldYPoints.length; i++) {
			canvasYPoints[i] = toCanvasY(worldYPoints[i]);
		}
		return canvasYPoints;
	}

	public double toWorldX(final double canvasX)	{
		return toWorldSize(canvasX) + this.x;
	}

	public double toWorldY(final double canvasY)	{
		return toWorldSize(canvasY) + this.y;
	}

	// special methods

	public boolean setSizeFromCanvas(final double canvasWidth, final double canvasHeight)	{
		boolean invalidated = setWidth(canvasWidth * getLevel().getMeasure());
		return invalidated | setHeight(canvasHeight * getLevel().getMeasure());
	}

	public boolean changePositionByNavigationPoint(final double canvasDeltaX, final double canvasDeltaY)	{
		final double worldDeltaX = toWorldSize(canvasDeltaX);
		final double worldDeltaY = toWorldSize(canvasDeltaY);
		boolean invalidated = increaseX(worldDeltaX);
		return invalidated | increaseY(worldDeltaY);
	}
}
