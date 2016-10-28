package com.evgenltd.mapper.ui.command.marker;

import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.command.scope.MarkerEdit;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 28-06-2016 11:06
 */
@CommandTemplate(
		id = UIConstants.MARKER_EDIT_REMOVE_POINT,
		text = "Remove Point",
		longText = "Remove point from the marker contour",
		graphic = "/image/pin--minus.png",
		accelerator = "Delete",
		path = "/Marker Editing",
		position = 1,
		scope = MarkerEdit.class
)
public class MarkerEditRemovePoint extends Command	{

	@Override
	protected void execute(ActionEvent event) {

		final MarkerNode editingMarkerNode = UIContext
				.get()
				.getMarkerEditing()
				.getMarkerNode();

		editingMarkerNode
				.getChildren()
				.stream()
				.filter(Node::isSelected)
				.collect(Collectors.toList())
				.forEach(editingMarkerNode::remove);

		UIContext
				.get()
				.getCommandManager()
				.updateCommandDisableState();

		UIContext
				.get()
				.getMapViewerWrapper()
				.repaint();

	}

}
