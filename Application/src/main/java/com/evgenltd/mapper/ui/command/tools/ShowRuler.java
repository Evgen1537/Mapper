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
 * Created: 09-07-2016 20:01
 */
@CommandTemplate(
		id = UIConstants.SHOW_RULER,
		text = "Show ruler",
		longText = "Show/hide ruler",
		graphic = "/image/ui-ruler.png",
		accelerator = "Alt+R",
		path = "/Tools",
		position = 4
)
public class ShowRuler extends Command {

	final SettingsBean settingsBean = Context.get().getSettingsBean();

	public ShowRuler() {
		setSelected(!settingsBean.isHideRuler());
	}

	@Override
	protected void execute(ActionEvent event) {

		final boolean showRuler = isSelected();

		settingsBean.setHideRuler(!showRuler);

		UIContext
				.get()
				.getMapViewerWrapper()
				.repaint();
	}

}
