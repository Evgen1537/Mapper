package com.evgenltd.mapper.ui.command.marker;

import com.evgenltd.mapper.core.entity.MarkerPoint;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.command.scope.MarkerEdit;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.component.markerediting.MarkerEditing;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.node.MarkerPointNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import math.geom2d.Point2D;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 28-06-2016 11:06
 */
@CommandTemplate(
		id = UIConstants.MARKER_EDIT_ADD_POINT,
		text = "Add Point",
		longText = "Add point to the marker contour",
		graphic = "/image/pin--plus.png",
		accelerator = "Ctrl+N",
		path = "/Marker Editing",
		position = 0,
		scope = MarkerEdit.class
)
public class MarkerEditAddPoint extends Command{

	final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();

	@Override
	protected void execute(ActionEvent event) {

		if(mapViewerWrapper.isTargetingPoint())	{
			return;
		}

		mapViewerWrapper.beginTargetingPoint(this::addPointCallback);

	}

	private boolean addPointCallback(@NotNull final Point2D selectedPoint)	{

		final MarkerEditing markerEditing = UIContext.get().getMarkerEditing();

		MarkerNode markerNode = markerEditing.getMarkerNode();

		MarkerPoint markerPoint = new MarkerPoint(selectedPoint.x(), selectedPoint.y(), 0L, markerNode.getMarker());

		MarkerPointNode markerPointNode = new MarkerPointNode(markerNode, markerPoint);

		markerNode.insert(markerPointNode);

		return true;

	}
}
