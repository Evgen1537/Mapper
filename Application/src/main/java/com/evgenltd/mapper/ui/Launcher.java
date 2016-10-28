package com.evgenltd.mapper.ui;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 12-06-2016 23:51
 */
public class Launcher extends Preloader {
	private Stage stage;

	@Override
	public void start(Stage primaryStage) throws Exception {

		final FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(Launcher.class.getResource("/fxml/Launcher.fxml"));

		final Pane root = fxmlLoader.load();
		final Text version = (Text)root.lookup("#version");
		version.setText(getClass().getPackage().getImplementationVersion());

		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/app_icon_16.png")));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/app_icon_32.png")));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/app_icon_64.png")));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/app_icon_128.png")));

		stage = primaryStage;
		stage.setScene(new Scene(root));
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification info) {
		if(info.getType().equals(StateChangeNotification.Type.BEFORE_START))	{
			stage.hide();
		}
	}
}
