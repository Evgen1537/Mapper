package com.evgenltd.mapper.ui.command.globallayer;

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
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-08-2016 22:24
 */
@CommandTemplate(
		id = UIConstants.GLOBAL_EXPORT_LAYER_TO_FOLDER,
		text = "Export layer",
		longText = "Exporting layer to the selected folder",
		graphic = "/image/folder--arrow.png"
)
public class ExportToFolder extends Command {

	private final LayerBean layerBean = Context.get().getLayerBean();

	private Long layerId;

	public void setLayerId(@NotNull final Long layerId) {
		this.layerId = layerId;
	}

	@Override
	protected void execute(ActionEvent event) {

		if(layerId == null)	{
			return;
		}

		UIUtils.askDirectory().ifPresent(directory -> {

			final Task<Void> task = makeTask(directory);
			setupDefaultListeners(task);

			UIContext.get().submit(getId(), task);

		});

	}


	private Task<Void> makeTask(final File destination)	{

		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Exporting layers...");

				layerBean.exportLayersToFileSystem(
						destination,
						layerId,
						this::updateMessage,
						this::updateProgress
				);

				return null;
			}
		};

	}

}
