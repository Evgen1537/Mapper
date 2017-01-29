package com.evgenltd.extractor.cellfactory;

import com.evgenltd.extractor.entity.CacheFile;
import javafx.scene.control.ListCell;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 04:45</p>
 */
public class CacheFileListCell extends ListCell<CacheFile> {

	@Override
	protected void updateItem(CacheFile item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText("");
		} else {
			setText(item.getName());
		}
	}
}
