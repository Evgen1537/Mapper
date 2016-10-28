package com.evgenltd.mapper.ui.cellfactory;

import com.evgenltd.mapper.core.entity.dto.LayerDto;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 15-06-2016 01:07
 */
public class LayerListCell extends ListCell<LayerDto> {

	private final boolean visiblityProperty;
	private Predicate<LayerDto> currentLayerChecker;

	public LayerListCell() {
		this(false);
	}

	public LayerListCell(boolean visibilityProperty) {
		this(visibilityProperty, layer -> false);
	}

	public LayerListCell(Predicate<LayerDto> currentLayerChecker) {
		this(false, currentLayerChecker);
	}

	public LayerListCell(
			final boolean visibilityProperty,
			final Predicate<LayerDto> currentLayerChecker
	) {
		this.visiblityProperty = visibilityProperty;
		this.currentLayerChecker = currentLayerChecker;
	}

	@Override
	protected void updateItem(LayerDto item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null) {
			setText("");
			setGraphic(null);
		}else {
			final BorderPane content = new BorderPane();
			final Label name = new Label(item.getName());
			configureIcons(name, item);
			final Label tileCount = new Label(String.format("%s tiles", item.getTileCount()));
			tileCount.setFont(Font.font(9));
			content.setLeft(name);
			content.setRight(tileCount);
			setGraphic(content);
		}
	}

	@ParametersAreNonnullByDefault
	private void configureIcons(final Label label, final LayerDto layer)	{

		final HBox iconsHolder = new HBox();
		final StringBuilder tooltip = new StringBuilder();

		if(layer.getVisibility().isPartly())	{
			final ImageView imageView = new ImageView(UIConstants.EYE_HALF);
			iconsHolder.getChildren().add(imageView);
			tooltip.append("Partly visible");
		}else if(layer.getVisibility().isNone())	{
			final ImageView imageView = new ImageView(UIConstants.EYE_CLOSE);
			iconsHolder.getChildren().add(imageView);
			tooltip.append("Invisible");
		}

		if(currentLayerChecker.test(layer))	{
			final ImageView imageView = new ImageView(UIConstants.TARGET);
			iconsHolder.getChildren().add(imageView);
			if(tooltip.length() > 0)	{
				tooltip.append("\n");
			}
			tooltip.append("Tracker: current layer");
		}

		if(!iconsHolder.getChildren().isEmpty())	{
			label.setGraphic(iconsHolder);
		}
		if(tooltip.length() > 0)	{
			label.setTooltip(new Tooltip(tooltip.toString()));
		}

	}

}
