package com.evgenltd.mapper.core.entity;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 14-07-2016 00:21
 */
@Entity
@Table(name = "REVINFO")
@RevisionEntity
public class Revision extends DefaultRevisionEntity	{

	private Long executed = 1L;

	public Long getExecuted() {
		return executed;
	}

	public void setExecuted(Long executed) {
		this.executed = executed;
	}
}
