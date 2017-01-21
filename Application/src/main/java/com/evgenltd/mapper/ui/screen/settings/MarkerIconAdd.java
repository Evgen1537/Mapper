package com.evgenltd.mapper.ui.screen.settings;

import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.ui.screen.DialogScreen;
import com.evgenltd.mapper.ui.util.UIConstants;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 23:41
 */
public class MarkerIconAdd extends DialogScreen<MarkerIcon> {

	@FXML private TextField name;
	@FXML private Button imageHolder;
	@FXML private ImageView imageView;

	private Image image;
	private Button buttonOk;

	public MarkerIconAdd() {
		initUI();
	}

	@Override
	protected String getTitle() {
		return "Add Marker Icon";
	}

	@Override
	protected MarkerIcon resultConverter(ButtonType buttonType) {
		if(buttonType.equals(ButtonType.OK))	{
			return EntityFactory.createMarkerIcon(name.getText(), image);
		}
		return null;
	}

	private void initUI()	{
		getDialog().getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		getDialog().setResultConverter(this::resultConverter);

		name.setOnKeyTyped(event -> verify());

		buttonOk = (Button)getDialog().getDialogPane().lookupButton(ButtonType.OK);
		buttonOk.setDisable(false);
	}

	// other

	private void hideErrors()	{
		buttonOk.setDisable(false);

		name.getStyleClass().remove("error");
		name.setTooltip(null);

		imageHolder.getStyleClass().remove("error");
		imageHolder.setTooltip(null);

	}

	private void verify()	{
		hideErrors();

		boolean failed = false;
		if(name.getText().trim().isEmpty())	{
			failed = true;
			name.getStyleClass().add("error");
			name.setTooltip(new Tooltip("Name should be specified"));
		}
		if(image == null)	{
			failed = true;
			imageView.setImage(UIConstants.HOME_ICON);
			imageHolder.getStyleClass().add("error");
			imageHolder.setTooltip(new Tooltip("Incorrect image"));
		}

		buttonOk.setDisable(failed);
	}

	// fxml handlers

	@FXML
	private void handleImageSelection(ActionEvent event) {

		UIUtils.askFile().ifPresent(imageFile -> {

			try {

				image = UIUtils.fitMarkerTypeImage(imageFile);
				imageView.setImage(image);
				imageView.setOpacity(1);

			}catch(IOException e) {
				image = null;
				imageView.setOpacity(0.1);
			}

			verify();

		});

	}
}
