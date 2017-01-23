package com.evgenltd.mapper.ui.screen.settings;

import com.evgenltd.mapper.core.entity.Settings;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.globalmap.*;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 24-07-2016 14:33
 */
public class GeneralPane extends AbstractScreen {

	@FXML private CheckBox overwriteTiles;
	@FXML private Slider partlyVisibilityAlphaSlider;
	@FXML private TextField partlyVisibilityAlpha;
	@FXML private CheckBox showMarkerQuality;

//	@FXML private Label globalMapStatus;
//	@FXML private ProgressIndicator globalMapLinkingIndicator;
//	@FXML private Hyperlink globalMapStopLinking;
//	@FXML private Hyperlink globalMapReLink;
//	@FXML private Hyperlink globalMapUnLink;

//	private GlobalMapModel globalMapModel = UIContext.get().getGlobalMapModel();

	public GeneralPane() {
		initUI();
	}

	void initUI()	{
		partlyVisibilityAlpha.textProperty().addListener(param -> partlyVisibilityAlphaTextBoxChanged());
		partlyVisibilityAlphaSlider.valueProperty().addListener(param -> partlyVisibilityAlphaSliderChanged());
//		globalMapModel.setIntegrationStatusChangeCallback(this::updateGlobalMapIntegrationStatus);
	}

	void fillUI(@NotNull final Settings settings)	{
		overwriteTiles.setSelected(settings.isOverwriteTiles());
		partlyVisibilityAlphaSlider.setValue(settings.getPartlyVisibilityAlpha() * 100);
		showMarkerQuality.setSelected(settings.isShowMarkerQuality());
//		updateGlobalMapIntegrationStatus(globalMapModel.getIntegrationStatus());
	}

	void fillEntity(@NotNull final Settings settings)	{
		settings.setOverwriteTiles(overwriteTiles.isSelected());
		settings.setPartlyVisibilityAlpha(partlyVisibilityAlphaSlider.getValue() / 100);
		settings.setShowMarkerQuality(showMarkerQuality.isSelected());
	}

	//

	private void partlyVisibilityAlphaTextBoxChanged()	{
		String textValue = partlyVisibilityAlpha.getText().trim();

		if(!textValue.matches("\\d*"))	{
			textValue = textValue.replaceAll("[^\\d]","");
			partlyVisibilityAlpha.setText(textValue);
			return;
		}

		textValue = textValue.isEmpty() ? "0" : textValue;

		final Integer intValue = Integer.parseInt(textValue);
		if(intValue < 0)	{
			partlyVisibilityAlpha.setText("0");
			return;
		}

		if(intValue > 100)	{
			partlyVisibilityAlpha.setText("100");
			return;
		}

		partlyVisibilityAlphaSlider.setValue(intValue);
	}

	private void partlyVisibilityAlphaSliderChanged()	{
		final int intValue = (int)partlyVisibilityAlphaSlider.getValue();
		partlyVisibilityAlpha.setText(String.valueOf(intValue));
	}

	//
/*
	private void updateGlobalMapIntegrationStatus(@NotNull final GlobalMapModel.Status status) {

		if (Objects.equals(status, GlobalMapModel.Status.IN_PROGRESS)) {

			globalMapStatus.setText("Linking...");
			globalMapStatus.setTextFill(Color.BLACK);
			globalMapStatus.setGraphic(null);
			globalMapLinkingIndicator.setVisible(true);
			globalMapStopLinking.setVisible(true);
			globalMapReLink.setVisible(false);
			globalMapUnLink.setVisible(false);

		} else if (Objects.equals(status, GlobalMapModel.Status.LINKED)) {

			globalMapStatus.setText("Integration enabled");
			globalMapStatus.setTextFill(UIConstants.STATUS_CORRECT);
			globalMapStatus.setGraphic(new ImageView(UIConstants.TICK));
			globalMapLinkingIndicator.setVisible(false);
			globalMapStopLinking.setVisible(false);
			globalMapReLink.setText("Re-Link");
			globalMapReLink.setVisible(true);
			globalMapUnLink.setVisible(true);

		} else {

			globalMapStatus.setText("Integration disabled");
			globalMapStatus.setTextFill(UIConstants.STATUS_INCORRECT);
			globalMapStatus.setGraphic(new ImageView(UIConstants.CROSS));
			globalMapLinkingIndicator.setVisible(false);
			globalMapStopLinking.setVisible(false);
			globalMapReLink.setText("Link");
			globalMapReLink.setVisible(true);
			globalMapUnLink.setVisible(false);

		}

	}

	@FXML
	private void handleGlobalMapStopLinking(ActionEvent actionEvent) {
		globalMapModel.stopLinkingGlobalMap();
	}

	@FXML
	private void handleGlobalMapReLink(ActionEvent actionEvent) {
		globalMapModel.reLinkGlobalMap();
	}

	@FXML
	private void handleGlobalMapUnLink(ActionEvent actionEvent) {
		globalMapModel.unLinkGlobalMap();
	}*/
}
