package com.evgenltd.mapper.core.exception;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 20-08-2016 22:59
 */
public class TrackerException extends RuntimeException {

	public TrackerException() {
	}

	public TrackerException(String message) {
		super(message);
	}

	public TrackerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TrackerException(Throwable cause) {
		super(cause);
	}

	public TrackerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
