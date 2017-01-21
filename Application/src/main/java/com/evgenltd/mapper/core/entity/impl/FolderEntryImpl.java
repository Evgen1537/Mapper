package com.evgenltd.mapper.core.entity.impl;

import com.evgenltd.mapper.core.entity.FolderEntry;
import com.evgenltd.mapper.core.enums.FolderState;

import javax.persistence.*;
import java.io.File;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-08-2016 12:35
 */
@Entity(name = "FolderEntry")
@Table(name = "tracker_folders")
public class FolderEntryImpl implements FolderEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_path")
	private String sessionPath;

	@Transient
	private File sessionFolder;

	@Enumerated(EnumType.STRING)
	private FolderState state;

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getSessionPath() {
		return sessionPath;
	}
	@Override
	public void setSessionPath(String sessionPath) {
		this.sessionPath = sessionPath;
	}

	@Override
	public File getSessionFolder() {
		if(sessionFolder == null)	{
			sessionFolder = new File(sessionPath);
		}
		return sessionFolder;
	}

	@Override
	public FolderState getState() {
		return state;
	}
	@Override
	public void setState(FolderState state) {
		this.state = state;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		FolderEntryImpl that = (FolderEntryImpl)o;

		return sessionPath.equals(that.sessionPath);

	}

	@Override
	public int hashCode() {
		return sessionPath.hashCode();
	}
}
