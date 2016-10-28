package com.evgenltd.mapper.core.util;

import com.evgenltd.mapper.core.Context;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 19-07-2016 10:10
 */
@Plugin(name="RollbarAppender", category = "Core", elementType = "appender", printObject = true)
public class RollbarAppender extends AbstractAppender {

	private final List<Throwable> trowedExceptions = new ArrayList<>();

	protected RollbarAppender(
			String name,
			Filter filter,
			Layout<? extends Serializable> layout,
			boolean ignoreExceptions
	) {
		super(name, filter, layout, ignoreExceptions);
	}

	@Override
	public void append(LogEvent logEvent) {
		if(!logEvent.getLevel().equals(Level.ERROR)) {
			return;
		}

		final Throwable throwable = logEvent.getThrown();
		if(throwable == null)	{
			return;
		}

		if(!isAlreadyThrown(throwable))	{
			Context.get().getRollbar().error(throwable);
		}
	}

	@PluginFactory
	public static RollbarAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter
//			@PluginAttribute("url") String url,
//			@PluginAttribute("apiKey") String apiKey,
//			@PluginAttribute("env") String env
	) {
		if (name == null) {
			LOGGER.error("No name provided for RollbarAppender");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}

		Context.get().getRollbar();

		return new RollbarAppender(name, filter, layout, false);
	}

	private boolean isAlreadyThrown(final Throwable newThrowable)    {
		final boolean isExceptionAlreadyThrown = trowedExceptions
				.stream()
				.anyMatch(throwable -> Arrays.equals(
						throwable.getStackTrace(),
						newThrowable.getStackTrace()
				));

		if(isExceptionAlreadyThrown)    {
			return true;
		}

		trowedExceptions.add(newThrowable);
		return false;
	}
}
