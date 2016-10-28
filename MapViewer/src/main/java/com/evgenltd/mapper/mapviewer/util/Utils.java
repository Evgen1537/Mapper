package com.evgenltd.mapper.mapviewer.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-07-2016 21:42
 */
public class Utils {
	public static <T> Predicate<T> firstOccurrencePredicate(@NotNull final Predicate<T> predicate)	{
		return new Predicate<T>() {

			private boolean firstConditionSucceed = false;

			@Override
			public boolean test(T t) {

				if(!firstConditionSucceed & predicate.test(t))	{
					firstConditionSucceed = true;
					return true;
				}else {
					return false;
				}

			}
		};
	}
}
