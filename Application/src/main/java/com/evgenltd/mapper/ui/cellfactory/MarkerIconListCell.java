package com.evgenltd.mapper.ui.cellfactory;

import com.evgenltd.mapper.core.entity.MarkerIcon;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 03:44
 */
public class MarkerIconListCell extends ListCell<MarkerIcon> {

	@Override
	protected void updateItem(MarkerIcon item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null)   {
			setText("");
			setGraphic(null);
		}else {
			setText(item.getName());
			if(item.getImage() != null) {
				final ImageView view = new ImageView(item.getImage().getImage());
				view.setFitWidth(14);
				view.setFitHeight(14);
				setGraphic(view);
			}else{
				setGraphic(null);
			}
		}
	}

}
