package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.*;
import com.evgenltd.mapper.core.entity.dto.LayerDto;
import com.evgenltd.mapper.core.entity.impl.LiteTile;
import com.evgenltd.mapper.core.entity.impl.PictureImpl;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.rule.TileInfo;
import com.evgenltd.mapper.core.rule.TileProvider;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Queries;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 02:01
 */
@Component
@ParametersAreNonnullByDefault
@Transactional(readOnly = true)
public class LoaderBean extends AbstractBean implements Loader,TileProvider	{

	@Autowired
	@SuppressWarnings("unused")
	private ImageCache imageCache;

	// layers

	@Override
	public List<Layer> loadByViewport(
			final Double x1,
			final Double y1,
			final Double x2,
			final Double y2,
			final ZLevel z
	)	{

		final List<Layer> layerList = getEntityManager()
				.createQuery(getQuery(Queries.LOAD_LAYER_BY_VISIBILITY_HQL), Layer.class)
				.getResultList();

		final Iterator<Layer> iterator = layerList.iterator();

		while(iterator.hasNext())	{

			final Layer layer = iterator.next();
			final List<Tile> tileSet = getEntityManager()
					.createQuery(
							getQuery(Queries.LOAD_TILE_LIST_BY_LAYER_AND_VIEWPORT_HQL),
							Tile.class
					)
					.setParameter("layerId",layer.getId())
					.setParameter("x1", x1)
					.setParameter("y1", y1)
					.setParameter("x2", x2)
					.setParameter("y2", y2)
					.setParameter("z", z)
					.getResultList();

			if(tileSet.isEmpty())	{
				iterator.remove();
				continue;
			}

			tileSet.forEach(tile -> {
				final List<Point2D> tilePoint = loadTilePoints(layer.getId(),tile);
				tile.setLowLevelTilePointList(tilePoint);
				final Picture image = tile.getImageEntity();
				imageCache.getImageAsync(Utils.getId(image), tile::setImageEntity);
			});

			layer.setTileSet(new HashSet<>(tileSet));

		}

		return layerList;

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<LayerDto> loadLayerDtoListByTypes(final LayerType... types)	{

		return getEntityManager()
				.createNativeQuery(getQuery(Queries.LOAD_LAYER_LIST_BY_TYPE_SQL))
				.setParameter(
						"types",
						Arrays
								.stream(types)
								.map(Enum::name)
								.collect(Collectors.toList())
				)
				.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.aliasToBean(LayerDto.class))
				.list();

	}

	@Override
	public List<Layer> loadAllLayerList()	{
		return getEntityManager()
				.createQuery("select l from Layer l", Layer.class)
				.getResultList();
	}

	@Override
	public List<LayerDto> loadSessionLayerList()	{
		return loadLayerDtoListByTypes(LayerType.SESSION);
	}

	@Override
	public List<LayerDto> loadGlobalLayerList()	{
		return loadLayerDtoListByTypes(LayerType.GROUND, LayerType.CAVE);
	}

	@Override
	public List<Layer> loadLayerListByTypes(final Collection<LayerType> layerTypes)	{

		return getEntityManager()
				.createQuery("from Layer where type in :layerTypes", Layer.class)
				.setParameter("layerTypes", layerTypes)
				.getResultList();

	}

	@Override
	@SuppressWarnings("unchecked")
	public math.geom2d.Point2D loadLayerCentroid(final Long layerId)	{

		return (math.geom2d.Point2D)getEntityManager()
				.createNativeQuery(getQuery(Queries.LOAD_LAYER_CENTROID_SQL))
				.setParameter("layerId", layerId)
				.getResultList()
				.stream()
				.findAny()
				.map(row -> Utils.toPoint((Object[])row))
				.orElse(Constants.ZERO_POINT);

	}

	// tiles

