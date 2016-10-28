package com.evgenltd.mapper.ui.screen.settings;

import com.evgenltd.mapper.core.entity.Settings;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

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

	public GeneralPane() {
		initUI();
	}

	void initUI()	{
		partlyVisibilityAlpha.textProperty().addListener(param -> partlyVisibilityAlphaTextBoxChanged());
		partlyVisibilityAlphaSlider.valueProperty().addListener(param -> partlyVisibilityAlphaSliderChanged());
	}

	void fillUI(@NotNull final Settings settings)	{
		overwriteTiles.setSelected(settings.isOverwriteTiles());
		partlyVisibilityAlphaSlider.setValue(settings.getPartlyVisibilityAlpha() * 100);
		showMarkerQuality.setSelected(settings.isShowMarkerQuality());
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
}
