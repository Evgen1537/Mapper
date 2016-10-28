package com.evgenltd.mapper.ui.screen.settings.hotkey;

import com.evgenltd.mapper.core.entity.Settings;
import com.evgenltd.mapper.ui.screen.AbstractScreen;
import com.evgenltd.mapper.ui.screen.settings.SettingsDialog;
import com.sun.javafx.scene.control.skin.TreeViewSkin;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 23:37
 */
public class HotKeyPane extends AbstractScreen {

	@FXML private TreeView<HotKey> hotKeyView;

	private final SettingsDialog parent;

	private boolean typing;
	private HotKey currentHotKey;

	private final HotKeyContainer container = new HotKeyContainer();

	public HotKeyPane(@NotNull final SettingsDialog parent) {
		this.parent = parent;
	}

	public void initUI()	{

		final RepaintSkin<HotKey> skin = new RepaintSkin<>(hotKeyView);
		hotKeyView.setSkin(skin);

		hotKeyView.setRoot(container.getTreeRoot());
		hotKeyView.setShowRoot(false);
		hotKeyView.setCellFactory(param -> new HotKeyTreeCell());
		hotKeyView.setOnMouseClicked(this::handleMouseClicked);
		hotKeyView.setOnKeyPressed(this::handleKeyPressed);
		hotKeyView.setOnKeyReleased(this::handleKeyReleased);

	}

	public void fillUI()	{
		container.fillContainer();
	}

	public void fillEntity(Settings settings)	{
		container.fillEntity(settings);
	}

	public boolean validate()	{
		return HotKeyChecker.check(container.getTreeItemList());
	}

	// handlers

	// event handlers

	private void handleMouseClicked(MouseEvent event) {
		if(event.getClickCount() != 2 || typing) {
			return;
		}

		final TreeItem<HotKey> selectedItem = hotKeyView.getSelectionModel().getSelectedItem();
		if(selectedItem == null
				|| selectedItem.getValue() == null
				|| selectedItem.getValue().isNode()
				) {
			return;
		}
		currentHotKey = selectedItem.getValue();
		currentHotKey.setTyping(true);
		typing = true;
		repaint();
	}

	private void handleKeyPressed(KeyEvent event) {
		if(typing) {
			event.consume();
		}
	}

	private void handleKeyReleased(KeyEvent event) {
		if(event.getCode().isModifierKey()
				|| event.getCode().isWhitespaceKey()
				|| event.getCode().isMediaKey()
				|| !typing
				) {
			return;
		}
		if(event.getCode().equals(KeyCode.ESCAPE)) {
			event.consume();
		}else {
			final KeyCodeCombination keyCodeCombination = new KeyCodeCombination(
					event.getCode(),
					event.isShiftDown() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
					event.isControlDown() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
					event.isAltDown() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
					KeyCombination.ModifierValue.UP,
					KeyCombination.ModifierValue.UP
			);
			currentHotKey.setKeyCombination(keyCodeCombination);
		}
		currentHotKey.setTyping(false);
		currentHotKey = null;
		typing = false;
		repaint();
		final boolean validationResult = validate();
		parent.updateValidationState(validationResult);
	}

	// util

	private void repaint() {
		((RepaintSkin)hotKeyView.getSkin()).refresh();
	}

	private static class RepaintSkin<T> extends TreeViewSkin<T> {
		public RepaintSkin(TreeView treeView) {
			super(treeView);
		}

		public void refresh() {
			super.flow.recreateCells();
		}
	}
}
