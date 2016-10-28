package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.rule.LayerMatcher;
import com.evgenltd.mapper.core.util.Queries;
import math.geom2d.Point2D;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 25-07-2016 22:20
 */
@Component
@Transactional
@ParametersAreNonnullByDefault
public class LayerMatcherBean extends AbstractBean {

	public Optional<Layer> findMatches(final Layer targetLayer)	{

		return findMatches(Collections.singletonList(targetLayer));

	}

	public Optional<Layer> findMatchesById(final List<Long> targetLayerLongList)	{

		final List<Layer> targetLayerList = getEntityManager()
				.createQuery("from Layer where id in :layerIds", Layer.class)
				.setParameter("layerIds", targetLayerLongList)
				.getResultList();
		return findMatches(targetLayerList);

	}

	public Optional<Layer> findMatches(final List<Layer> targetLayerList)	{

		final List<Long> targetLayerIdList = targetLayerList
				.stream()
				.map(Layer::getId)
				.collect(Collectors.toList());
		final List<Layer> layersInDatabase = getEntityManager()
				.createQuery("from Layer where id not in :layerIds",Layer.class)
				.setParameter("layerIds",targetLayerIdList)
				.getResultList();

		final List<LayerMatcher.LayerGroup> matchResult = LayerMatcher.matchLayers(
				targetLayerList,
				layersInDatabase,
				true,
				this::matchLayers
		);

		if(matchResult.isEmpty())	{
			return Optional.empty();
		}

		matchResult
				.get(0)
				.getLayerGroup()
				.forEach(layer -> getEntityManager()
						.merge(layer));

		return matchResult
				.get(0)
				.getLayerGroup()
				.stream()
				.findAny();

	}

	private Optional<Point2D> matchLayers(final Layer first, final Layer second)	{

		final List matches = getEntityManager()
				.createNativeQuery(getQuery(Queries.LOAD_LAYER_MATCH_SQL))
				.setParameter("firstLayerId", first.getId())
				.setParameter("secondLayerId", second.getId())
				.getResultList();

		if(matches.size() != 1)	{
			return Optional.empty();
		}

		final Object[] pointRaw = (Object[])matches.get(0);
		return Optional.of(new Point2D(
				(Double)pointRaw[0],
				(Double)pointRaw[1]
		));

	}

}
