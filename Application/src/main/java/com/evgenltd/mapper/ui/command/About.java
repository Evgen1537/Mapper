package com.evgenltd.mapper.ui.command;

import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 08-07-2016 10:54
 */
@CommandTemplate(
		id = UIConstants.ABOUT,
		text = "About",
		longText = "About program",
		graphic = "/image/balloon.png"
)
public class About extends Command {

	@Override
	protected void execute(ActionEvent event) {

		new com.evgenltd.mapper.ui.screen.About().show();

	}

}