	@Override
	public List<Point2D> loadTilePoints(final Long layerId, final Tile tile)	{

		if(tile.getZ().isMinimumLevel())	{
			return Collections.singletonList(new Point2D(tile.getX(),tile.getY()));
		}

		return loadTilePoints(
				layerId,
				ZLevel.MIN_LEVEL,
				tile.getX(),
				tile.getY(),
				tile.getX() + tile.getZ().getMeasure(),
				tile.getY() + tile.getZ().getMeasure()
		);

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Point2D> loadTilePoints(
			final Long layerId,
			final ZLevel level,
			@Nullable final Double x1,
			@Nullable final Double y1,
			@Nullable final Double x2,
			@Nullable final Double y2
	)	{

		return (List)getEntityManager()
				.createNativeQuery(getQuery(Queries.LOAD_TILE_POINTS_SQL))
				.setParameter("layerId", layerId)
				.setParameter("z", level.name())
				.setParameter("x1", x1)
				.setParameter("y1", y1)
				.setParameter("x2", x2)
				.setParameter("y2", y2)
				.getResultList()
				.stream()
				.map(object -> {
					final Object[] row = (Object[])object;
					final double x = (Float)row[0];
					final double y = (Float)row[1];
					return new Point2D(x,y);
				})
				.collect(Collectors.toList());

	}

	@Override
	public Optional<Tile> loadTile(final TileInfo tileInfo)	{

		try {
			return getEntityManager()
					.createQuery(getQuery(Queries.LOAD_TILE_BY_INFO_HQL), Tile.class)
					.setParameter("x", tileInfo.getX())
					.setParameter("y", tileInfo.getY())
					.setParameter("z", tileInfo.getZ())
					.setParameter("layerId", tileInfo.getLayer().getId())
					.getResultList()
					.stream()
					.findAny();
		}catch(NoResultException e) {
			return Optional.empty();
		}

	}

	@Override
	public List<Tile> loadTileList(
			final Long layerId,
			final ZLevel level
	)	{

		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_FIRST_LEVEL_TILE_LIST_HQL), Tile.class)
				.setParameter("layerId", layerId)
				.setParameter("z", level)
				.getResultList();

	}

	@Override
	public List<? extends Tile> loadLiteTileList(
			final Long layerId,
			final ZLevel level
	)	{

		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_FIRST_LEVEL_LITE_TILE_LIST_HQL), LiteTile.class)
				.setParameter("layerId", layerId)
				.setParameter("z", level)
				.getResultList();

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> loadTileIdList(final Long layerId, final ZLevel level) {
		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_TILE_ID_LIST_HQL))
				.setParameter("layerId", layerId)
				.setParameter("z", level)
				.getResultList();
	}

	@Override
	public Optional<Tile> loadTile(Long tileId) {
		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_TILE_BY_ID_HQL), Tile.class)
				.setParameter("tileId",tileId)
				.getResultList()
				.stream()
				.findAny();
	}

	@Override
	public Picture loadImage(final Long imageId)	{
		return getEntityManager().find(PictureImpl.class, imageId);
	}

	// markers

	@Override
	public List<Marker> loadMarkerListByViewport(
			final Double x1,
			final Double y1,
			final Double x2,
			final Double y2
	)	{

		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_MARKER_LIST_BY_VIEWPORT_HQL), Marker.class)
				.setParameter("x1", x1)
				.setParameter("y1", y1)
				.setParameter("x2", x2)
				.setParameter("y2", y2)
				.getResultList();

	}

	@Override
	public List<Marker> loadMarkerListByQuery(final String query)	{
		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_MARKER_LIST_BY_QUERY_HQL), Marker.class)
				.setParameter("query", prepareQuery(query))
				.getResultList();
	}

	@Override
	public List<Marker> loadMarkerListByViewportAndQuery(
			final Double x1,
			final Double y1,
			final Double x2,
			final Double y2,
			final String query
	)	{

		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_MARKER_LIST_BY_VIEWPORT_AND_QUERY_HQL), Marker.class)
				.setParameter("x1", x1)
				.setParameter("y1", y1)
				.setParameter("x2", x2)
				.setParameter("y2", y2)
				.setParameter("query", prepareQuery(query))
				.getResultList();

	}

	private String prepareQuery(final String query)	{
		return "%"+query.toLowerCase()+"%";
	}

	@Override
	public List<Marker> loadMarkerList(final List<Long> markerIdList)	{

		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_MARKER_LIST_BY_ID_HQL), Marker.class)
				.setParameter("markerIdList", markerIdList)
				.getResultList();

	}

	@Override
	public List<Marker> loadLinkedMarkerList(final List<Long> layerIdList)	{

		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_LINKED_MARKER_LIST_BY_LAYER_ID_HQL), Marker.class)
				.setParameter("layerIdList", layerIdList)
				.getResultList();

	}

	@Override
	public boolean isLayersContainsLinkedMarkers(final List<Long> layerIdList)	{

		return !getEntityManager()
				.createNativeQuery(getQuery(Queries.CHECK_MARKER_CONTAINS_LINKED_LAYERS_SQL))
				.setParameter("layerIdList", layerIdList)
				.getResultList()
				.isEmpty();

	}

	// marker icon

	@Override
	public List<MarkerIcon> loadMarkerIconList()	{

		return getEntityManager()
				.createQuery("from MarkerIcon mi order by mi.name", MarkerIcon.class)
				.getResultList();

	}

	@Override
	public MarkerIcon loadDefaultMarkerIcon()	{

		return loadMarkerIcon(Constants.MARKER_ICON_NAME_NONE);

	}

	@Override
	public MarkerIcon loadMarkerIcon(final String name)	{

		return getEntityManager()
				.createQuery(getQuery(Queries.LOAD_MARKER_ICON_BY_NAME_HQL), MarkerIcon.class)
				.setParameter("name",name)
				.getSingleResult();

	}
}
