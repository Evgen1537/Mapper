package com.evgenltd.mapper.ui.command.tools;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.enums.SelectionMode;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 17-07-2016 22:25
 */
@CommandTemplate(
		id = UIConstants.SELECTION_MARKER,
		text = "Marker",
		longText = "Allow selection only markers",
		graphic = "/image/selection.png",
		accelerator = "Alt+M",
		path = "/Tools",
		position = 2
)
public class SelectionMarker extends Command {

	final SettingsBean settingsBean = Context.get().getSettingsBean();

	public SelectionMarker() {
		setSelected(Objects.equals(SelectionMode.MARKER, settingsBean.getSelectionMode()));
	}

	@Override
	protected void execute(ActionEvent event) {

		settingsBean.setSelectionMode(SelectionMode.MARKER);

	}

}
