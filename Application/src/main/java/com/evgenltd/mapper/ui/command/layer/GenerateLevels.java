package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.LayerBean;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.component.mapviewer.MapViewerWrapper;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 15:47
 */
@CommandTemplate(
		id = UIConstants.GENERATE_LEVELS,
		text = "Generate levels",
		longText = "Generate layer zoom levels",
		graphic = "/image/layers-stack.png",
		accelerator = "Ctrl+F5",
		path = "/Layer",
		position = 2
)
public class GenerateLevels extends Command {

	private final LayerBean layerBean = Context.get().getLayerBean();
	private final MapViewerWrapper mapViewerWrapper = UIContext.get().getMapViewerWrapper();

	@Override
	protected void execute(ActionEvent actionEvent) {

		final List<Long> selectedLayerIdList = mapViewerWrapper.getSelectedNodeIds(LayerNode.class);
		if(selectedLayerIdList.isEmpty())	{
			Message.information().text("No selected layers").publish();
			return;
		}

		final Task<Void> task = makeTask(selectedLayerIdList);
		setupDefaultListeners(task);

		UIContext.get().submit(getId(), task);

	}

	private Task<Void> makeTask(@NotNull final List<Long> layerIdList)	{

		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Generating levels...");

				for(Long layerId : layerIdList) {
					layerBean.generateLevels(
							layerId,
							this::updateMessage,
							this::updateProgress
					);
				}

				Context.get().getImageCache().cleanCache();

				return null;
			}
		};

	}

}
