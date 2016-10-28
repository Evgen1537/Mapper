package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.mapviewer.common.ZLevel;

import java.util.List;
import java.util.Optional;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-08-2016 21:28
 */
public interface TileProvider {

	List<Long> loadTileIdList(final Long layerId, final ZLevel level);

	Optional<Tile> loadTile(final Long tileId);

}
