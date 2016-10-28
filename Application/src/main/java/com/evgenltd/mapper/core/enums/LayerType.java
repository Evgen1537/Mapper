package com.evgenltd.mapper.core.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:15
 */
public enum LayerType {
	GROUND("Ground"), CAVE("Cave"), SESSION("Session");

	private String label;

	LayerType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean isGlobal() {
		return Objects.equals(this, GROUND)
				|| Objects.equals(this, CAVE);
	}

	public boolean isGround() {
		return Objects.equals(this, GROUND);
	}

	public boolean isCave() {
		return Objects.equals(this, CAVE);
	}

	public boolean isSession() {
		return Objects.equals(this, SESSION);
	}

	public static Collection<LayerType> sessionType()	{
		return Collections.singleton(SESSION);
	}

	public static Collection<LayerType> globalTypes()	{
		return Arrays.asList(GROUND, CAVE);
	}
}
