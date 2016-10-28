package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.LayerBean;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.util.UIConstants;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-08-2016 22:24
 */
@CommandTemplate(
		id = UIConstants.EXPORT_LAYER_TO_FOLDER,
		text = "Export layers",
		longText = "Exporting layers to the selected folder",
		graphic = "/image/folder--arrow.png",
		path = "/Layer",
		position = 8
)
public class ExportToFolder extends Command {

	private final LayerBean layerBean = Context.get().getLayerBean();

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

		UIUtils.askDirectory().ifPresent(directory -> {

			final Task<Void> task = makeTask(directory, selectedLayerIdList);
			setupDefaultListeners(task);

			UIContext.get().submit(getId(), task);

		});



	}


	private Task<Void> makeTask(final File destination, final @NotNull List<Long> layerIdList)	{

		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Exporting layers...");

				for(Long layerId : layerIdList) {
					layerBean.exportLayersToFileSystem(
							destination,
							layerId,
							this::updateMessage,
							this::updateProgress
					);
				}

				return null;
			}
		};

	}

}
