package com.evgenltd.mapper.ui.command.globallayer;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandTemplate;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 09-07-2016 13:00
 */
@CommandTemplate(
		id = UIConstants.GLOBAL_LAYER_DOWN,
		text = "Down",
		longText = "Move layer down",
		graphic = "/image/arrow-270.png"
)
public class ReorderDown extends Command {

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
				.bringLayersForward(Collections.singletonList(layerId), 1, Arrays.asList(LayerType.GROUND, LayerType.CAVE));

		UIContext
				.get()
				.refresh();

	}

}
