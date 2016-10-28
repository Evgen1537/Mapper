package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 22:22
 */
public class TileInfo {
	private double x;
	private double y;
	private ZLevel z;
	private Layer layer;

	private TileInfo() {
	}

	public static TileInfo of(final double x, final double y, @NotNull final ZLevel z, @NotNull final Layer layer) {
		final TileInfo info = new TileInfo();
		info.x = x;
		info.y = y;
		info.z = z;
		info.layer = layer;
		return info;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public ZLevel getZ() {
		return z;
	}

	public Layer getLayer() {
		return layer;
	}
}
