package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;
import com.evgenltd.mapper.core.rule.LayerMerge;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 07-07-2016 11:45
 */
@Component
@ParametersAreNonnullByDefault
public class CommonBean extends AbstractBean {

	@Autowired
	private SettingsBean settingsBean;
	@Autowired
	private LayerBean layerBean;
	@Autowired
	private TileBean tileBean;
	@Autowired
	private MarkerBean markerBean;
	@Autowired
	private Loader loader;

	@Transactional
	public void persistMovedState(
			final List<Long> layerIdList,
			final List<Long> markerIdList,
			final double deltaWorldX,
			final double deltaWorldY
	)	{

		moveLayers(layerIdList, deltaWorldX, deltaWorldY);
		moveMarkers(loader.loadMarkerList(markerIdList), deltaWorldX, deltaWorldY);

	}

	private void moveLayers(
			final List<Long> layerIdList,
			final double deltaWorldX,
			final double deltaWorldY
	)	{

		final double deltaTileWorldX = Utils.toTileSize(deltaWorldX);
		final double deltaTileWorldY = Utils.toTileSize(deltaWorldY);

		getEntityManager()
				.createQuery("from Layer where id in :layerIdList", Layer.class)
				.setParameter("layerIdList", layerIdList)
				.getResultList()
				.forEach(layer -> {
					layer.setX(layer.getX() + deltaTileWorldX);
					layer.setY(layer.getY() + deltaTileWorldY);
					getEntityManager().merge(layer);
				});

		moveMarkers(
				loader.loadLinkedMarkerList(layerIdList),
				deltaWorldX,
				deltaWorldY
		);

	}

	private void moveMarkers(
			final List<Marker> markerList,
			final double deltaWorldX,
			final double deltaWorldY
	)	{

		markerList.forEach(marker -> {
			marker.getMarkerPointList().forEach(markerPoint -> {
				markerPoint.setX(markerPoint.getX() + deltaWorldX);
				markerPoint.setY(markerPoint.getY() + deltaWorldY);
			});
			getEntityManager().merge(marker);
		});

	}

	@Transactional
	public void mergeTogether(
			final List<Long> sourceLayerIdList,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	)	{

		if(sourceLayerIdList.size() < 2)	{
			throw new IllegalArgumentException("need at least two layers");
		}

		final Layer targetLayer = new Layer();
		targetLayer.setName("Merge result "+ LocalDateTime.now());
		targetLayer.setOrderNumber(layerBean.getNewOrderNumber());
		targetLayer.setType(LayerType.SESSION);
		targetLayer.setVisibility(Visibility.FULL);
		targetLayer.setX(0D);
		targetLayer.setY(0D);
		getEntityManager().persist(targetLayer);

		merge(
				targetLayer.getId(),
				sourceLayerIdList,
				messageUpdater,
				progressUpdater
		);

	}

	@Transactional
	public void merge(
			final Long targetLayerId,
			final List<Long> sourceLayerIdList,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	)	{

		if(sourceLayerIdList.isEmpty())	{
			throw new IllegalArgumentException("Nothing to merge");
		}

		final boolean isOverwrite = settingsBean.isOverwriteTiles();
		final Layer targetLayer = findLayer(targetLayerId);
		final List<Layer> sourceLayerList = sourceLayerIdList
				.stream()
				.map(this::findLayer)
				.collect(Collectors.toList());

		LayerMerge.merge(
				targetLayer,
				sourceLayerList,
				isOverwrite,
				layer -> loader.loadTileList(layer.getId(), ZLevel.MIN_LEVEL),
				loader::loadTile,
				tileBean::update,
				messageUpdater,
				progressUpdater
		);

		markerBean.relinkMarkers(sourceLayerIdList, targetLayerId);

		layerBean.removeLayers(sourceLayerIdList, Optional.empty());

	}

	private Layer findLayer(final Long id)	{
		return getEntityManager().find(Layer.class, id);
	}
}
