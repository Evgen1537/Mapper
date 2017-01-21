package com.evgenltd.mapper.ui.command.marker;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.command.scope.MarkerEdit;
import com.evgenltd.mapper.ui.component.markerediting.MarkerNodeFactory;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import math.geom2d.Point2D;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 02-07-2016 16:48
 */
@CommandTemplate(
		id = UIConstants.MARKER_ADD_POINT,
		text = "Point",
		longText = "Create point marker",
		graphic = "/image/status-away.png",
		accelerator = "Ctrl+1",
		path = "/Marker",
		position = 0
)
public class MarkerAddPoint extends Command {

	@Override
	protected void execute(ActionEvent event) {

		if(UIContext
				.get()
				.getMapViewerWrapper()
				.isTargetingPoint())	{
			return;
		}

		UIContext
				.get()
				.getMapViewerWrapper()
				.beginTargetingPoint(this::addMarkerCallback);

	}

	private boolean addMarkerCallback(@NotNull final Point2D point)	{

		final MarkerNode marker = MarkerNodeFactory.createPointMarker(point.x(), point.y());

		UIContext
				.get()
				.getMarkerEditing()
				.setMarker(marker);

		UIContext
				.get()
				.getMapViewerWrapper()
				.beginEditingNode(marker);

		UIContext
				.get()
				.getToolbar()
				.showMarkerEditingTab();

		UIContext
				.get()
				.getCommandManager()
				.updateHotKeysFromSettings(MarkerEdit.class);

		return false;

	}

}
