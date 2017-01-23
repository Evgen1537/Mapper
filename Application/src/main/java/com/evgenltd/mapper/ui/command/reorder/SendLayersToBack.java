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
		id = UIConstants.SEND_LAYERS_TO_BACK,
		text = "Send to back",
		longText = "Send selected layers to back",
		graphic = "/image/layers-stack-arrange-back.png",
		accelerator = "Ctrl+Page Down",
		path = "/Layer",
		position = 13
)
public class SendLayersToBack extends Command{

	private final LayerBean layerBean = Context.get().getLayerBean();
	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();

	@Override
	protected void execute(ActionEvent event) {

		final List<Long> selectedLayerIdList = mapViewerWrapper.getSelectedNodeIds(LayerNode.class);
		if(selectedLayerIdList.isEmpty())	{
			Message.information().text("No selected layers").publish();
			return;
		}

		layerBean.sendLayersBackward(selectedLayerIdList, Integer.MAX_VALUE, Collections.singleton(LayerType.SESSION));
		UIContext.get().refresh();

	}

}
