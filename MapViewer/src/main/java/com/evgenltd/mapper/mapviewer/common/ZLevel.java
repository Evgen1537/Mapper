package com.evgenltd.mapper.mapviewer.common;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 18:12
 */
public enum ZLevel {
	Z1(1,1), Z2(2,2), Z3(3,4), Z4(4,8), Z5(5,16);

	public static final ZLevel MIN_LEVEL = Z1;
	public static final ZLevel MAX_LEVEL = Z5;

	private int level;
	private int measure;

	ZLevel(int level, int measure) {
		this.level = level;
		this.measure = measure;
	}

	public int getLevel() {
		return level;
	}

	public int getMeasure() {
		return measure;
	}

	public ZLevel nextLevel()    {
		if(this.equals(MAX_LEVEL)) {
			throw new IllegalStateException(this + " is highest level");
		}
		return ZLevel.valueOf(getLevel()+1);
	}

	public ZLevel previousLevel()   {
		if(this.equals(MIN_LEVEL)) {
			throw new IllegalStateException(this + " is lowest level");
		}
		return ZLevel.valueOf(getLevel()-1);
	}

	public boolean isMinimumLevel() {
		return this.equals(MIN_LEVEL);
	}

	public boolean isMaximumLevel() {
		return this.equals(MAX_LEVEL);
	}

	public static ZLevel valueOf(int level) {
		return ZLevel.valueOf("Z"+level);
	}
}
