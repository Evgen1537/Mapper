package com.evgenltd.mapper.ui.cellfactory;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.util.Constants;
import javafx.scene.control.ListCell;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 06-07-2016 01:29
 */
public class LayerListCell extends ListCell<Layer> {

	private boolean forPrompt;

	public LayerListCell(boolean forPrompt) {
		this.forPrompt = forPrompt;
	}

	@Override
	protected void updateItem(Layer item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null) {
			setText("");
		}else {
			setText(formatLayer(item));
		}
	}

	private String formatLayer(@NotNull final Layer layer)	{
		if (Objects.equals(layer, Constants.NONE)) {
			return "None";
		}
		return String.format(
				"%s (%s)",
				layer.getName(),
				layer.getOrderNumber()
		);
	}
}
