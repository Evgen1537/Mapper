package com.evgenltd.mapper.core.entity;

import com.evgenltd.mapper.core.enums.FolderState;

import javax.persistence.*;
import java.io.File;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-08-2016 12:35
 */
@Entity
@Table(name = "tracker_folders")
public class FolderEntry {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "session_path")
	private String sessionPath;

	@Transient
	private File sessionFolder;

	@Enumerated(EnumType.STRING)
	private FolderState state;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSessionPath() {
		return sessionPath;
	}

	public void setSessionPath(String sessionPath) {
		this.sessionPath = sessionPath;
	}

	public File getSessionFolder() {
		if(sessionFolder == null)	{
			sessionFolder = new File(sessionPath);
		}
		return sessionFolder;
	}

	public FolderState getState() {
		return state;
	}

	public void setState(FolderState state) {
		this.state = state;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		FolderEntry that = (FolderEntry)o;

		return sessionPath.equals(that.sessionPath);

	}

	@Override
	public int hashCode() {
		return sessionPath.hashCode();
	}
}
