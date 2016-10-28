package com.evgenltd.mapper.ui;

import com.evgenltd.mapper.core.Config;
import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.DataBaseBackup;
import com.evgenltd.mapper.ui.screen.main.Main;
import com.evgenltd.mapper.ui.util.UIExceptionHandler;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 12-06-2016 23:52
 */
public class Application extends javafx.application.Application {

	public static void main(String[] args)	{
		System.setProperty("javafx.preloader", Launcher.class.getName());
		launch(args);
	}

	@Override
	public void init() throws Exception {

		if(!hasParameter(Constants.SKIP_BACKUP_ON_STARTUP))	{
			DataBaseBackup.doBackup();
		}

		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		if(hasParameter(Constants.DEBUG_MODE_FLAG))	{
			UIContext.get().initializationDebugMode(primaryStage, getHostServices());
		}else {
			UIContext.get().initialization(primaryStage, getHostServices());
		}

		primaryStage.setTitle(String.format(
				"%s %s",
				getClass().getPackage().getImplementationTitle(),
				getClass().getPackage().getImplementationVersion()
		));
		addAppIcons(
				primaryStage,
				"/image/app_icon_16.png",
				"/image/app_icon_32.png",
				"/image/app_icon_64.png",
				"/image/app_icon_128.png"
		);

		primaryStage.getScene().getStylesheets().addAll("/css/mapper.css", "/css/ribbon.css");

		primaryStage.show();

		Thread.currentThread().setUncaughtExceptionHandler(new UIExceptionHandler());

		UIContext.get().openScreen(new Main());

		Context.get().getRollbar().logLaunch();
	}

	@Override
	public void stop() throws Exception {
		UIContext
				.get()
				.getMapViewerWrapper()
				.stop();
	}

	private void addAppIcons(final Stage stage, final String... paths)	{
		for(String path : paths) {
			stage.getIcons().add(new Image(path));
		}
	}

	private boolean hasParameter(final String parameterName)	{
		return getParameters()
				.getUnnamed()
				.contains(parameterName);
	}

}
