package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.node.LayerNode;
import com.evgenltd.mapper.ui.screen.LayerChooser;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 07-07-2016 23:33
 */
@CommandTemplate(
		id = UIConstants.MERGE_WITH_GLOBAL,
		text = "Merge with global",
		longText = "Merge layers and markers with global layer",
		graphic = "/image/arrow-merge-090.png",
		accelerator = "Ctrl+M",
		path = "/Layer",
		position = 5
)
@ParametersAreNonnullByDefault
public class MergeWithGlobal extends Command {

	@Override
	protected void execute(ActionEvent event) {

		final List<Long> selectedLayer = UIContext
				.get()
				.getMapViewerWrapper()
				.getSelectedNodeIds(LayerNode.class);

		if(selectedLayer.isEmpty())	{
			return;
		}

		LayerChooser
				.create(LayerType.GROUND, LayerType.CAVE)
				.showAndWait()
				.ifPresent(targetLayerId -> makeAndSubmitTask(targetLayerId, selectedLayer));

	}

	private void makeAndSubmitTask(final Long targetLayerId, final List<Long> sourceLayerIdList)	{

		final Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Merge layers");

				Context
						.get()
						.getCommonBean()
						.merge(
								targetLayerId,
								sourceLayerIdList,
								this::updateMessage,
								this::updateProgress
						);

				Context.get().getImageCache().cleanCache();

				return null;
			}
		};

		setupDefaultListeners(task);

		UIContext
				.get()
				.submit(getId(), task);

	}
}
