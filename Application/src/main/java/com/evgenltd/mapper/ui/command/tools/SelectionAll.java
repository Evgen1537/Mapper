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
		id = UIConstants.SELECTION_ALL,
		text = "All",
		longText = "Allow selection all objects",
		graphic = "/image/selection.png",
		accelerator = "Alt+A",
		path = "/Tools",
		position = 0
)
public class SelectionAll extends Command {

	final SettingsBean settingsBean = Context.get().getSettingsBean();

	public SelectionAll() {
		setSelected(Objects.equals(SelectionMode.ALL,settingsBean.getSelectionMode()));
	}

	@Override
	protected void execute(ActionEvent event) {

		settingsBean.setSelectionMode(SelectionMode.ALL);

	}

}
