package com.evgenltd.mapper.ui.screen;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

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
		dialog.setResultConverter(this::resultConverter);
		dialog.getDialogPane().getButtonTypes().addAll(getButtonTypes());
	}

	protected Dialog<R> getDialog() {
		return dialog;
	}

	protected abstract String getTitle();

	protected R resultConverter(final ButtonType buttonType)	{
		return null;
	}

	protected List<ButtonType> getButtonTypes()	{
		return Collections.emptyList();
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
