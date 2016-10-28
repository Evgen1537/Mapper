package com.evgenltd.mapper.core.entity.envers;

import org.hibernate.envers.RevisionType;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 14-07-2016 10:48
 */
public interface Aud {

    Long getRev();

    void setRev(final Long rev);

    RevisionType getRevType();

    void setRevType(final RevisionType revType);

	Class<?> getTargetClass();

	Long getId();

	void setId(Long id);

}
