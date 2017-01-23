package com.evgenltd.mapper.core.exception;

import org.jetbrains.annotations.NonNls;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 22-01-2017 23:31
 */
public class GlobalMapException extends RuntimeException {

	public GlobalMapException() {
	}

	public GlobalMapException(@NonNls String message) {
		super(message);
	}

	public GlobalMapException(String message, Throwable cause) {
		super(message, cause);
	}

	public GlobalMapException(Throwable cause) {
		super(cause);
	}

	public GlobalMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
