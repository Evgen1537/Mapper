package com.evgenltd.mapper.core.bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

/**
 * Project: Driver Pay
 * Author:  Evgeniy
 * Created: 14-05-2016 12:20
 */
@ParametersAreNonnullByDefault
public abstract class AbstractBean {

	private final Logger log = LogManager.getLogger(getClass());

	@PersistenceContext
	private EntityManager entityManager;

	protected Logger getLogger()	{
		return log;
	}

	protected EntityManager getEntityManager()	{
		return entityManager;
	}

	protected String getQuery(final String path)	{
		try {
			getLogger().debug(String.format("Load query [%s]", path));
			final InputStream stream = getClass().getResourceAsStream(path);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			final StringBuilder sb = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		}catch(final Exception e) {
			getLogger().error(e);
			throw new RuntimeException(e);
		}
	}

	protected void executeBatchUpdate(final String queryPath)	{

		executeBatchUpdate(queryPath, Collections.emptyMap());

	}

	protected void executeBatchUpdate(final String queryPath, final Map<String,Object> parameters)	{

		final String initQuery = getQuery(queryPath);
		final String[] queries = initQuery.split(";");

		for(final String queryString : queries) {

			final String trimmedQuery = queryString.trim();
			if(trimmedQuery.length() == 0)	{
				continue;
			}

			final Query query = getEntityManager().createNativeQuery(trimmedQuery);

			parameters.forEach(query::setParameter);

			query.executeUpdate();

		}

	}
}
