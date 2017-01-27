package com.evgenltd.mapper.ui;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.DataBaseBackup;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 12-06-2016 23:52
 */
public class Mapper extends Application {

	public static void main(String[] args)	{
		System.setProperty("javafx.preloader", Launcher.class.getName());
		launch(args);
	}

	@Override
	public void init() throws Exception {

		final boolean hasSkipBackupFlag = getParameters()
				.getUnnamed()
				.contains(Constants.SKIP_BACKUP_ON_STARTUP);

		if(!hasSkipBackupFlag)	{
			DataBaseBackup.doBackup();
		}

		Context.get().init();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		final boolean hasDebugModeFlag = getParameters()
				.getUnnamed()
				.contains(Constants.DEBUG_MODE_FLAG);

		if(hasDebugModeFlag)	{
			UIContext.get().initWithDebugMode(primaryStage, getHostServices());
		}else {
			UIContext.get().init(primaryStage, getHostServices());
		}

	}

	@Override
	public void stop() throws Exception {
		UIContext.get().close();
		Context.get().close();
	}

}
