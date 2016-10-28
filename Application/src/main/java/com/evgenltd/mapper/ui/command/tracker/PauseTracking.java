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
 * Created: 24-08-2016 23:42
 */
@CommandTemplate(
		id = UIConstants.PAUSE_TRACKING,
		text = "Pause Tracking",
		longText = "Stop tracking for layers and sessions",
		graphic = "/image/control-pause.png",
		accelerator = "F2",
		path = "/Tracker",
		position = 1
)
public class PauseTracking extends Command{

	public PauseTracking() {
		setSelected(!Context.get().getSettingsBean().isEnableTracker());
	}

	@Override
	protected void execute(ActionEvent event) {
		Context.get().getSettingsBean().setEnableTracker(false);
		UIContext.get().getCommandManager().updateCommandDisableState();
	}

}
