package com.evgenltd.mapper.core.entity.dto;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 22-01-2017 23:25
 */
public class GlobalMapResponse<T> {

	private Status status;
	private T data;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public enum Status {
		UNKNOWN(null),SUCCESS("success"),FAILED("failed");

		private String code;

		Status(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public static Status fromCode(@NotNull final String code) {
			for (final Status status : Status.values()) {
				if (Objects.equals(status.getCode(), code)) {
					return status;
				}
			}
			return UNKNOWN;
		}

	}

}
