package com.evgenltd.mapper.ui.command.marker;

import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.command.scope.MarkerEdit;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.node.MarkerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 27-06-2016 16:35
 */
@CommandTemplate(
		id = UIConstants.MARKER_EDIT_BEGIN,
		text = "Edit",
		longText = "Edit marker",
		graphic = "/image/pencil.png",
		accelerator = "Ctrl+E",
		path = "/Marker",
		position = 4
)
public class MarkerEditBegin extends Command{

	@Override
	protected void execute(ActionEvent event) {

		final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();

		final List<Node> selectedMarkerList = mapViewerWrapper.getSelectedNodes(MarkerNode.class);
		if(selectedMarkerList.size() != 1)	{
			return;
		}

		final MarkerNode marker = (MarkerNode)selectedMarkerList.get(0);

		UIContext
				.get()
				.getMarkerEditing()
				.setMarker(marker);
		mapViewerWrapper.beginEditingNode(marker);

		UIContext
				.get()
				.getToolbar()
				.showMarkerEditingTab();

		UIContext
				.get()
				.getCommandManager()
				.updateHotKeysFromSettings(MarkerEdit.class);

	}

}
