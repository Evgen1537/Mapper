package com.evgenltd.extractor.cellfactory;

import com.evgenltd.extractor.Constants;
import com.evgenltd.extractor.screen.ResourceEntry;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 01:40</p>
 */
public class ResourceEntryTreeCell extends TreeCell<ResourceEntry> {

	@Override
	protected void updateItem(ResourceEntry item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText("");
			setGraphic(null);
		} else {
			setText(item.getName());
			setGraphic(
					item.isDirectory()
							? new ImageView(Constants.FOLDER)
							: new ImageView(Constants.FILE)
			);
		}
	}
}
