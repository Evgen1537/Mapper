package com.evgenltd.mapper.core.bean.envers;

import com.evgenltd.mapper.core.entity.envers.Aud;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.envers.RevisionType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 16-07-2016 01:03
 */
@ParametersAreNonnullByDefault
public class ChangePersistence {

	private static final Logger log = LogManager.getLogger(ChangePersistence.class.getName());

	private static final List<String> SKIPPED_FIELDS = Arrays.asList("rev","revType");
	private static final String ID_FIELD_NAME = "id";

	private static final String INSERT_QUERY_TEMPLATE = "insert into %s (%s) values (%s)";
	private static final String UPDATE_QUERY_TEMPLATE = "update %s set %s where %s = :%s";
	private static final String DELETE_QUERY_TEMPLATE = "delete from %s where %s = :%s";

	public static void persist(final EntityManager entityManager, final Aud change)	{

		final RevisionType revisionType = change.getRevType();
		switch(revisionType)	{
			case ADD:
				insert(entityManager, change.getTargetClass(), getFieldToValueMap(change));
				break;
			case MOD:
				update(entityManager, change.getTargetClass(), change.getId(), getFieldToValueMap(change));
				break;
			case DEL:
				delete(entityManager, change.getTargetClass(), change.getId());
				break;
		}

	}

	public static void persistStartSnapshot(final EntityManager entityManager, final Aud change)	{

		change.setRev(1L);
		change.setRevType(RevisionType.ADD);

		insert(entityManager, change.getClass(), getFieldToValueMap(change, false));

	}

	private static Map<String,Object> getFieldToValueMap(final Aud change)	{
		return getFieldToValueMap(change, true);
	}

	/**
	 * Build map with fields and field values based on {@link Aud} fields
	 * @param change Aud object to convert
	 * @return result map
	 */
	private static Map<String,Object> getFieldToValueMap(final Aud change, final boolean skipEnversField)	{

		final Field[] changeFieldArray = change.getClass().getDeclaredFields();
		final Map<String,Object> resultMap = new HashMap<>();

		for(final Field changeField : changeFieldArray) {

			final String fieldName = changeField.getName();
			final String columnName = resolveColumnName(changeField);

			if(skipEnversField && SKIPPED_FIELDS.contains(fieldName))	{
				continue;
			}

			changeField.setAccessible(true);
			Object fieldValue;

			try {
				fieldValue = changeField.get(change);
			}catch(IllegalAccessException e) {
				log.debug(String.format(
						"Unable to retrieve value, fieldName=[%s] rev=[%s] revType=[%s] id=[%s]",
						fieldName,
						change.getRev(),
						change.getRevType(),
						change.getId()
				));
				continue;
			}

			resultMap.put(columnName, valueToString(fieldValue));

		}

		return resultMap;

	}

	private static String valueToString(@Nullable final Object value)	{
		if(value == null)	{
			return null;
		}

		if(value instanceof Enum)	{
			return ((Enum)value).name();
		}else {
			return String.valueOf(value);
		}
	}

	// persistence

	private static void insert(
			final EntityManager entityManager,
			final Class<?> targetClass,
			final Map<String, Object> fieldMap
	) {

		final Query query = entityManager.createNativeQuery(buildInsertQuery(targetClass, fieldMap));

		fieldMap.forEach(query::setParameter);

		query.executeUpdate();

	}

	private static void update(
			final EntityManager entityManager,
			final Class<?> targetClass,
			final Long changeId,
			final Map<String, Object> fieldMap
	) {

		final Query query = entityManager.createNativeQuery(buildUpdateQuery(targetClass, fieldMap));

		fieldMap.remove(ID_FIELD_NAME);
		fieldMap.forEach(query::setParameter);

		query
				.setParameter(ID_FIELD_NAME,changeId)
				.executeUpdate();

	}

	private static void delete(
			final EntityManager entityManager,
			final Class<?> targetClass,
			final Long changeId
	)	{

		entityManager
				.createNativeQuery(buildDeleteQuery(targetClass))
				.setParameter(ID_FIELD_NAME,changeId)
				.executeUpdate();

	}

	// query builders

	private static String buildInsertQuery(
			final Class<?> targetClass,
			final Map<String, Object> fieldMap
	) {

		final StringBuilder queryValueNamesBuilder = new StringBuilder();
		final StringBuilder queryValuesBuilder = new StringBuilder();

		fieldMap.forEach((fieldName, fieldValue) -> {

			if(queryValueNamesBuilder.length() > 0)	{
				queryValueNamesBuilder.append(",");
				queryValuesBuilder.append(",");
			}

			queryValueNamesBuilder.append(fieldName);
			queryValuesBuilder.append(":").append(fieldName);

		});

		return String.format(
				INSERT_QUERY_TEMPLATE,
				resolveTableName(targetClass),
				queryValueNamesBuilder.toString(),
				queryValuesBuilder.toString()
		);
	}

	private static String buildUpdateQuery(
			final Class<?> targetClass,
			final Map<String, Object> fieldMap
	) {

		final StringBuilder queryParameterBuilder = new StringBuilder();

		fieldMap.forEach((fieldName, fieldValue) -> {

			if(Objects.equals(fieldName, ID_FIELD_NAME))	{
				return;
			}

			if(queryParameterBuilder.length() > 0)	{
				queryParameterBuilder.append(",");
			}

			queryParameterBuilder
					.append(fieldName)
					.append("=:")
					.append(fieldName);

		});

		return String.format(
				UPDATE_QUERY_TEMPLATE,
				resolveTableName(targetClass),
				queryParameterBuilder.toString(),
				ID_FIELD_NAME,
				ID_FIELD_NAME
		);

	}

	private static String buildDeleteQuery(final Class<?> targetClass)	{

		return String.format(
				DELETE_QUERY_TEMPLATE,
				resolveTableName(targetClass),
				ID_FIELD_NAME,
				ID_FIELD_NAME
		);

	}

	public static String resolveTableName(final Class<?> entityClass)	{
		for(Annotation annotation : entityClass.getAnnotations()) {
			try {
				final Class<? extends Annotation> annotationClass = annotation.annotationType();
				if(annotationClass.equals(Table.class)) {
					return (String)annotationClass
							.getDeclaredMethod("name")
							.invoke(annotation);
				}
			}catch(Exception e) {
				throw new RuntimeException(String.format("Unable to retrieve table name, entityClass=[%s]",entityClass));
			}
		}
		throw new IllegalArgumentException(String.format(
				"Entity class does not have @Table annotation, entityClass=[%s]",
				entityClass
		));
	}

	public static String resolveColumnName(final Field entityField)	{
		for(Annotation annotation : entityField.getAnnotations()) {
			try {
				final Class<? extends Annotation> annotationClass = annotation.annotationType();
				if(annotationClass.equals(Column.class)) {
					return (String)annotationClass
							.getDeclaredMethod("name")
							.invoke(annotation);
				}
			}catch(Exception e) {
				throw new RuntimeException(String.format(
						"Unable to retrieve column name, entityField=[%s]",
						entityField.getName()
				));
			}
		}
		return entityField.getName();
	}
}
