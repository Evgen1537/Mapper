package com.evgenltd.mapper.ui.screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 02:16
 */
public abstract class AbstractScreen {
	private Node root;

	public AbstractScreen() {
		this(null);
	}

	public AbstractScreen(String fxmlPath) {
		try {

			fxmlPath = fxmlPath == null
					? String.format("/fxml/%s.fxml", getClass().getSimpleName())
					: fxmlPath;

			final URL fxmlUrl = getClass().getResource(fxmlPath);
			if(fxmlUrl == null)	{
				return;
			}

			final FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(fxmlUrl);
			fxmlLoader.setControllerFactory(param -> this);
			fxmlLoader.load();
			root = fxmlLoader.getRoot();

		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Node getRoot() {
		return root;
	}

	protected void setRoot(Node root) {
		this.root = root;
	}

	public void refresh()	{}

	public void dispose()	{}
}
