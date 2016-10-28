package com.evgenltd.mapper.ui.command;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 17-07-2016 21:46
 */
@CommandTemplate(
		id = UIConstants.REDO,
		text = "Redo",
		longText = "Redo last changes",
		graphic = "/image/arrow-curve-000-left.png",
		accelerator = "Ctrl+Y",
		path = "/Other",
		position = 1
)
public class Redo extends Command {

	@Override
	protected void execute(ActionEvent event) {

		if(!Context
				.get()
				.getEnversBean()
				.isRedoAvailable())	{
			return;
		}

		final Task<Void> task = makeTask();
		setupDefaultListeners(task);

		UIContext
				.get()
				.submit(getId(), task);

	}

	private Task<Void> makeTask()	{

		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				updateTitle("Redo changes...");

				Context
						.get()
						.getEnversBean()
						.redo();

				return null;
			}
		};

	}

}
