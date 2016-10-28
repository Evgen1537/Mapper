package com.evgenltd.mapper.ui.screen.settings.hotkey;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 00:42
 */
class HotKeyTreeCell extends TreeCell<HotKey> {
	private final Label ALT_KEY = new Label("Alt");
	private final Label CONTROL_KEY = new Label("Ctrl");
	private final Label SHIFT_KEY = new Label("Shift");
	private final Label TYPING = new Label("<Type combination>");
	private final Effect DROP_SHADOW = new DropShadow(8, Color.RED);
	private final Effect INNER_SHADOW = new InnerShadow(8, Color.RED);

	private GridPane root;
	private Pane graphic;
	private Label label;
	private HBox hotKeyPane;

	public HotKeyTreeCell() {
		ALT_KEY.getStyleClass().add("key-label");
		CONTROL_KEY.getStyleClass().add("key-label");
		SHIFT_KEY.getStyleClass().add("key-label");
		TYPING.getStyleClass().add("typing-label");

		root = new GridPane();
		graphic = new Pane();
		label = new Label();
		hotKeyPane = new HBox();
		hotKeyPane.setAlignment(Pos.CENTER_RIGHT);
		hotKeyPane.setSpacing(4);

		GridPane.setColumnIndex(label, 1);

		GridPane.setColumnIndex(hotKeyPane, 2);
		GridPane.setHgrow(hotKeyPane, Priority.ALWAYS);
		GridPane.setHalignment(hotKeyPane, HPos.RIGHT);

		root.getChildren().addAll(graphic, label, hotKeyPane);
	}

	@Override
	protected void updateItem(HotKey item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null) {
			setGraphic(null);
		}else {
			setGraphic(root);

			graphic.getChildren().clear();
			if(item.getGraphic() != null)	{
				final ImageView itemImageView = (ImageView)item.getGraphic();
				final ImageView imageView = new ImageView(itemImageView.getImage());
				imageView.setFitWidth(16);
				imageView.setFitHeight(16);
				graphic.getChildren().add(imageView);
			}

			label.setText(item.getName());
			label.setEffect(item.isInvalid() && item.isNode()
									? DROP_SHADOW
									: null
			);

			hotKeyPane.getChildren().clear();
			if(item.isNode()) {
				return;
			}
			if(!item.isTyping()) {
				renderGeneral(item);
			}else {
				renderTyping();
			}
		}
	}

	private void renderGeneral(HotKey item) {
		final KeyCombination accelerator = item.getKeyCombination();
		if(accelerator == null)	{
			return;
		}

		if(accelerator.getAlt().equals(KeyCombination.ModifierValue.DOWN)) {
			hotKeyPane.getChildren().add(ALT_KEY);
		}
		if(accelerator.getControl().equals(KeyCombination.ModifierValue.DOWN)) {
			hotKeyPane.getChildren().add(CONTROL_KEY);
		}
		if(accelerator.getShift().equals(KeyCombination.ModifierValue.DOWN)) {
			hotKeyPane.getChildren().add(SHIFT_KEY);
		}

		final Label key = buildKeyLabel(accelerator);
		hotKeyPane.getChildren().add(key);

		for(Node node : hotKeyPane.getChildren()) {
			node.setEffect(item.isInvalid()
								   ? INNER_SHADOW
								   : null);
		}
	}

	private Label buildKeyLabel(KeyCombination keyCombination) {
		final Label key = new Label();
		if(keyCombination instanceof KeyCodeCombination) {
			final KeyCodeCombination keyCodeCombination = (KeyCodeCombination)keyCombination;
			key.setText(keyCodeCombination.getCode().getName());
		}else if(keyCombination instanceof KeyCharacterCombination) {
			final KeyCharacterCombination keyCharCombination = (KeyCharacterCombination)keyCombination;
			key.setText(keyCharCombination.getCharacter());
		}
		key.getStyleClass().add("key-label");
		return key;
	}

	private void renderTyping() {
		hotKeyPane.getChildren().add(TYPING);
	}
}
