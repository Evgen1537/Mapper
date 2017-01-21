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
 * Created: 02-07-2016 16:49
 */
@CommandTemplate(
		id = UIConstants.MARKER_ADD_TRACK,
		text = "Track",
		longText = "Create track marker",
		graphic = "/image/flag-blue.png",
		accelerator = "Ctrl+3",
		path = "/Marker",
		position = 2
)
public class MarkerAddTrack extends Command {

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

	private boolean addMarkerCallback(final @NotNull Point2D point)	{

		final MarkerNode marker = MarkerNodeFactory.createTrackMarker(point.x(), point.y());

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
