package com.evgenltd.mapper.ui.component.command;

import com.evgenltd.mapper.ui.component.command.scope.Default;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 19:14
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandTemplate {

	String id();

	String text() default "";

	String longText() default "";

	String graphic() default "";

	String accelerator() default "";

	String path() default "";

	int position() default 0;

	Class<?> scope() default Default.class;
}
