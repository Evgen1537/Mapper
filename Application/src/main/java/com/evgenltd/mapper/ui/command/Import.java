package com.evgenltd.mapper.ui.command;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.screen.Importer;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 08-07-2016 01:44
 */
@CommandTemplate(
		id = UIConstants.IMPORT,
		text = "Import",
		longText = "Import data from 1.x version",
		graphic = "/image/database-import.png",
		path = "/Other",
		position = 2
)
public class Import extends Command {

	@Override
	protected void execute(ActionEvent event) {

		new Importer().showAndWait();
		UIContext
				.get()
				.refresh();

	}

}
