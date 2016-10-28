package com.evgenltd.mapper.ui.command.tracker;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 24-08-2016 23:39
 */
@CommandTemplate(
		id = UIConstants.START_TRACKING,
		text = "Start Tracking",
		longText = "Begin tracking for layers and sessions",
		graphic = "/image/control.png",
		accelerator = "F9",
		path = "/Tracker",
		position = 0
)
public class StartTracking extends Command {

	public StartTracking() {
		setSelected(Context.get().getSettingsBean().isEnableTracker());
	}

	@Override
	protected void execute(ActionEvent event) {
		Context.get().getSettingsBean().setEnableTracker(true);
		UIContext.get().getCommandManager().updateCommandDisableState();
	}

}
