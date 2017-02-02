package com.evgenltd.extractor;

import com.evgenltd.extractor.entity.CacheFile;
import com.evgenltd.extractor.entity.CacheFileBuilder;
import com.evgenltd.extractor.screen.GameCacheModel;
import haven.Config;
import haven.ResCache;
import haven.Resource;
import javafx.embed.swing.SwingFXUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
}
