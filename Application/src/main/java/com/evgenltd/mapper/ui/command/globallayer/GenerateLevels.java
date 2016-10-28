package com.evgenltd.mapper.ui.command.globallayer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-07-2016 00:55
 */
@CommandTemplate(
		id = UIConstants.GLOBAL_LAYER_GENERATE_LEVELS,
		text = "Generate levels",
		longText = "Generate layer zoom levels",
		graphic = "/image/layers-stack.png"
)
public class GenerateLevels extends Command {

	private Long layerId;

	public void setLayerId(@NotNull final Long layerId) {
		this.layerId = layerId;
	}

	@Override
	protected void execute(ActionEvent event) {

		if(layerId == null)	{
			return;
		}

		final Task<Void> task = makeTask();
		setupDefaultListeners(task);

		UIContext.get().submit(getId(), task);

	}

	private Task<Void> makeTask()	{

		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Generating levels...");

				Context.get().getLayerBean().generateLevels(
						layerId,
						this::updateMessage,
						this::updateProgress
				);

				return null;
			}
		};

	}

}
