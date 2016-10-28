package com.evgenltd.mapper.ui.command.marker;

import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.component.command.scope.MarkerEdit;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 27-06-2016 20:16
 */
@CommandTemplate(
		id = UIConstants.MARKER_EDIT_CANCEL,
		text = "Cancel",
		longText = "Cancel editing",
		graphic = "/image/cross_32.png",
		accelerator = "Esc",
		path = "/Marker Editing",
		position = 3,
		scope = MarkerEdit.class
)
public class MarkerEditCancel extends Command {

	@Override
	protected void execute(ActionEvent event) {

		UIContext
				.get()
				.getMapViewerWrapper()
				.endEditingNode();

		UIContext
				.get()
				.getMarkerEditing()
				.setMarker(null);

		UIContext
				.get()
				.getToolbar()
				.hideMarkerEditingTab();
		UIContext
				.get()
				.getCommandManager()
				.updateHotKeysFromSettings();

		UIContext.get().refresh();

	}

}
