package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.screen.LayerProperties;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

import java.util.List;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 02-02-2017 11:26</p>
 */
@CommandTemplate(
		id = UIConstants.PROPERTIES,
		text = "Properties",
		longText = "Display layer properties"
)
public class Properties extends Command {

	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();

	@Override
	protected void execute(ActionEvent event) {

		final List<Long> selectedLayerIdList = mapViewerWrapper.getSelectedNodeIds(LayerNode.class);
		if(selectedLayerIdList.isEmpty())	{
			Message.information().text("No selected layers").publish();
			return;
		}

		final LayerProperties layerProperties = new LayerProperties();
		layerProperties.setLayer(selectedLayerIdList.get(0));
		layerProperties.showAndWait();

		UIContext.get().refresh();
	}
}
