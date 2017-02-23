package com.evgenltd.extractor;

import com.evgenltd.extractor.entity.CacheFile;
import com.evgenltd.extractor.entity.CacheFileBuilder;
import com.evgenltd.extractor.screen.GameCacheModel;
import haven.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 31-01-2017 01:43</p>
 */
public class HafenCache {

	private static final Logger log = LoggerFactory.getLogger(HafenCache.class);

	private File cacheFolder;

	public HafenCache() {
		try {
			cacheFolder = new File(Constants.CACHE_FOLDER_PATH);
			Config.resurl = new URL(Constants.RES_ID_URL);
			Resource.setcache(ResCache.global);
			removeLocalPool();
		}catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void removeLocalPool() {
		try {
			final Resource.Pool pool = Resource.remote();
			final Field parentField = pool.getClass().getDeclaredField("parent");
			parentField.setAccessible(true);
			parentField.set(pool, null);
		}catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isCacheFolderValid() {
		return  cacheFolder.exists() && cacheFolder.isDirectory() && cacheFolder.list() != null;
	}

	private boolean cacheFileFilter(@NotNull final File file) {
		return file.getName().matches(Constants.CACHE_FILE_NAME_PATTERN);
	}

	private String excludeResourceprefix(@NotNull final String resourceName) {
		if (!resourceName.startsWith(Constants.RESOURCE_PREFIX)) {
			return resourceName;
		}
		return resourceName.substring(Constants.RESOURCE_PREFIX.length());
	}

	@NotNull
	public List<CacheFile> loadCacheFileList() {
		return loadCacheFileList(cacheFile -> true);
	}

	@SuppressWarnings("ConstantConditions")
	@NotNull
	public List<CacheFile> loadCacheFileList(@NotNull final Predicate<CacheFile> cacheFileFilter) {

		if (!isCacheFolderValid()) {
			log.warn("Cache folder is invalid or does not exists");
			return Collections.emptyList();
		}

		return Arrays.stream(cacheFolder.listFiles(this::cacheFileFilter))
				.map(CacheFileBuilder::buildCacheFileHeader)
				.filter(cacheFileFilter)
				.sorted(Constants.CACHE_FILE_COMPARATOR)
				.collect(Collectors.toList());

	}

	@NotNull
	public Resource loadResource(@NotNull final String name) {
		final String cleanedName = excludeResourceprefix(name);
		if (!cleanedName.startsWith(Constants.GRAPHIC_RESOURCE_PREFIX)) {
			throw new IllegalArgumentException(String.format("[%s] is not graphic resource", name));
		}

		return Resource.remote().loadwait(cleanedName);
	}

	@NotNull
	public Image extractImage(@NotNull final Resource resource) {
		final Resource.Image image = resource.layer(Resource.Image.class);
		if (image == null) {
			throw new IllegalStateException("Resource does not have image layer");
		}

		final BufferedImage awtImage = image.img;
		return SwingFXUtils.toFXImage(awtImage, null);
	}

	public Map<Point2D, Long> loadMapIndex() {

		final MapFile mapFile = MapFile.load(ResCache.global);
		final Map<Point2D,Long> resultIndex = new HashMap<>();

		mapFile.lock.readLock().tryLock();

		try {
			for (final Long segmentId : mapFile.knownsegs) {

				final MapFile.Segment segment = mapFile.segments.get(segmentId);
				final Map<Coord,Long> index = getPrivateValue(segment, "map");

				index.forEach((coord,id) -> resultIndex.put(new Point2D(coord.x, coord.y), id));

			}

			return resultIndex;

		} finally {
			mapFile.lock.readLock().unlock();
		}

	}

	@Nullable
	public Image loadGrid(@NotNull final Long id) {
		final MapFile.Grid grid = MapFile.Grid.load(ResCache.global, id);
		if (grid == null) {
			return null;
		}
		final BufferedImage awtImage = grid.render(new Coord());
		return SwingFXUtils.toFXImage(awtImage,null);
	}

	// utils

	@SuppressWarnings("unchecked")
	private <T> T getPrivateValue(@NotNull final Object object, @NotNull final String fieldName) {

		try {
			final Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(object);
		}catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

}
