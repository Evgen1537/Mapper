package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.enums.Visibility;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 23:21
 */
@ParametersAreNonnullByDefault
public class LayerFileSystemIntegration {

	private static final Logger log = LogManager.getLogger(LayerFileSystemIntegration.class);

	public static Layer addLayerFromSessionFolder(
			final File sessionFolder,
			final Supplier<Long> orderNumberGenerator,
			final Function<TileInfo, Optional<Tile>> tileProvider,
			final Consumer<Layer> layerPersistent,
			final UnaryOperator<Tile> tilePersistent,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	) {

		checkSessionPath(sessionFolder);

		final Layer layer = EntityFactory.createLayer();
		layer.setType(LayerType.SESSION);
		layer.setName(sessionFolder.getName());
		layer.setSessionPath(sessionFolder.getPath());
		layer.setOrderNumber(orderNumberGenerator.get());
		layer.setX(0D);
		layer.setY(0D);
		layer.setVisibility(Visibility.FULL);

		layerPersistent.accept(layer);

		LayerRefreshing.refreshLayerFromFileSystem(
				layer,
				true,
				tileProvider,
				tilePersistent,
				messageUpdater,
				progressUpdater
		);

		return layer;

	}

	public static List<Layer> addManyLayersFromSessionFolder(
			final File mapFolder,
			final Supplier<Long> orderNumberGenerator,
			final Function<TileInfo, Optional<Tile>> tileProvider,
			final Consumer<Layer> layerPersistent,
			final UnaryOperator<Tile> tilePersistent,
			final Consumer<String> titleUpdater,
			final Consumer<String> messageUpdater,
			final BiConsumer<Long, Long> progressUpdater
	) {

		checkSessionPath(mapFolder);

		final List<Layer> layerList = new ArrayList<>();
		int counter = 0;
		int total = mapFolder.list().length;
		for(String sessionPath : mapFolder.list()) {
			titleUpdater.accept(String.format("Adding many layers (%s/%s) ...", ++counter, total));
			final File sessionFolder = new File(mapFolder,sessionPath);
			try {
				final Layer layer = addLayerFromSessionFolder(
						sessionFolder,
						orderNumberGenerator,
						tileProvider,
						layerPersistent,
						tilePersistent,
						messageUpdater,
						progressUpdater
				);
				layerList.add(layer);
			}catch(Exception e) {
				log.info(String.format("Session skipped, path=[%s]",sessionFolder.getAbsoluteFile()),e);
			}
		}

		return layerList;

	}


	public static void checkSessionPath(final String sessionPath) {

		final File sessionFolder = new File(sessionPath);
		checkSessionPath(sessionFolder);

	}


	public static void checkSessionPath(final File sessionFolder) {

		if(!sessionFolder.exists()) {
			throw new IllegalArgumentException(String.format("Specified file does not exists, path=[%s]",
															 sessionFolder));
		}
		if(!sessionFolder.isDirectory()) {
			throw new IllegalArgumentException(String.format("Specified path is not a folder, path=[%s]",
															 sessionFolder));
		}
		if(sessionFolder.list().length == 0) {
			throw new IllegalArgumentException(String.format("Specified folder does not have files, path=[%s]",
															 sessionFolder));
		}

	}

	public static boolean checkSessionPathNoThrow(final File sessionFolder) {
		if(!sessionFolder.exists()) {
			return false;
		}
		if(!sessionFolder.isDirectory()) {
			return false;
		}
		if(sessionFolder.list().length == 0) {
			return false;
		}
		return true;
	}

	public static void exportLayerToFolder(
			final File destination,
			final Layer layer,
			final TileProvider tileProvider,
			final BiConsumer<Long, Long> progressUpdater
	)	{

		if(!destination.exists())	{
			if(!destination.mkdirs())	{
				throw new IllegalStateException(String.format(
						"Unable to create destination folder [%s]",
						destination.getAbsoluteFile()
				));
			}
		}

		final List<Long> tileIdList = tileProvider.loadTileIdList(layer.getId(), ZLevel.MIN_LEVEL);

		long workDone = 0;

		for(final Long tileId : tileIdList) {

			progressUpdater.accept(++workDone, (long)tileIdList.size());

			tileProvider
					.loadTile(tileId)
					.ifPresent(tile -> exportToFile(destination, tile));

		}

	}

	private static void exportToFile(final File folder, final Tile tile)	{

		final File destination = new File(folder, Utils.prepareTileName(tile));

		try(final FileOutputStream outputStream = new FileOutputStream(destination)) {

			outputStream.write(tile.getContent());

		}catch(IOException e) {
			log.warn("Unable to write tile");
		}

	}

}
