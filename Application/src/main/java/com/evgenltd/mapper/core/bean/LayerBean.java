package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;
import com.evgenltd.mapper.core.rule.*;
import com.evgenltd.mapper.core.util.Queries;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 11:19
 */
@Component
@ParametersAreNonnullByDefault
public class LayerBean extends AbstractBean {

	@Autowired
	@SuppressWarnings("unused")
	private Loader loader;
	@Autowired
	@SuppressWarnings("unused")
	private TileBean tileBean;
	@Autowired
	@SuppressWarnings("unused")
	private MarkerBean markerBean;
	@Autowired
	@SuppressWarnings("unused")
	private LayerMatcherBean layerMatcherBean;

	private Long orderNumberIdentity = null;

	@Transactional(readOnly = true)
	public Long getNewOrderNumber()	{
		if(orderNumberIdentity == null) {
			orderNumberIdentity = (Long)getEntityManager()
					.createNativeQuery("select max(order_number) order_number from layers")
					.getSingleResult();
		}
		return ++orderNumberIdentity;
	}

	@Transactional
	public void generateLevels(
			final Long layerId,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long,Long> progressUpdater
	)	{

		final List<? extends Tile> sourceTileList = loader.loadLiteTileList(layerId, ZLevel.MIN_LEVEL);
		generateLevels(layerId, sourceTileList, messageUpdater, progressUpdater);

	}

	@Transactional
	public void generateLevels(
			final Long layerId,
			final List<? extends Tile> sourceTileList,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long,Long> progressUpdater
	)	{

		final Layer layer = getEntityManager().find(Layer.class, layerId);
		LayerLevelGeneration.execute(
				layer,
				sourceTileList,
				loader::loadTile,
				tileBean::update,
				messageUpdater,
				progressUpdater
		);

	}

	@Transactional
	public void refreshLayer(
			final Long layerId,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	) {

		final Layer layer = getEntityManager().find(Layer.class, layerId);
		LayerRefreshing.refreshLayerFromFileSystem(
				layer,
				true,
				loader::loadTile,
				tileBean::update,
				messageUpdater,
				progressUpdater
		);

	}

	@Transactional
	public void addSessionLayerFromFileSystem(
			final File sessionFolder,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	)	{

		final Layer result = LayerFileSystemIntegration.addLayerFromSessionFolder(
				sessionFolder,
				this::getNewOrderNumber,
				loader::loadTile,
				layer -> getEntityManager().persist(layer),
				tileBean::update,
				messageUpdater,
				progressUpdater
		);

		layerMatcherBean.findMatches(result);

	}

	@Transactional
	public void addManyLayersFromSessionFolder(
			final File mapFolder,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	)	{

		LayerFileSystemIntegration.addManyLayersFromSessionFolder(
				mapFolder,
				this::getNewOrderNumber,
				loader::loadTile,
				layer -> getEntityManager().persist(layer),
				tileBean::update,
				messageUpdater,
				progressUpdater
		);

	}

	@Transactional
	public void bringLayersForward(final List<Long> layerIdList, final int stepCount, final Collection<LayerType> layerTypes)	{

		if(layerIdList.isEmpty())	{
			throw new IllegalArgumentException("Passed layers collection is empty");
		}

		final List<Layer> targetLayerList = layerIdList
				.stream()
				.map(layerId -> getEntityManager().find(Layer.class, layerId))
				.collect(Collectors.toList());

		final Long maxOrderNumber = targetLayerList
				.stream()
				.map(Layer::getOrderNumber)
				.max(Long::compare)
				.get();

		final List<Layer> affectedLayerList = getEntityManager()
				.createQuery(getQuery(Queries.LOAD_LAYER_LIST_BY_MAX_ORDER_NUMBER_HQL), Layer.class)
				.setParameter("maxOrderNumber",maxOrderNumber)
				.setParameter("layerTypes", layerTypes)
				.setMaxResults(stepCount)
				.getResultList();

		Reordering.bringForward(targetLayerList,affectedLayerList);

		targetLayerList.forEach(layer -> getEntityManager().merge(layer));
		affectedLayerList.forEach(layer -> getEntityManager().merge(layer));

	}

	@Transactional
	public void sendLayersBackward(final List<Long> layerIdList, final int stepCount, final Collection<LayerType> layerTypes)	{

		if(layerIdList.isEmpty())	{
			throw new IllegalArgumentException("Passed layers collection is empty");
		}

		final List<Layer> targetLayerList = layerIdList
				.stream()
				.map(layerId -> getEntityManager().find(Layer.class, layerId))
				.collect(Collectors.toList());

		final Long minOrderNumber = targetLayerList
				.stream()
				.map(Layer::getOrderNumber)
				.min(Long::compare)
				.get();

		final List<Layer> affectedLayerList = getEntityManager()
				.createQuery(getQuery(Queries.LOAD_LAYER_LIST_BY_MIN_ORDER_NUMBER_HQL), Layer.class)
				.setParameter("minOrderNumber",minOrderNumber)
				.setParameter("layerTypes", layerTypes)
				.setMaxResults(stepCount)
				.getResultList();

		Reordering.sendBackward(targetLayerList,affectedLayerList);

		targetLayerList.forEach(layer -> getEntityManager().merge(layer));
		affectedLayerList.forEach(layer -> getEntityManager().merge(layer));

	}

	@Transactional
	public void setLayerVisibility(
			final List<Long> layerIdList,
			final Visibility visibility
	)	{

		getEntityManager()
				.createQuery("from Layer where id in :layerIdList", Layer.class)
				.setParameter("layerIdList", layerIdList)
				.getResultList()
				.forEach(layer -> {
					layer.setVisibility(visibility);
					getEntityManager().merge(layer);
				});

	}

	@Transactional
	public void removeLayers(final List<Long> layerIdList, final Optional<Boolean> removeLinkedMarkers)	{

		removeLinkedMarkers.ifPresent(remove -> {
			if(remove)	{
				markerBean.removeLinkedMarkers(layerIdList);
			}else {
				markerBean.unlinkMarkers(layerIdList);
			}
		});

		getEntityManager()
				.createQuery("from Tile where layer.id in :layerIdList", Tile.class)
				.setParameter("layerIdList", layerIdList)
				.getResultList()
				.forEach(tile -> getEntityManager().remove(tile));

		getEntityManager()
				.createQuery("from Layer where id in :layerIdList", Layer.class)
				.setParameter("layerIdList", layerIdList)
				.getResultList()
				.forEach(layer -> getEntityManager().remove(layer));

	}

	@Transactional(readOnly = true)
	public void exportLayersToFileSystem(
			final File destination,
			final Long layerId,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	)	{

		final Layer layer = getEntityManager().find(Layer.class, layerId);
		final String layerFolderName = layer.getType().isCave()
				? String.format("%s (%s)", layer.getName(), layer.getOrderNumber())
				: layer.getName();

		messageUpdater.accept(String.format("Exporting %s...", layerFolderName));

		final File layerFolder = new File(destination, layerFolderName);

		LayerFileSystemIntegration.exportLayerToFolder(layerFolder, layer, (TileProvider)loader, progressUpdater);

	}

}
