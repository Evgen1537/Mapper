package com.evgenltd.mapper;

import com.sun.glass.ui.Screen;
import com.sun.javafx.application.PlatformImpl;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 11-07-2016 21:55
 */
public class JavaFXSpringRunner extends SpringJUnit4ClassRunner {
	public JavaFXSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	@Override
	public void run(RunNotifier notifier) {
		PlatformImpl.startup(Screen::notifySettingsChanged);
		super.run(notifier);
	}
}
