package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.CommonDao;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.entity.Layer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 02-02-2017 02:00</p>
 */
public class LayerProperties extends DialogScreen<Void> {

	@FXML
	private TextField layerName;
	@FXML
	private CheckBox preventRemove;
	private Button buttonOk;

	private Loader loader = Context.get().getLoader();
	private CommonDao commonDao = Context.get().getCommonDao();
	private Layer layer;

	public LayerProperties() {
		buttonOk = lookupButton(ButtonType.OK);
		layerName.setOnKeyTyped(event -> verify());
	}

	public void setLayer(@NotNull final Long id) {
		layer = loader.loadLayer(id);
		layerName.setText(layer.getName());
		preventRemove.setSelected(layer.getPreventRemove());
	}

	@Override
	protected String getTitle() {
		return "Layer Properties";
	}

	@Override
	protected List<ButtonType> getButtonTypes() {
		return Arrays.asList(
				ButtonType.OK,
				ButtonType.CANCEL
		);
	}

	@Override
	protected Void resultConverter(ButtonType buttonType) {
		if (!buttonType.equals(ButtonType.OK)) {
			return null;
		}

		layer.setName(layerName.getText());
		layer.setPreventRemove(preventRemove.isSelected());
		commonDao.update(layer);

		return null;
	}

	private void hideErrors()	{
		buttonOk.setDisable(false);

		layerName.getStyleClass().remove("error");
		layerName.setTooltip(null);
	}

	private void verify() {
		hideErrors();

		if(layerName.getText().trim().isEmpty())	{
			buttonOk.setDisable(true);
			layerName.getStyleClass().add("error");
			layerName.setTooltip(new Tooltip("Name should be specified"));
		}
	}

}
