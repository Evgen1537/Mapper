package com.evgenltd.mapper.core.entity;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 14-07-2016 00:21
 */
@Entity
@Table(name = "REVINFO")
@RevisionEntity
public class Revision {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@RevisionNumber
	private int id;

	@RevisionTimestamp
	private long timestamp;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public Date getRevisionDate() {
		return new Date(this.timestamp);
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean equals(Object o) {
		if(this == o) {
			return true;
		} else if(!(o instanceof DefaultRevisionEntity)) {
			return false;
		} else {
			Revision that = (Revision)o;
			return this.id == that.id && this.timestamp == that.timestamp;
		}
	}

	public int hashCode() {
		int result = this.id;
		result = 31 * result + (int)(this.timestamp ^ this.timestamp >>> 32);
		return result;
	}

	public String toString() {
		return "DefaultRevisionEntity(id = " + this.id + ", revisionDate = " + DateFormat
				.getDateTimeInstance().format(this.getRevisionDate()) + ")";
	}

	private Long executed = 1L;

	public Long getExecuted() {
		return executed;
	}

	public void setExecuted(Long executed) {
		this.executed = executed;
	}
}
