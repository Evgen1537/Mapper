package com.evgenltd.mapper.ui.screen.settings;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.entity.Settings;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import com.evgenltd.mapper.ui.screen.DialogScreen;
import com.evgenltd.mapper.ui.screen.settings.hotkey.HotKeyPane;
import com.evgenltd.mapper.ui.screen.settings.tracker.TrackerPane;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 23:33
 */
public class SettingsDialog extends DialogScreen<Void> {

	@FXML private TabPane tabPane;
	@FXML private Tab generalTab;
	@FXML private Tab markerIconsTab;
	@FXML private Tab hotKeysTab;
	@FXML private Tab trackerTab;
	private Button buttonApply;

	private final GeneralPane generalPane = new GeneralPane();
	private final MarkerIconPane markerIconPane = new MarkerIconPane();
	private final HotKeyPane hotKeyPane = new HotKeyPane(this);
	private final TrackerPane trackerPane = new TrackerPane();

	private final SettingsBean settingsBean = Context.get().getSettingsBean();
	private final CommandManager commandManager = UIContext.get().getCommandManager();

	private Settings settings;

	public SettingsDialog() {
		initUI();
		loadData();
	}

	@Override
	protected String getTitle() {
		return "Settings";
	}

	@Override
	protected Void resultConverter(ButtonType buttonType) {
		if(buttonType.equals(ButtonType.APPLY)) {
			fillEntity(settings);
			settingsBean.setSettings(settings);
			settingsBean.save();
			commandManager.updateHotKeysFromSettings();
			markerIconPane.persist();
		}
		return null;
	}

	private void initUI()	{

		generalTab.setContent(generalPane.getRoot());
		generalPane.initUI();
		markerIconsTab.setContent(markerIconPane.getRoot());
		markerIconPane.initUI();
		hotKeysTab.setContent(hotKeyPane.getRoot());
		hotKeyPane.initUI();
		trackerTab.setContent(trackerPane.getRoot());
		trackerPane.initUI();

		getDialog().getDialogPane().getButtonTypes().addAll(
				ButtonType.APPLY,
				ButtonType.CANCEL
		);
		final Stage dialogStage = (Stage)getDialog().getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(UIConstants.GEAR);

		buttonApply = (Button)getDialog().getDialogPane().lookupButton(ButtonType.APPLY);
		buttonApply.addEventFilter(ActionEvent.ACTION, this::validate);
		buttonApply.setDefaultButton(true);

	}

	private void loadData() {
		settings = settingsBean.getSettings().copy();
		fillUI(settings);
	}

	private void fillUI(@NotNull final Settings settings)	{
		generalPane.fillUI(settings);
		hotKeyPane.fillUI();
		markerIconPane.fillUI();
		trackerPane.fillUI(settings);
	}

	private void fillEntity(Settings settings)	{
		generalPane.fillEntity(settings);
		hotKeyPane.fillEntity(settings);
		trackerPane.fillEntity(settings);
	}

	private void validate(final Event event)	{
		boolean validated = hotKeyPane.validate();
		if(!validated)	{
			tabPane.getSelectionModel().select(hotKeysTab);
			event.consume();
		}
		updateValidationState(validated);
	}

	public void updateValidationState(final boolean validationResult)	{
		buttonApply.setDisable(!validationResult);
	}
}
