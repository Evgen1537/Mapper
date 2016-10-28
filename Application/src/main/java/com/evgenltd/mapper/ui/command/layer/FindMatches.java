package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import math.geom2d.Point2D;

import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 28-07-2016 10:24
 */
@CommandTemplate(
		id = UIConstants.FIND_MATCHES,
		text = "Find matches",
		longText = "Find layer matches by identical tiles",
		graphic = "/image/magnifier-zoom-fit.png",
		accelerator = "Ctrl+F",
		path = "/Layer",
		position = 7
)
public class FindMatches extends Command {

	@Override
	protected void execute(ActionEvent event) {

		final List<Long> selectedLayerIdList = UIContext
				.get()
				.getMapViewerWrapper()
				.getSelectedNodeIds(LayerNode.class);

		if(selectedLayerIdList.isEmpty())	{
			Message.information().text("No selected layers").publish();
			return;
		}

		Context
				.get()
				.getLayerMatcherBean()
				.findMatchesById(selectedLayerIdList)
				.ifPresent(layer -> {
					final Point2D centroid = Context
							.get()
							.getLoader()
							.loadLayerCentroid(layer.getId());
					UIContext
							.get()
							.getMapViewerWrapper()
							.moveViewportToPoint(
									Utils.convertTileToPixel(centroid.x()),
									Utils.convertTileToPixel(centroid.y())
							);
				});

		UIContext
				.get()
				.refresh();

	}

}
