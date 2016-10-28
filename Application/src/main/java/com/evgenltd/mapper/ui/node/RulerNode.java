package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 09-07-2016 19:08
 */
public class RulerNode extends Node {

	private final long identifier = (long)(Math.random() * Long.MAX_VALUE);

	private final SettingsBean settingsBean = Context.get().getSettingsBean();

	public RulerNode() {
		setOrderNumber(Long.MAX_VALUE);
	}

	@Override
	public long getIdentifier() {
		return identifier;
	}

	@Override
	protected void paint(PaintContext context) {

		if(settingsBean.isHideRuler())	{
			return;
		}

		final double viewportWorldPositionX = context.getViewportX();
		final double viewportWorldPositionY = context.getViewportY();
		final double viewportWorldWidth = context.getViewportWidth();
		final double viewportWorldHeight = context.getViewportHeight();

		final double tileIncrement = context.getLevel().getMeasure();

		final double viewportWorldTilePositionX = Utils.convertPixelToTile(viewportWorldPositionX);
		final double viewportWorldTilePositionY = Utils.convertPixelToTile(viewportWorldPositionY);
		final double viewportWorldTileWidth = Utils.convertPixelToTile(viewportWorldWidth) + tileIncrement;
		final double viewportWorldTileHeight = Utils.convertPixelToTile(viewportWorldHeight) + tileIncrement;

		final double viewportWorldTilePositionLeveledX = Utils.adjustSizeToLevel(
				viewportWorldTilePositionX,
				context.getLevel().getMeasure()
		);
		final double viewportWorldTilePositionLeveledY = Utils.adjustSizeToLevel(
				viewportWorldTilePositionY,
				context.getLevel().getMeasure()
		);

		final double viewportWorldPositionLeveledX = Utils.convertTileToPixel(viewportWorldTilePositionLeveledX);
		final double viewportWorldPositionLeveledY = Utils.convertTileToPixel(viewportWorldTilePositionLeveledY);

		context.getGraphicsContext().setStroke(UIConstants.GRID_COLOR);
		context.getGraphicsContext().setLineWidth(UIConstants.RULER_BORDER_THICKNESS);

		for(double tileOffset = 0; tileOffset <= viewportWorldTileWidth; tileOffset = tileOffset + tileIncrement)  {

			final double worldOffset = Utils.convertTileToPixel(tileOffset);
			final double canvasX = context.toCanvasX(viewportWorldPositionLeveledX + worldOffset);

			context.getGraphicsContext().setFill(Color.WHITE);
			context.getGraphicsContext().fillRect(canvasX, 0, Constants.TILE_SIZE, UIConstants.RULER_THICKNESS);
			context.getGraphicsContext().strokeRect(canvasX, 0, Constants.TILE_SIZE, UIConstants.RULER_THICKNESS);
			context.getGraphicsContext().setFill(UIConstants.GRID_COLOR);

			final int tileX = (int)(viewportWorldTilePositionLeveledX + tileOffset);
			context.getGraphicsContext().fillText(String.valueOf(tileX), canvasX+4, 20);
		}

		for(double tileOffset = 0; tileOffset <= viewportWorldTileHeight; tileOffset = tileOffset + tileIncrement)  {

			final double worldOffset = Utils.convertTileToPixel(tileOffset);
			final double canvasY = context.toCanvasY(viewportWorldPositionLeveledY + worldOffset);

			context.getGraphicsContext().setFill(Color.WHITE);
			context.getGraphicsContext().fillRect(0, canvasY, UIConstants.RULER_THICKNESS, Constants.TILE_SIZE);
			context.getGraphicsContext().strokeRect(0, canvasY, UIConstants.RULER_THICKNESS, Constants.TILE_SIZE);
			context.getGraphicsContext().setFill(UIConstants.GRID_COLOR);

			final int tileY = (int)(viewportWorldTilePositionLeveledY + tileOffset);
			context.getGraphicsContext().fillText(String.valueOf(tileY), 4, canvasY+20);
		}

		context.getGraphicsContext().setFill(Color.WHITE);
		context.getGraphicsContext().fillRect(0,0, UIConstants.RULER_THICKNESS, UIConstants.RULER_THICKNESS);
	}

	@Override
	protected boolean setHighlighted(@NotNull Predicate<Node> changeCondition) {
		return false;
	}

	@Override
	protected boolean setSelected(@NotNull Predicate<Node> changeCondition) {
		return false;
	}

	@Override
	protected boolean setMoved(@NotNull Predicate<Node> changeCondition) {
		return false;
	}

	@Override
	protected void setBlocked(boolean blocked) {}

	@Override
	protected void setVisible(boolean visible) {}

	@Override
	protected void setEditing(boolean editing) {}


	@Override
	protected boolean intersect(double worldX, double worldY) {
		return false;
	}

	@Override
	protected boolean intersect(
			double worldX,
			double worldY,
			double worldWidth,
			double worldHeight
	) {
		return false;
	}

	@Override
	protected void move(double worldDeltaX, double worldDeltaY) {}

	@Override
	protected void merge(@NotNull Node source) {}
}
