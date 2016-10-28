package com.evgenltd.mapper.ui.component.eventlog;

import java.util.Objects;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 18-06-2016 16:43
 */
public enum MessageType {
	WARNING,INFORMATION,ERROR;

	public boolean isInformation()	{
		return Objects.equals(this,INFORMATION);
	}

	public boolean isWarning()	{
		return Objects.equals(this,WARNING);
	}

	public boolean isError()	{
		return Objects.equals(this,ERROR);
	}
}
