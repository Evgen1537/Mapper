package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import com.evgenltd.mapper.ui.util.Geometry;
import javafx.geometry.Point2D;
import math.geom2d.polygon.Rectangle2D;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 02:00
 */
class TileNode extends Node {

	private Tile tile;
	private ZLevel level;

	private final SettingsBean settingsBean = Context.get().getSettingsBean();

	TileNode(@NotNull final LayerNode layerNode, @NotNull final Tile tile, @NotNull final ZLevel level) {
		super(layerNode);
		this.tile = tile;
		this.level = level;
	}

	List<Point2D> getLowLevelTilePoints()	{
		return tile.getLowLevelTilePointList();
	}

	@Override
	public long getIdentifier() {
		return tile.getId();
	}

	@Override
	public LayerNode getParent() {
		return (LayerNode)super.getParent();
	}

	private double getTileX()	{
		return getParent().getX() + tile.getX();
	}

	private double getTileY()	{
		return getParent().getY() + tile.getY();
	}

	private double getWorldX()	{
		return getTileX() * Constants.TILE_SIZE;
	}

	private double getWorldY()	{
		return getTileY() * Constants.TILE_SIZE;
	}

	@Override
	public void paint(PaintContext context) {
		context.image(
				tile.getImage(),
				getWorldX(),
				getWorldY()
		);
	}

	@Override
	public boolean intersect(double worldX, double worldY) {

		if(!isAccessible()) {
			return false;
		}

		for(final Point2D point : getLowLevelTilePoints()) {

			final Rectangle2D tileRect = new Rectangle2D(
					Utils.convertTileToPixel(point.getX() + getParent().getX()),
					Utils.convertTileToPixel(point.getY() + getParent().getY()),
					Constants.TILE_SIZE,
					Constants.TILE_SIZE
			);

			if(tileRect.contains(worldX, worldY))	{
				return true;
			}

		}

		return false;
	}

	@Override
	public boolean intersect(double worldX, double worldY, double worldWidth, double worldHeight) {

		if(!isAccessible()) {
			return false;
		}

		final Rectangle2D worldRect = new Rectangle2D(worldX,worldY,worldWidth,worldHeight);

		for(final Point2D point : getLowLevelTilePoints()) {

			final Rectangle2D tileRect = new Rectangle2D(
					Utils.convertTileToPixel(point.getX() + getParent().getX()),
					Utils.convertTileToPixel(point.getY() + getParent().getY()),
					Constants.TILE_SIZE,
					Constants.TILE_SIZE
			);

			if(Geometry.intersect(worldRect,tileRect))	{
				return true;
			}

		}

		return false;

	}

	@Override
	public void move(double worldDeltaX, double worldDeltaY) {}

	@Override
	public void merge(@NotNull final Node source) {
		final TileNode tileNodeSource = (TileNode)source;
		tile = tileNodeSource.tile;
		level = tileNodeSource.level;
	}

	private boolean isAccessible()  {
		return !settingsBean.getSelectionMode().isLayerRestricted();
	}
}
