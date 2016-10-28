package com.evgenltd.mapper.core.util;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:31
 */
public class Queries {

	public static final String LOAD_LAYER_BY_VISIBILITY_HQL = "/sql/query/loadLayerByVisibility.hql";
	public static final String LOAD_LAYER_LIST_BY_TYPE_SQL = "/sql/query/loadLayerListByType.sql";
	public static final String LOAD_LAYER_LIST_BY_MAX_ORDER_NUMBER_HQL = "/sql/query/loadLayerListByMaxOrderNumber.hql";
	public static final String LOAD_LAYER_LIST_BY_MIN_ORDER_NUMBER_HQL = "/sql/query/loadLayerListByMinOrderNumber.hql";
	public static final String LOAD_LAYER_MATCH_SQL = "/sql/query/loadLayerMatch.sql";
	public static final String LOAD_LAYER_CENTROID_SQL = "/sql/query/loadLayerCentroid.sql";

	public static final String LOAD_TILE_POINTS_SQL = "/sql/query/loadTilePoints.sql";

	public static final String LOAD_TILE_LIST_BY_LAYER_AND_VIEWPORT_HQL = "/sql/query/loadTileListByLayerAndViewport.hql";
	public static final String LOAD_FIRST_LEVEL_TILE_LIST_HQL = "/sql/query/loadFirstLevelTileList.hql";
	public static final String LOAD_FIRST_LEVEL_LITE_TILE_LIST_HQL = "/sql/query/loadFirstLevelLiteTileList.hql";
	public static final String LOAD_TILE_BY_INFO_HQL = "/sql/query/loadTileByInfo.hql";
	public static final String LOAD_TILE_ID_LIST_HQL = "/sql/query/loadTileIdList.hql";
	public static final String LOAD_TILE_BY_ID_HQL = "/sql/query/loadTileById.hql";

	public static final String LOAD_MARKER_LIST_BY_VIEWPORT_HQL = "/sql/query/loadMarkerListByViewport.hql";
	public static final String LOAD_MARKER_LIST_BY_VIEWPORT_AND_QUERY_HQL = "/sql/query/loadMarkerListByViewportAndQuery.hql";
	public static final String LOAD_MARKER_LIST_BY_QUERY_HQL = "/sql/query/loadMarkerListByQuery.hql";
	public static final String LOAD_MARKER_LIST_BY_ID_HQL = "/sql/query/loadMarkerListById.hql";
	public static final String LOAD_LINKED_MARKER_LIST_BY_LAYER_ID_HQL = "/sql/query/loadLinkedMarkerListByLayerId.hql";
	public static final String CHECK_MARKER_CONTAINS_LINKED_LAYERS_SQL = "/sql/query/checkMarkerContainsLinkedLayers.sql";

	public static final String LOAD_MARKER_ICON_BY_NAME_HQL = "/sql/query/loadMarkerIconByName.hql";

	public static final String ENVERS_CLEANUP_CANCELLED_CHANGES_SQL = "/sql/query/envers/cleanupCancelledChanges.sql";
	public static final String ENVERS_CLEANUP_CHANGES_BY_REVISION_SQL = "/sql/query/envers/cleanupChangesByRevision.sql";
	public static final String ENVERS_CLEANUP_COMPLETE_SQL = "/sql/query/envers/cleanupComplete.sql";
	public static final String ENVERS_CHECK_FOR_CLEANUP_SQL = "/sql/query/envers/checkForCleanup.sql";

	public static final String MAINTENANCE_CLEANUP_DOUBLE_TILES_SQL = "/sql/query/maintenance/cleanupDoubleTiles.sql";
}
