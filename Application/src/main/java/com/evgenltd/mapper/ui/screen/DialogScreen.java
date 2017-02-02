package com.evgenltd.mapper.ui.screen;

import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Project: KwickUI
 * Author:  Evgeniy
 * Created: 18-05-2016 23:39
 */
public abstract class DialogScreen<R> extends AbstractScreen {

	private Dialog<R> dialog;

	public DialogScreen() {
		this(null);
	}

	public DialogScreen(final String fxmlPath)	{
		super(fxmlPath);
		dialog = new Dialog<>();
		dialog.getDialogPane().setContent(getRoot());
		dialog.setTitle(getTitle());
		final Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(getIcon());
		dialog.setResultConverter(this::resultConverter);
		dialog.getDialogPane().getButtonTypes().addAll(getButtonTypes());
	}

	protected Dialog<R> getDialog() {
		return dialog;
	}

	protected abstract String getTitle();

	protected Image getIcon() {
		return UIConstants.APP_ICON_16;
	}

	protected R resultConverter(final ButtonType buttonType)	{
		return null;
	}

	protected List<ButtonType> getButtonTypes()	{
		return Collections.emptyList();
	}

	public Button lookupButton(@NotNull final ButtonType buttonType) {
		return (Button)getDialog().getDialogPane().lookupButton(buttonType);
	}

	public void show() {
		dialog.show();
	}

	public Optional<R> showAndWait() {
		return dialog.showAndWait();
	}

	public void close() {
		dialog.close();
	}

	public void hide() {
		dialog.hide();
	}
}
