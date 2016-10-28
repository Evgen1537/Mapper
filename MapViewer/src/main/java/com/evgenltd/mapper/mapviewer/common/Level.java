package com.evgenltd.mapper.mapviewer.common;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Project: MapperPrototype
 * Author:  Evgeniy
 * Created: 09-06-2016 00:42
 */
class Level {
	private final DoubleProperty level = new SimpleDoubleProperty();
	private ZLevel zLevel;

	private final double min;
	private final double max;

	public Level(final double min, final double max) {
		this.min = min;
		this.max = max;
		level.addListener(observable -> zLevel = ZLevel.valueOf((int)level.get()));
		level.set(min);
	}

	public double get() {
		return level.get();
	}

	public ZLevel getZLevel()	{
		return zLevel;
	}

	public DoubleProperty property() {
		return level;
	}

	public void set(double level) {
		this.level.set(level);
	}

	public void increment()	{
		if(level.get() < max)	{
			level.set(level.get() + 1.0);
		}
	}

	public void decrement()	{
		if(level.get() > min)	{
			level.set(level.get() - 1.0);
		}
	}
}
