package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.entity.impl.LiteTile;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
			final UnaryOperator<Tile> tilePersistent,
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
			final UnaryOperator<Tile> tilePersistent,
			final BiConsumer<Long,Long> progressUpdater
	)	{
		final File sessionFolder = new File(layer.getSessionPath());
		final List<Tile> result = new ArrayList<>();
		final File[] sessionFolderContent = sessionFolder.listFiles();

		if(sessionFolderContent == null || sessionFolderContent.length == 0)	{
			return Collections.emptyList();
		}

		final Map<Point2D,Long> idsDescriptor = parseIdsDescriptor(sessionFolder);

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
						tilePersistent,
						idsDescriptor
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
			final UnaryOperator<Tile> tilePersistent,
			final Map<Point2D,Long> idsDescriptor
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

		final Tile tile = tileFromDataBase.orElse(EntityFactory.createTile());

		final Image image = new Image("file:"+tileFile);
		final String hash = Utils.calculateHash(image);
		final Long gridId = idsDescriptor.get(point);

		tile.setX(point.getX());
		tile.setY(point.getY());
		tile.setZ(ZLevel.MIN_LEVEL);
		tile.setLayer(layer);
		tile.setImage(image);
		tile.setHash(hash);
		tile.setGridId(gridId);

		final Tile savedTile = tilePersistent.apply(tile);

		return Optional.of(LiteTile.fromTile(savedTile));

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

	private static Map<Point2D, Long> parseIdsDescriptor(@NotNull final File sessionFolder) {

		final File idsDescriptor = new File(sessionFolder, Constants.IDS_DESCRIPTOR);
		if (!idsDescriptor.exists() || !idsDescriptor.isFile()) {
			return Collections.emptyMap();
		}

		try {

			final List<String> idsLines = Files.readAllLines(idsDescriptor.toPath());
			final Map<Point2D, Long> pointToIdMap = new HashMap<>();

			for (final String idsLine : idsLines) {

				try {
					final String[] parts = idsLine.split(",");
					final Integer x = Integer.parseInt(parts[0]);
					final Integer y = Integer.parseInt(parts[1]);
					final Long gridId = Long.parseLong(parts[2]);
					pointToIdMap.put(new Point2D(x,y), gridId);
				}catch (NumberFormatException e) {
					log.warn("Unable to parse ids entry [%s]", idsLine);
				}

			}

			return pointToIdMap;

		}catch (IOException e) {
			log.warn("Unable to read ids descriptor, path=[%s]", idsDescriptor.toString());
		}
		return Collections.emptyMap();

	}
}
