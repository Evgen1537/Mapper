package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.core.entity.MarkerIcon;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.TilePane;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 30-01-2017 01:00</p>
 */
public class IconChooser extends DialogScreen<MarkerIcon> {

	@Override
	protected String getTitle() {
		return "Select Icon";
	}

	private void initUI() {

		final TilePane iconPane = new TilePane(Orientation.VERTICAL, 4, 4);
		final ScrollPane scrollPane = new ScrollPane(iconPane);
		setRoot(scrollPane);

	}

}
