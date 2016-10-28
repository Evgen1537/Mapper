package com.evgenltd.mapper.core.util;

import org.jetbrains.annotations.NotNull;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 14:20
 */
public class Profiler {

	private long start;
	private String name;

	public Profiler() {
		this("dummy");
	}

	public Profiler(final String name) {
		start = System.currentTimeMillis();
		this.name = name;
	}

	public static Profiler start()	{
		return new Profiler();
	}

	public static Profiler start(@NotNull final String name)	{
		return new Profiler(name);
	}

	public void end()	{
		final long time = System.currentTimeMillis() - start;
		System.err.println(String.format(
				"Action '%s': %s ms",
				name,
				time
		));
	}

}
