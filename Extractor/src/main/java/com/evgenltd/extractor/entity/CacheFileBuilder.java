package com.evgenltd.extractor.entity;

import com.evgenltd.extractor.Constants;
import com.evgenltd.extractor.util.RandomAccessFileInputStream;
import haven.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.function.BiConsumer;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 24-01-2017 23:35
 */
public class CacheFileBuilder {

	public static CacheFile buildWithDetails(@NotNull final CacheFile cacheFile) {

		final String name = cacheFile.getName();
		final File file = cacheFile.getFile();

		if (name.matches("map/index")) {

			return CacheFileBuilder.buildMapIndex(file);

		} else if (name.matches("map/grid-.*")) {

			return CacheFileBuilder.buildGrid(file);

		} else if (name.matches("map/seg-.*")) {

			return CacheFileBuilder.buildSegment(file);

		} else if (name.startsWith("res/")) {

			return CacheFileBuilder.buildRes(file);

		} else {
			return cacheFile;
		}

	}

	// cache file header

	public static CacheFile buildCacheFileHeader(@NotNull final File file) {

		return build(file, new CacheFile(), (message, cacheFile) -> {});

	}

	// map index

	public static MapIndexFile buildMapIndex(@NotNull final File file) {
		return build(file, new MapIndexFile(), CacheFileBuilder::buildMapIndexImpl);
	}

	private static void buildMapIndexImpl(@NotNull final StreamMessage message, @NotNull final MapIndexFile mapIndex) {

		mapIndex.setSegmentBlockVersion(message.uint8());
		checkVersion(mapIndex.getSegmentBlockVersion());

		// segments
		final int segmentsCount = message.int32();
		for (int segmentIndex = 0; segmentIndex < segmentsCount; segmentIndex++) {
			mapIndex.getSegmentIdList().add(message.int64());
		}

		// markers
		final int markersCount = message.int32();
		for (int markerIndex = 0; markerIndex < markersCount; markerIndex++) {
			mapIndex.getHafenMarkerList().add(buildHafenMarker(message));
		}

	}

	private static MarkerFile buildHafenMarker(@NotNull final StreamMessage message) {

		final MarkerFile marker = new MarkerFile();
		marker.setVersion(message.uint8());
		checkVersion(marker.getVersion());

		marker.setSegmentId(message.int64());
		marker.setCoordinates(message.coord());
		marker.setName(message.string());
		final char type = (char) message.uint8();
		marker.setType(MarkerFile.Type.fromCode(type));

		switch (marker.getType()) {
			case PLAYER_DEFINED:
				marker.setColor(message.color());
				break;
			case SERVER_DEFINED:
				marker.setObjectId(message.int64());
				marker.setResourcePath(message.string());
				marker.setResourceVersion(message.uint16());
				break;
			default:
				throw new IllegalArgumentException(String.format("Unsupported marker type [%s]", marker.getType()));
		}
		return marker;

	}

	// segment

	public static SegmentFile buildSegment(@NotNull final File file) {

		return build(file, new SegmentFile(), CacheFileBuilder::buildSegmentImpl);

	}

	private static void buildSegmentImpl(@NotNull final StreamMessage message, @NotNull final SegmentFile segment) {

		segment.setSegmentBlockVersion(message.uint8());
		checkVersion(segment.getSegmentBlockVersion());

		final ZMessage zMessage = new ZMessage(message);
		segment.setId(zMessage.int64());

		final int mapCount = zMessage.int32();
		for (int mapIndex = 0; mapIndex < mapCount; mapIndex++) {
			segment.getCoordinatesToId().put(zMessage.coord(), zMessage.int64());
		}

	}


	// map grid info

	public static void buildMapGridInfo(@NotNull final File file) {

	}

	// map grid

	public static MapGridFile buildGrid(@NotNull final File file) {

		return build(file, new MapGridFile(), CacheFileBuilder::buildGridImpl);

	}

	private static void buildGridImpl(@NotNull final StreamMessage message, @NotNull final MapGridFile grid) {

		grid.setGridBlockVersion(message.uint8());
		checkVersion(grid.getGridBlockVersion());

		final ZMessage zMessage = new ZMessage(message);
		grid.setId(zMessage.int64());

		final int tileCount = zMessage.uint8();
		for (int tileIndex = 0; tileIndex < tileCount; tileIndex++) {
			grid.getTileSet().add(buildTileInfo(zMessage));
		}

		grid.setContent(zMessage.bytes(Constants.TILE_SIZE * Constants.TILE_SIZE));

	}

	private static MapGridFile.TileInfo buildTileInfo(@NotNull final ZMessage zMessage) {
		final MapGridFile.TileInfo tileInfo = new MapGridFile.TileInfo();
		tileInfo.setResourcePath(zMessage.string());
		tileInfo.setResourceVersion(zMessage.uint16());
		tileInfo.setPrio(zMessage.uint8());
		return tileInfo;
	}

	// res

	public static CacheFile buildRes(@NotNull final File file) {
		return build(file, new ResFile(), CacheFileBuilder::buildResImpl);
	}

	private static void buildResImpl(@NotNull final StreamMessage message, @NotNull final ResFile resFile) {
		final byte[] sig = "Haven Resource 1".getBytes(Utils.ascii);
		final byte[] messageSig = message.bytes(sig.length);
		final int version = message.uint16();
		resFile.setResourceVersion(version);

		while (!message.eom()) {
			final String layer = message.string();
			final int length = message.int32();
			if (!layer.equals("image")) {
				message.skip(length);
				continue;
			}
			final Message buffer = new LimitMessage(message, length);
			final int z = buffer.int16();
			final int subz = buffer.int16();
			final int fl = buffer.uint8();
			final int id = buffer.int16();
			final int x = buffer.int16();
			final int y = buffer.int16();
			try {
				final BufferedImage awtImage = ImageIO.read(new MessageInputStream(buffer));
				final Image image = SwingFXUtils.toFXImage(awtImage, null);
				resFile.setImage(image);
			}catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	// utils

	private static <T extends CacheFile, M extends Message> T build(
			@NotNull final File file,
			@NotNull final T result,
			@NotNull final BiConsumer<StreamMessage, T> implementation
	) {

		try (final RandomAccessFile fileStream = new RandomAccessFile(file, "r");
			 final InputStream stream = new RandomAccessFileInputStream(fileStream);
			 final StreamMessage message = new StreamMessage(stream)
		) {

			final int version = fileStream.readByte();
			checkVersion(version);
			result.setFile(file);
			result.setVersion(version);
			result.setCacheId(fileStream.readUTF());
			result.setName(fileStream.readUTF());

			implementation.accept(message, result);

			return result;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static void checkVersion(final int version) {
		if (version != 1) {
			throw new IllegalStateException("Incorrect cache version");
		}
	}

}
