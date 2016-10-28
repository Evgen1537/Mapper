package com.evgenltd.mapper.ui.command.tools;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 09-07-2016 20:30
 */
@CommandTemplate(
		id = UIConstants.SHOW_GRID,
		text = "Show grid",
		longText = "Show/hide grid",
		graphic = "/image/grid.png",
		accelerator = "Alt+G",
		path = "/Tools",
		position = 3
)
public class ShowGrid extends Command{

	final SettingsBean settingsBean = Context.get().getSettingsBean();

	public ShowGrid() {
		setSelected(!settingsBean.isHideGrid());
	}

	@Override
	protected void execute(ActionEvent event) {

		final boolean showGrid = isSelected();

		settingsBean.setHideGrid(!showGrid);

		UIContext
				.get()
				.getMapViewerWrapper()
				.repaint();
	}

}
