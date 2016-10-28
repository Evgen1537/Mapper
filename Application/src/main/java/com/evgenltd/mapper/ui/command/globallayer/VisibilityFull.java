package com.evgenltd.mapper.ui.command.globallayer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.enums.Visibility;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 09-07-2016 13:01
 */
@CommandTemplate(
		id = UIConstants.GLOBAL_LAYER_FULL,
		text = "Full",
		longText = "Make layer visible",
		graphic = "/image/eye.png"
)
public class VisibilityFull extends Command {

	private Long layerId;

	public void setLayerId(@NotNull final Long layerId) {
		this.layerId = layerId;
	}

	@Override
	protected void execute(ActionEvent event) {

		if(layerId == null)	{
			return;
		}

		Context
				.get()
				.getLayerBean()
				.setLayerVisibility(Collections.singletonList(layerId), Visibility.FULL);

		UIContext
				.get()
				.refresh();

	}

}
