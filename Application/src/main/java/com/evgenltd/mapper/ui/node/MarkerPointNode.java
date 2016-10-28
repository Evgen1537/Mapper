package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.core.entity.MarkerPoint;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.NodeGroup;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.util.Geometry;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.scene.paint.Color;
import math.geom2d.polygon.Rectangle2D;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 28-06-2016 23:35
 */
public class MarkerPointNode extends Node {

	private MarkerPoint markerPoint;
	private long editingModeNewMarkerPointIdentity;

	public MarkerPointNode(
			@NotNull final NodeGroup parent,
			@NotNull final MarkerPoint markerPoint
	) {
		super(parent);
		this.markerPoint = markerPoint;
		if(markerPoint.getId() == null)	{
			editingModeNewMarkerPointIdentity = UIContext
					.get()
					.getMarkerEditing()
					.getNextPointIdentity();
		}
	}

	@Override
	public long getIdentifier() {
		return markerPoint.getId() == null
				? editingModeNewMarkerPointIdentity
				: markerPoint.getId();
	}

	public MarkerPoint getMarkerPoint() {
		return markerPoint;
	}

	@Override
	public void paint(PaintContext context) {
		context.ovalFill(
				UIConstants.POINT_COLOR,
				markerPoint.getX(),
				markerPoint.getY(),
				UIConstants.MARKER_POINT_SIZE,
                UIConstants.MARKER_POINT_SIZE
		);
		if(isSelected() || isHighlighted())	{

			final Color color = isSelected()
					? UIConstants.SELECTION_COLOR
					: UIConstants.HIGHLIGHT_COLOR;
			context.ovalStroke(
					color,
					UIConstants.MARKER_POINT_OUTLINE_WIDTH,
					markerPoint.getX(),
					markerPoint.getY(),
					UIConstants.MARKER_POINT_SIZE,
					UIConstants.MARKER_POINT_SIZE
			);

		}
	}

	@Override
	public boolean intersect(double worldX, double worldY) {
		final Rectangle2D pointRect = Geometry.getRectangle(
				markerPoint.getX(),
				markerPoint.getY(),
				UIConstants.MARKER_POINT_SIZE,
				UIConstants.MARKER_POINT_SIZE
		);
		return pointRect.contains(worldX, worldY);
	}

	@Override
	public boolean intersect(
			double worldX,
			double worldY,
			double worldWidth,
			double worldHeight
	) {
		final Rectangle2D pointRect = Geometry.getRectangle(
				markerPoint.getX(),
				markerPoint.getY(),
				UIConstants.MARKER_POINT_SIZE,
				UIConstants.MARKER_POINT_SIZE
		);
		final Rectangle2D intersectedRect = new Rectangle2D(
				worldX,
				worldY,
				worldWidth,
				worldHeight
		);
		return Geometry.intersect(pointRect, intersectedRect);
	}

	@Override
	public void move(double worldDeltaX, double worldDeltaY) {
		markerPoint.setX(markerPoint.getX() + worldDeltaX);
		markerPoint.setY(markerPoint.getY() + worldDeltaY);
	}

	@Override
	public void merge(@NotNull final Node source) {
		final MarkerPointNode sourceMarkerPointNode = (MarkerPointNode)source;
		markerPoint = sourceMarkerPointNode.markerPoint;
	}
}
