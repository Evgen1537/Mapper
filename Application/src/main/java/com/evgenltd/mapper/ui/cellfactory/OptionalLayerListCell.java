package com.evgenltd.mapper.ui.cellfactory;

import com.evgenltd.mapper.core.entity.Layer;
import javafx.scene.control.ListCell;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 06-07-2016 01:29
 */
public class OptionalLayerListCell extends ListCell<Optional<Layer>> {

	private boolean forPrompt;

	public OptionalLayerListCell(boolean forPrompt) {
		this.forPrompt = forPrompt;
	}

	@Override
	protected void updateItem(Optional<Layer> item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null) {
			setText("");
		}else {
			final String caption = item
					.map(this::formatLayer)
					.orElseGet(() -> forPrompt
							? "None"
							: "");
			setText(caption);
		}
	}

	private String formatLayer(@NotNull final Layer layer)	{
		return String.format(
				"%s (%s)",
				layer.getName(),
				layer.getOrderNumber()
		);
	}
}
