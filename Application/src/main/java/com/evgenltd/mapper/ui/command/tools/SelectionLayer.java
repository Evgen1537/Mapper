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
 * Created: 17-07-2016 22:26
 */
@CommandTemplate(
		id = UIConstants.SELECTION_LAYER,
		text = "Layer",
		longText = "Allow selection only layers",
		graphic = "/image/selection.png",
		accelerator = "Alt+L",
		path = "/Tools",
		position = 1
)
public class SelectionLayer extends Command {

	final SettingsBean settingsBean = Context.get().getSettingsBean();

	public SelectionLayer() {
		setSelected(Objects.equals(SelectionMode.LAYER, settingsBean.getSelectionMode()));
	}

	@Override
	protected void execute(ActionEvent event) {

		settingsBean.setSelectionMode(SelectionMode.LAYER);

	}

}
