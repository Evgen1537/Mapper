package com.evgenltd.mapper.core.entity;

import com.evgenltd.mapper.core.enums.FolderState;

import java.io.File;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 21-01-2017 21:53
 */
public interface FolderEntry extends Identified {

	String getSessionPath();
	void setSessionPath(String sessionPath);

	File getSessionFolder();

	FolderState getState();
	void setState(FolderState state);

}
