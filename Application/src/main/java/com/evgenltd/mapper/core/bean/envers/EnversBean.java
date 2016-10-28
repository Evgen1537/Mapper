	package com.evgenltd.mapper.core.bean.envers;

	import com.evgenltd.mapper.core.bean.AbstractBean;
	import com.evgenltd.mapper.core.entity.Layer;
	import com.evgenltd.mapper.core.entity.Marker;
	import com.evgenltd.mapper.core.entity.MarkerPoint;
	import com.evgenltd.mapper.core.entity.Tile;
	import com.evgenltd.mapper.core.entity.envers.*;
	import com.evgenltd.mapper.core.util.Queries;
	import org.hibernate.envers.RevisionType;
	import org.jetbrains.annotations.NotNull;
	import org.springframework.stereotype.Component;
	import org.springframework.transaction.annotation.Transactional;
	import org.springframework.transaction.support.TransactionSynchronizationManager;

	import java.util.*;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 12-07-2016 22:24
 */
@Component
@Transactional
public class EnversBean extends AbstractBean {

	private static final Comparator<Aud> CHANGE_COMPARATOR = new ChangeComparator();

	public void cleanupCancelledChanges()	{

		executeBatchUpdate(Queries.ENVERS_CLEANUP_CANCELLED_CHANGES_SQL);

	}

	public void cleanup()	{

		executeBatchUpdate(Queries.ENVERS_CLEANUP_COMPLETE_SQL);

		getEntityManager()
				.createQuery("from Layer", Layer.class)
				.getResultList()
				.stream()
				.map(ChangeFactory::build)
				.forEach(change -> ChangePersistence.persistStartSnapshot(getEntityManager(), change));

		getEntityManager()
				.createQuery("from Tile", Tile.class)
				.getResultList()
				.stream()
				.map(ChangeFactory::build)
				.forEach(change -> ChangePersistence.persistStartSnapshot(getEntityManager(), change));

		getEntityManager()
				.createQuery("from Marker", Marker.class)
				.getResultList()
				.stream()
				.map(ChangeFactory::build)
				.forEach(change -> ChangePersistence.persistStartSnapshot(getEntityManager(), change));

		getEntityManager()
				.createQuery("from MarkerPoint", MarkerPoint.class)
				.getResultList()
				.stream()
				.map(ChangeFactory::build)
				.forEach(change -> ChangePersistence.persistStartSnapshot(getEntityManager(), change));

	}

	@Deprecated
	public void cleanup(@NotNull final List<Long> revisionsForCleanup)	{
		revisionsForCleanup.forEach(this::cleanup);
	}

	@Deprecated
	private void cleanup(@NotNull final Long revision)  {

		executeBatchUpdate(Queries.ENVERS_CLEANUP_CHANGES_BY_REVISION_SQL, Collections.singletonMap("REV", revision));

	}


	public void beforeTransactionCommit()	{

		final boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		if(isReadOnly)	{
			return;
		}

		final String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		if(Objects.equals(transactionName, getClass().getName() + ".undo"))	{
			return;
		}

		if(getEntityManager()
				.createNativeQuery(getQuery(Queries.ENVERS_CHECK_FOR_CLEANUP_SQL))
				.getResultList()
				.isEmpty())	{
			return;
		}

		System.err.println(transactionName+" beforeTransactionCommit()");
		cleanupCancelledChanges();
		// Note: if transaction fails then cleanup will be cancelled and history will be in safety
	}

	public boolean isUndoAvailable()	{
		return !getEntityManager()
				.createNativeQuery("select distinct 1 from REVINFO where executed = 1")
				.getResultList()
				.isEmpty();
	}

	@Transactional
	public void undo()	{
		if(!isUndoAvailable())	{
			return;
			// throw exception if check failed
		}

		getLastAcceptedChangeRevisionNumber()
				.ifPresent(actualRevisionNumber -> {

					Arrays
							.asList(
									loadChangeList(LayerAud.class, actualRevisionNumber),
									loadChangeList(TileAud.class, actualRevisionNumber),
									loadChangeList(MarkerAud.class, actualRevisionNumber),
									loadChangeList(MarkerPointAud.class, actualRevisionNumber)
							)
							.stream()
							.flatMap(Collection::stream)
							.map(aud -> resolvePreviousEntityState(aud, actualRevisionNumber))
							.sorted(CHANGE_COMPARATOR)
							.forEach(aud -> ChangePersistence.persist(getEntityManager(), aud));

					getEntityManager()
							.createNativeQuery("update REVINFO set executed = 0 where id = :REV")
							.setParameter("REV", actualRevisionNumber)
							.executeUpdate();

				});

	}

	public boolean isRedoAvailable()	{
		return !getEntityManager()
				.createNativeQuery("select distinct 1 from REVINFO where executed = 0")
				.getResultList()
				.isEmpty();
	}

