package com.evgenltd.mapper.core.enums;

import java.util.Objects;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:18
 */
public enum Visibility {
	FULL("Full"),PARTLY("Partly"),NONE("None");

	private String label;

	Visibility(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean isFull() {
		return Objects.equals(this, FULL);
	}

	public boolean isPartly()   {
		return Objects.equals(this, PARTLY);
	}

	public boolean isNone() {
		return Objects.equals(this, NONE);
	}
}
