package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.LiteTile;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.TileImpl;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 21:43
 */
@ParametersAreNonnullByDefault
public class LayerRefreshing {

	private static final Logger log = LogManager.getLogger(LayerRefreshing.class);

	public static void refreshLayerFromFileSystem(
			final Layer layer,
			final boolean isOverwrite,
			final Function<TileInfo,Optional<Tile>> tileProvider,
			final Consumer<Tile> tilePersistent,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long,Long> progressUpdater
	)	{

		if(layer.getSessionPath() == null)	{
			return;	// todo add event log publish
		}

		LayerFileSystemIntegration.checkSessionPath(layer.getSessionPath());

		messageUpdater.accept("Loading first level tiles");

		final List<Tile> firstLevelTileList = uploadFirstLevelTiles(
				layer,
				isOverwrite,
				tileProvider,
				tilePersistent,
				progressUpdater
		);

		if(firstLevelTileList.isEmpty())	{
			return; // todo add event log publish
		}

		LayerLevelGeneration.execute(
				layer,
				firstLevelTileList,
				tileProvider,
				tilePersistent,
				messageUpdater,
				progressUpdater
		);

	}

	public static boolean isTile(final File file)	{
		return file.exists()
				&& file.isFile()
				&& file.getName().matches(Constants.TILE_NAME_PATTERN);
	}

	private static List<Tile> uploadFirstLevelTiles(
			final Layer layer,
			final boolean isOverwrite,
			final Function<TileInfo,Optional<Tile>> tileProvider,
			final Consumer<Tile> tilePersistent,
			final BiConsumer<Long,Long> progressUpdater
	)	{
		final File sessionFolder = new File(layer.getSessionPath());
		final List<Tile> result = new ArrayList<>();
		final File[] sessionFolderContent = sessionFolder.listFiles();

		if(sessionFolderContent == null || sessionFolderContent.length == 0)	{
			return Collections.emptyList();
		}

		long workDone = 1;
		long workMax = sessionFolderContent.length;

		for (final File tileFile : sessionFolderContent) {
			if(!isTile(tileFile))	{
				continue;
			}

			Utils.checkInterruption();

			try {

				readTile(
						tileFile,
						layer,
						isOverwrite,
						tileProvider,
						tilePersistent
				).ifPresent(result::add);

			}catch(Exception e) {
				log.error(String.format("Tile [%s] skipped due to error", tileFile.getName()),e);
			}

			progressUpdater.accept(workDone++, workMax);
		}

		return result;
	}

	//

	private static Optional<Tile> readTile(
			final File tileFile,
			final Layer layer,
			final boolean isOverwrite,
			final Function<TileInfo,Optional<Tile>> tileProvider,
			final Consumer<Tile> tilePersistent
	)	{

		final Optional<Point2D> pointHolder = Utils.parseTileName(tileFile.getName());
		if(!pointHolder.isPresent())	{
			return Optional.empty();
		}

		final Point2D point = pointHolder.get();

		final Optional<Tile> tileFromDataBase = readTileFromDataBase(point, layer, tileProvider);
		if(tileFromDataBase.isPresent() && !isOverwrite)	{
			return Optional.empty();
		}

		final Tile tile = tileFromDataBase.orElse(new TileImpl());

		final Image image = new Image("file:"+tileFile);
		final String hash = Utils.calculateHash(image);

		tile.setX(point.getX());
		tile.setY(point.getY());
		tile.setZ(ZLevel.MIN_LEVEL);
		tile.setLayer(layer);
		tile.setImage(image);
		tile.setHash(hash);

		tilePersistent.accept(tile);

		return Optional.of(LiteTile.fromTile(tile));

	}

	private static Optional<Tile> readTileFromDataBase(
			final Point2D point,
			final Layer layer,
			final Function<TileInfo,Optional<Tile>> tileProvider
	)	{
		final TileInfo tileInfo = TileInfo.of(
				point.getX(),
				point.getY(),
				ZLevel.MIN_LEVEL,
				layer
		);

		return tileProvider.apply(tileInfo);
	}
}
