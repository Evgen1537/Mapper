package com.evgenltd.mapper.ui.command;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.screen.settings.SettingsDialog;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 23:58
 */
@CommandTemplate(
		id = UIConstants.OPEN_SETTINGS_COMMAND,
		text = "Settings",
		longText = "Application settings",
		graphic = "/image/gear.png",
		accelerator = "Alt+O",
		path = "/Other",
		position = 3
)
public class OpenSettings extends Command {

	@Override
	protected void execute(ActionEvent event) {
		new SettingsDialog().showAndWait();
		UIContext
				.get()
				.refresh();
	}

}
