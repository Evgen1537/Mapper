package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.util.UIConstants;
import math.geom2d.Point2D;
import org.jetbrains.annotations.NotNull;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 25-08-2016 00:55
 */
public class PlayerPositionNode extends Node {

	private Point2D playerPosition;

	public PlayerPositionNode(Point2D playerPosition) {
		this.playerPosition = playerPosition;
	}

	@Override
	public long getIdentifier() {
		return 3141592653589793238L;
	}

	@Override
	public long getOrderNumber() {
		return Long.MAX_VALUE;
	}

	@Override
	protected void paint(PaintContext context) {
		UIContext.get().putDebugInfo("player position",String.format("%s : %s",playerPosition.x(),playerPosition.y()));
		context.imageLeveled(UIConstants.SMILEY, playerPosition.x(), playerPosition.y(), 24, 24);
	}

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
	protected void merge(@NotNull final Node source) {
		final PlayerPositionNode playerPositionNode = (PlayerPositionNode)source;
		playerPosition = playerPositionNode.playerPosition;
	}
}
