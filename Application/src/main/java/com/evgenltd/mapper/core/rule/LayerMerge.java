package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.mapviewer.common.ZLevel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 07-07-2016 16:22
 */
@ParametersAreNonnullByDefault
public class LayerMerge {

    public static void merge(
            Layer targetLayer,
            final List<Layer> sourceLayerList,
            final boolean isOverwriteTile,
            final Function<Layer,List<Tile>> firstLevelTileListProvider,
            final Function<TileInfo,Optional<Tile>> tileProvider,
			final UnaryOperator<Tile> tilePersistent,
            final Consumer<String> messageUpdater,
            final BiConsumer<Long,Long> progressUpdater
    )  {

		messageUpdater.accept("Merging in progress...");

        List<Tile> targetLayerFirstLevelAffectedTiles = new ArrayList<>();

		final List<Tile> sourceLayersFirstLevelTiles = sourceLayerList
				.stream()
				.flatMap(layer -> firstLevelTileListProvider.apply(layer).stream())	// load lite tiles
				.collect(Collectors.toList());

		final long workMax = sourceLayersFirstLevelTiles.size();
		long workDone = 1;

		for(final Tile firstLevelTile : sourceLayersFirstLevelTiles) {

			progressUpdater.accept(workDone++,workMax);

			final TileInfo firstLevelTileInfo = TileInfo.of(
					firstLevelTile.getX() + firstLevelTile.getLayer().getX(),
					firstLevelTile.getY() + firstLevelTile.getLayer().getY(),
					ZLevel.MIN_LEVEL,
					targetLayer
			);

			final boolean isTileAlreadyExists = tileProvider
					.apply(firstLevelTileInfo)
					.isPresent();

			if(isTileAlreadyExists && !isOverwriteTile)	{
				continue;
			}

			addLayerOffset(firstLevelTile);
			replace(targetLayerFirstLevelAffectedTiles, firstLevelTile);
			firstLevelTile.setLayer(targetLayer);

		}

		final List<Tile> savedTargetLayerFistLevelAffectedTiles = targetLayerFirstLevelAffectedTiles.stream()
				.map(tilePersistent)
				.collect(Collectors.toList());

        LayerLevelGeneration.execute(
                targetLayer,
				savedTargetLayerFistLevelAffectedTiles,
                tileProvider,
				tilePersistent,
                messageUpdater,
                progressUpdater
        );

    }

	private static void addLayerOffset(final Tile targetTile)	{
		final Layer tileOwner = targetTile.getLayer();
		targetTile.setX(targetTile.getX() + tileOwner.getX());
		targetTile.setY(targetTile.getY() + tileOwner.getY());
	}

	private static void replace(List<Tile> tileList, Tile targetTile)	{
		tileList
				.stream()
				.filter(tile -> compareTiles(targetTile,tile))
				.findAny()
				.ifPresent(tileList::remove);
		tileList.add(targetTile);
	}

	private static boolean compareTiles(final Tile first, final Tile second)	{
		return Objects.equals(first.getX(), second.getX())
				&& Objects.equals(first.getY(), second.getY());
	}
}
