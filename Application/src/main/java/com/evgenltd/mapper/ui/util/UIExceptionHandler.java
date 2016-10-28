package com.evgenltd.mapper.ui.util;

import com.evgenltd.mapper.ui.UIContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.dialog.ExceptionDialog;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 17:37
 */
public class UIExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger log = LogManager.getLogger(UIExceptionHandler.class);

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		if(UIContext.get().isDebugMode()) {
			e.printStackTrace();
		}

		log.error("Uncaught exception has occurred", e);

		final ExceptionDialog exceptionDialog = new ExceptionDialog(e);
		exceptionDialog.showAndWait();

	}

}