	@Transactional
	public void redo()	{

		if(!isRedoAvailable())	{
			return;
		}

		getFirstCancelledChangeRevisionNumber()
				.ifPresent(firstCancellationRevisionNumber -> {

					Arrays
							.asList(
									loadChangeList(LayerAud.class, firstCancellationRevisionNumber),
									loadChangeList(TileAud.class, firstCancellationRevisionNumber),
									loadChangeList(MarkerAud.class, firstCancellationRevisionNumber),
									loadChangeList(MarkerPointAud.class, firstCancellationRevisionNumber)
							)
							.stream()
							.flatMap(Collection::stream)
							.map(aud -> {
								getEntityManager().detach(aud);
								return aud;
							})
							.sorted(CHANGE_COMPARATOR)
							.forEach(aud -> ChangePersistence.persist(getEntityManager(), aud));

					getEntityManager()
							.createNativeQuery("update REVINFO set executed = 1 where id = :REV")
							.setParameter("REV", firstCancellationRevisionNumber)
							.executeUpdate();

				});

	}

	// extension

	public <T> List<T> loadChangeList(
			@NotNull final Class<T> entityClass,
			@NotNull final Long actualRevisionNumber
	)	{

		final String queryString = String.format("from %s where REV = :REV",entityClass.getSimpleName());
		return getEntityManager()
				.createQuery(queryString, entityClass)
				.setParameter("REV", actualRevisionNumber)
				.getResultList();

	}

	public Aud resolvePreviousEntityState(
			@NotNull final Aud change,
			@NotNull final Long actualRevisionNumber
	)	{

		getEntityManager().detach(change);

		final RevisionType revisionType = change.getRevType();
		Aud previousChange;

		switch(revisionType)	{
			case ADD:
				change.setRevType(RevisionType.DEL);
				return change;
			case DEL:
				previousChange = loadPreviousEntityState(
						change.getClass(),
						change.getId(),
						actualRevisionNumber
				);
				previousChange.setRevType(RevisionType.ADD);
				return previousChange;
			case MOD:
				previousChange = loadPreviousEntityState(
						change.getClass(),
						change.getId(),
						actualRevisionNumber
				);
				previousChange.setRevType(RevisionType.MOD);
				return previousChange;
			default:
				throw new IllegalStateException(String.format(
						"Unknown RevisionType=[%s] change id=[%s] change rev=[%s] change class=[%s]",
						revisionType,
						change.getId(),
						change.getRev(),
						change.getClass()
				));
		}

	}

	@SuppressWarnings("unchecked")
	public Optional<Long> getLastAcceptedChangeRevisionNumber()    {

		return getEntityManager()
				.createNativeQuery("select max(id) from REVINFO where executed = 1")
				.getResultList()
				.stream()
				.findFirst();

	}

	@SuppressWarnings("unchecked")
	public Optional<Long> getFirstCancelledChangeRevisionNumber()	{

		return getEntityManager()
				.createNativeQuery("select min(id) from REVINFO where executed = 0")
				.getResultList()
				.stream()
				.findFirst();

	}

	@SuppressWarnings("unchecked")
	public <T> T loadPreviousEntityState(
			@NotNull final Class<T> entityClass,
			@NotNull final Long id,
			@NotNull final Long actualRevisionNumber
	)	{

		final String tableName = ChangePersistence.resolveTableName(entityClass);
		String queryString = String.format(
				"select max(REV) from %s where id = :id and REV < :REV",
				tableName
		);
		final Long previousRevisionNumber = (Long)getEntityManager()
				.createNativeQuery(queryString)
				.setParameter("id", id)
				.setParameter("REV", actualRevisionNumber)
				.getResultList()
				.stream()
				.findFirst()
				.orElse(null);
		if(previousRevisionNumber == null)	{
			throw new IllegalStateException(String.format(
					"Previous revision number does not exists, class=[%s] id=[%s] actualRevisionNumber=[%s]",
					entityClass,
					id,
					actualRevisionNumber
			));
		}

		queryString = String.format(
				"from %s where id = :id and REV = :REV",
				entityClass.getSimpleName()
		);
		final T previousState = getEntityManager()
				.createQuery(queryString, entityClass)
				.setParameter("id", id)
				.setParameter("REV", previousRevisionNumber)
				.getResultList()
				.stream()
				.findFirst()
				.orElse(null);
		if(previousState == null)	{
			throw new IllegalStateException(String.format(
					"Previous state of specified entity does not exists, class=[%s] id=[%s] actualRevisionNumber=[%s]",
					entityClass,
					id,
					actualRevisionNumber
			));
		}

		getEntityManager().detach(previousState);

		return previousState;

	}

}
