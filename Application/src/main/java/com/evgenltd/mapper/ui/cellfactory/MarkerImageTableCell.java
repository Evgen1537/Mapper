package com.evgenltd.mapper.ui.cellfactory;

import com.evgenltd.mapper.core.entity.Marker;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 15-06-2016 01:26
 */
public class MarkerImageTableCell extends TableCell<Marker,Image> {

	@Override
	protected void updateItem(Image item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null)   {
			setGraphic(null);
		}else {
			ImageView imageView = new ImageView(item);
			imageView.setFitWidth(16);
			imageView.setFitHeight(16);
			setGraphic(imageView);
		}
	}

}
