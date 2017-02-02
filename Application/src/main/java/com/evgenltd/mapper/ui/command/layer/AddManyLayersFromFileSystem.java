package com.evgenltd.mapper.ui.command.layer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.LayerBean;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 20-06-2016 00:50
 */
@CommandTemplate(
		id = UIConstants.ADD_MANY_LAYERS_FROM_FILE_SYSTEM,
		text = "Add many layers",
		longText = "Add many session layers fom the map folder",
		graphic = "/image/folder--plus.png",
		accelerator = "Ctrl+Shift+N",
		path = "/Layer",
		position = 1
)
public class AddManyLayersFromFileSystem extends Command {

	private final LayerBean layerBean = Context.get().getLayerBean();

	@Override
	protected void execute(ActionEvent event) {

		UIUtils.askDirectory().ifPresent(file -> {

			final Task<Void> task = makeTask(file);
			setupDefaultListeners(task);
			UIContext.get().submit(getId(), task);

		});

	}

	private Task<Void> makeTask(final @NotNull File selectedDirectory)	{

		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Adding many layers...");

				layerBean.addManyLayersFromSessionFolder(
						selectedDirectory,
						this::updateTitle,
						this::updateMessage,
						this::updateProgress
				);

				Context.get().getImageCache().cleanCache();

				return null;
			}
		};

	}
}
