package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.ui.util.UIConstants;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 09-07-2016 19:08
 */
public class GridNode extends Node{

	private final long identifier = (long)(Math.random() * Long.MAX_VALUE);

	private final SettingsBean settingsBean = Context.get().getSettingsBean();

	public GridNode() {
		setOrderNumber(Long.MAX_VALUE);
	}

	@Override
	public long getIdentifier() {
		return identifier;
	}

	@Override
	protected void paint(PaintContext context) {

		if(settingsBean.isHideGrid()) {
			return;
		}

		final double viewportWorldPositionX = context.getViewportX();
		final double viewportWorldPositionY = context.getViewportY();
		final double viewportWorldWidth = context.getViewportWidth();
		final double viewportWorldHeight = context.getViewportHeight();

		final double worldIncrement = context.getLevel().getMeasure() * Constants.TILE_SIZE;

		final double viewportWorldTilePositionX = Utils.convertPixelToTile(viewportWorldPositionX);
		final double viewportWorldTilePositionY = Utils.convertPixelToTile(viewportWorldPositionY);

		final double viewportWorldTilePositionLeveledX = Utils.adjustSizeToLevel(
				viewportWorldTilePositionX,
				context.getLevel().getMeasure()
		) + context.getLevel().getMeasure();
		final double viewportWorldTilePositionLeveledY = Utils.adjustSizeToLevel(
				viewportWorldTilePositionY,
				context.getLevel().getMeasure()
		) + context.getLevel().getMeasure();

		final double viewportWorldPositionLeveledX = Utils.convertTileToPixel(viewportWorldTilePositionLeveledX);
		final double viewportWorldPositionLeveledY = Utils.convertTileToPixel(viewportWorldTilePositionLeveledY);

		context.getGraphicsContext().setStroke(UIConstants.GRID_COLOR);
		context.getGraphicsContext().setLineWidth(UIConstants.RULER_BORDER_THICKNESS);

		for(double worldOffset = 0; worldOffset <= viewportWorldWidth; worldOffset = worldOffset + worldIncrement)  {

			final double canvasX = context.toCanvasX(viewportWorldPositionLeveledX + worldOffset);
			context.getGraphicsContext().strokeLine(canvasX, 0, canvasX, viewportWorldHeight);

		}

		for(double worldOffset = 0; worldOffset <= viewportWorldHeight; worldOffset = worldOffset + worldIncrement)  {

			final double canvasY = context.toCanvasY(viewportWorldPositionLeveledY + worldOffset);
			context.getGraphicsContext().strokeLine(0, canvasY, viewportWorldWidth, canvasY);

		}

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
