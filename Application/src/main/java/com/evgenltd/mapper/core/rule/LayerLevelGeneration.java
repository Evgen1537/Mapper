package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.entity.impl.LiteTile;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 10:07
 */
@ParametersAreNonnullByDefault
public class LayerLevelGeneration {

	public static void execute(
			final Layer layer,
			final List<? extends Tile> sourceTileList,
			final Function<TileInfo,Optional<Tile>> tileProvider,
			final UnaryOperator<Tile> tilePersistent,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long,Long> progressUpdater
	)	{

		Map<Point2D, Tile> currentIteration = mapTileList(sourceTileList);
		Map<Point2D, Tile> nextIteration = new HashMap<>(sourceTileList.size());

		Utils.checkInterruption();

		for(ZLevel currentLevel : ZLevel.values()) {

			if(currentLevel.equals(ZLevel.MAX_LEVEL))  {
				break;
			}

			Utils.checkInterruption();
			messageUpdater.accept(String.format("Level %s in progress", currentLevel));
			
			generateLevel(
					layer,
					currentLevel,
					currentIteration,
					nextIteration,
					tileProvider,
					tilePersistent,
					progressUpdater
			);
			
		}

	}

	private static void generateLevel(
			final Layer layer,
			final ZLevel currentLevel,
			final Map<Point2D, Tile> currentIteration,
			final Map<Point2D, Tile> nextIteration,
			final Function<TileInfo,Optional<Tile>> tileProvider,
			final UnaryOperator<Tile> tilePersistent,
			final BiConsumer<Long,Long> progressUpdater
	)	{

		long workDone = 1;

		for(final Point2D point : currentIteration.keySet()) {

			final Point2D nextLevelPoint = Utils.adjustPointToNextLevel(point, currentLevel);
			if(nextIteration.containsKey(nextLevelPoint))	{
				continue;
			}
			
			Utils.checkInterruption();
			
			final Map<Point2D,Tile> currentLevelTiles = getTilesForStitching(
					layer,
					currentLevel,
					nextLevelPoint,
					currentIteration,
					tileProvider
			);

			if(currentLevelTiles.isEmpty())	{
				continue;
			}

			final Tile stitchedTile = stitchTileImages(
					nextLevelPoint.getX(),
					nextLevelPoint.getY(),
					currentLevel,
					layer,
					currentLevelTiles,
					tileProvider
			);

			final Tile savedTile = tilePersistent.apply(stitchedTile);

			nextIteration.put(nextLevelPoint, LiteTile.fromTile(savedTile));

			progressUpdater.accept(workDone++, (long)currentIteration.size());

		}

		currentIteration.clear();
		currentIteration.putAll(nextIteration);
		nextIteration.clear();
		
	}

	private static Map<Point2D,Tile> getTilesForStitching(
			final Layer layer,
			final ZLevel currentLevel,
			final Point2D nextLevelPoint,
			final Map<Point2D, Tile> currentIteration,
			final Function<TileInfo,Optional<Tile>> tileProvider
	)	{

		final Map<Point2D,Tile> result = new HashMap<>();
		final List<Point2D> pointModifiers = Arrays.asList(
				new Point2D(0,0),
				new Point2D(1,0),
				new Point2D(0,1),
				new Point2D(1,1)
		);

		for(Point2D pointModifier : pointModifiers) {

			final Point2D deltaAdjustedToLevel = pointModifier.multiply(currentLevel.getMeasure());
			final Point2D pointWithModifier = nextLevelPoint.add(deltaAdjustedToLevel);
			final Tile currentLevelTile = currentIteration.get(pointWithModifier);

			if(currentLevelTile != null) {

				result.put(pointModifier, currentLevelTile);

			}else {

				tileProvider
						.apply(TileInfo.of(
								pointWithModifier.getX(),
								pointWithModifier.getY(),
								currentLevel,
								layer
						))
						.ifPresent(tile -> result.put(pointModifier, tile));

			}

		}

		return result;

	}

	private static Tile stitchTileImages(
			final double nextLevelPointX,
			final double nextLevelPointY,
			final ZLevel currentLevel,
			final Layer layer,
			final Map<Point2D,Tile> currentLevelTiles,
			final Function<TileInfo,Optional<Tile>> tileProvider
	)	{

		final Image stitchedTileImage = Utils.stitchImages(currentLevelTiles);
		final String stitchedTileHash = Utils.calculateHash(
				currentLevelTiles
						.values()
						.stream()
						.map(Tile::getHash)
						.collect(Collectors.toList())
		);

		final Tile stitchedTile = tileProvider
				.apply(TileInfo.of(
						nextLevelPointX,
						nextLevelPointY,
						currentLevel.nextLevel(),
						layer
				))
				.orElse(EntityFactory.createTile());

		stitchedTile.setX(nextLevelPointX);
		stitchedTile.setY(nextLevelPointY);
		stitchedTile.setZ(currentLevel.nextLevel());
		stitchedTile.setLayer(layer);
		stitchedTile.setImage(stitchedTileImage);
		stitchedTile.setHash(stitchedTileHash);

		return stitchedTile;

	}

	// utils

	private static Map<Point2D, Tile> mapTileList(final List<? extends Tile> tileList)	{
		return tileList
				.stream()
				.collect(Collectors.toMap(
						LayerLevelGeneration::getPosition,
						tile -> tile
				));
	}

	private static Point2D getPosition(final Tile tile)	{
		return new Point2D(tile.getX(), tile.getY());
	}

}
