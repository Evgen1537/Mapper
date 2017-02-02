package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.node.LayerNode;
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
		id = UIConstants.MERGE_TOGETHER,
		text = "Merge together",
		longText = "Merge layers and markers with in new session layer",
		graphic = "/image/arrow-merge-090.png",
		accelerator = "Ctrl+J",
		path = "/Layer",
		position = 4
)
@ParametersAreNonnullByDefault
public class MergeTogether extends Command{

	@Override
	protected void execute(ActionEvent event) {

		final List<Long> selectedLayer = UIContext
				.get()
				.getMapViewerWrapper()
				.getSelectedNodeIds(LayerNode.class);

		if(selectedLayer.size() < 2)	{
			return;
		}

		final Task<Void> task = makeTask(selectedLayer);
		setupDefaultListeners(task);

		UIContext.get().submit(getId(), task);

	}

	private Task<Void> makeTask(final List<Long> sourceLayerIdList)	{

		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Merge layers");

				Context
						.get()
						.getCommonBean()
						.mergeTogether(
								sourceLayerIdList,
								this::updateMessage,
								this::updateProgress
						);

				Context.get().getImageCache().cleanCache();

				return null;
			}
		};

	}

}
