package com.evgenltd.mapper.ui.command.tracker;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import math.geom2d.Point2D;

/**
 * Project: mapper
 * Author:  Евгений
 * Created: 31-Авг.-2016 01:48
 */
@CommandTemplate(
		id = UIConstants.TO_PLAYER_POSITION,
		text = "To player",
		longText = "Move viewport to current plater position",
		graphic = "/image/smiley-16.png",
		accelerator = "Ctrl+P",
		path = "/Tracker",
		position = 2
)
public class MoveToPlayerPosition extends Command{

	@Override
	protected void execute(ActionEvent event) {
		final Point2D playerPosition = Context
				.get()
				.getTrackerBean()
				.getWorldPlayerPosition();
		if(playerPosition == null)	{
			return;	// todo add notification to user
		}
		UIContext
				.get()
				.getMapViewerWrapper()
				.moveViewportToPoint(playerPosition.x(), playerPosition.y());
	}
}
