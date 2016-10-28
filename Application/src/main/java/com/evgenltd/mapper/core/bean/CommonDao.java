package com.evgenltd.mapper.core.bean;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:33
 */
@Component
@ParametersAreNonnullByDefault
public class CommonDao extends AbstractBean	{

	@Transactional
	public <T> void insert(final T entity)	{
		getEntityManager().persist(entity);
	}

	@Transactional
	public <T> T update(final T entity)	{
		return getEntityManager().merge(entity);
	}

	@Transactional
	public <T> void delete(final Class<T> entityClass, final Long id)	{
		final T attachedEntity = find(entityClass, id);
		getEntityManager().remove(attachedEntity);
	}

	@Transactional(readOnly = true)
	public <T> T find(final Class<T> entityClass, final Long id)	{
		return getEntityManager().find(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public <T> List<T> find(final String queryString, final Map<String,Object> parameters)	{
		final Query query = getEntityManager()
				.createQuery(queryString);
		parameters.forEach(query::setParameter);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List findNative(final String queryString, final Map<String,Object> parameters)	{
		final Query query = getEntityManager()
				.createNativeQuery(queryString);
		parameters.forEach(query::setParameter);
		return query.getResultList();
	}
}
