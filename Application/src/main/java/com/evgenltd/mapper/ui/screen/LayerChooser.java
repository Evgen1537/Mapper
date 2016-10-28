package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.entity.dto.LayerDto;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.ui.cellfactory.LayerListCell;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 07-07-2016 23:41
 */
public class LayerChooser extends DialogScreen<Long> {

	private LayerType[] allowedLayerTypes;
	private final ListView<LayerDto> layerListView = new ListView<>();

	public LayerChooser(final LayerType... layerTypes) {
		super();

		final Stage dialogStage = (Stage)getDialog().getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(UIConstants.LAYER);

		setRoot(layerListView);
		getDialog().getDialogPane().setContent(layerListView);
		layerListView.setCellFactory(param -> new LayerListCell());

		allowedLayerTypes = layerTypes;
		loadData();
	}

	@Override
	protected String getTitle() {
		return "Select Layer";
	}

	@Override
	protected Long resultConverter(ButtonType buttonType) {
		if(buttonType.equals(ButtonType.OK))	{
			return Optional
					.ofNullable(layerListView
										.getSelectionModel()
										.getSelectedItem()
					)
					.map(LayerDto::getId)
					.orElse(null);
		}
		return null;
	}

	@Override
	protected List<ButtonType> getButtonTypes() {
		return Arrays.asList(ButtonType.OK, ButtonType.CANCEL);
	}

	private void loadData()	{
		layerListView.getItems().addAll(
				Context
						.get()
						.getLoader()
						.loadLayerDtoListByTypes(allowedLayerTypes)
		);

	}

	public static LayerChooser create(final LayerType... layerTypes)	{
		return new LayerChooser(layerTypes);
	}
}
