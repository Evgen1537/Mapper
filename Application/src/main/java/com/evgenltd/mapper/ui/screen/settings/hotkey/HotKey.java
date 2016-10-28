package com.evgenltd.mapper.ui.screen.settings.hotkey;

import com.evgenltd.mapper.ui.component.command.Command;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 00:42
 */
class HotKey {
	private String id;
	private String name;
	private Node graphic;
	private KeyCombination keyCombination;
	private Class<?> scope;
	private String path;
	private int position;
	private boolean node;
	private boolean invalid;
	private boolean typing;

	public HotKey(String name) {
		this.name = name;
		this.node = true;
		this.invalid = false;
		this.typing = false;
	}

	public HotKey(Command command) {
		this.id = command.getId();
		this.name = command.getText();
		this.graphic = command.getGraphic();
		this.keyCombination = command.getAccelerator();
		this.scope = command.getScope();
		this.path = command.getPath();
		this.position = command.getPosition();
		this.node = false;
		this.invalid = false;
		this.typing = false;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Node getGraphic() {
		return graphic;
	}

	public KeyCombination getKeyCombination() {
		return keyCombination;
	}

	public void setKeyCombination(KeyCombination keyCombination) {
		this.keyCombination = keyCombination;
	}

	public Class<?> getScope() {
		return scope;
	}

	public String getPath() {
		return path;
	}

	public int getPosition() {
		return position;
	}

	public boolean isNode() {
		return node;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isTyping() {
		return typing;
	}

	public void setTyping(boolean typing) {
		this.typing = typing;
	}
}
