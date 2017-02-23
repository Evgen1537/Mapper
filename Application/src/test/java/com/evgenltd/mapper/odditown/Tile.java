package com.evgenltd.mapper.odditown;

import java.io.File;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 15-02-2017 01:08</p>
 */
public class Tile {
	private int x;
	private int y;
	private File image;
	private Long id;

	public Tile(int x, int y, File file, Long id)
	{
		this.x = x;
		this.y = y;
		image = file;
		this.id = id;
	}

	public int getXCoord()
	{
		return x;
	}

	public int getYCoord()
	{
		return y;
	}

	public File getTile()
	{
		return image;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString()
	{
		return x + "," + y;
	}
}
