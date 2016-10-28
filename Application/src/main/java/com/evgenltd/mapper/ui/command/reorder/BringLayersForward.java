package com.evgenltd.mapper.ui.command.reorder;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.LayerBean;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

import java.util.Collections;
import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 20-06-2016 12:45
 */
@CommandTemplate(
		id = UIConstants.BRING_LAYERS_FORWARD,
		text = "Bring forward",
		longText = "Bring selected layers forward",
		graphic = "/image/layers-arrange.png",
		accelerator = "Page Up",
		path = "/Layer",
		position = 14
)
public class BringLayersForward extends Command {

	private final LayerBean layerBean = Context.get().getLayerBean();
	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();

	@Override
	protected void execute(ActionEvent event) {

		final List<Long> selectedLayerIdList = mapViewerWrapper.getSelectedNodeIds(LayerNode.class);
		if(selectedLayerIdList.isEmpty())	{
			Message.information().text("No selected layers").publish();
			return;
		}

		layerBean.bringLayersForward(selectedLayerIdList, 1, Collections.singleton(LayerType.SESSION));
		UIContext.get().refresh();

	}

}
