package com.evgenltd.extractor.screen;

import com.evgenltd.extractor.Constants;
import com.evgenltd.extractor.Extractor;
import com.evgenltd.extractor.entity.CacheFile;
import com.evgenltd.extractor.entity.CacheFileBuilder;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 04:09</p>
 */
public class GameCacheModel {

	private File cacheFolder;
	private Task<List<CacheFile>> cachedFilesLoader;

	// lifecycle

	public void init() {
		cacheFolder = new File(System.getenv("APPDATA")
									   + File.separator + "Haven and Hearth"
									   + File.separator + "data");
	}

	public void destroy() {

	}

	//

	@SuppressWarnings("ConstantConditions")
	public void loadCachedFiles(@NotNull final Consumer<List<CacheFile>> resultCallback) {

		if (cachedFilesLoader != null) {
			cachedFilesLoader.cancel();
		}

		cachedFilesLoader = new Task<List<CacheFile>>() {
			@Override
			protected List<CacheFile> call() throws Exception {

				final boolean isSkip = !cacheFolder.exists() || !cacheFolder.isDirectory();
				if (isSkip) {
					return Collections.emptyList();
				}

				return Arrays.stream(cacheFolder.listFiles(GameCacheModel.this::cacheFileFilter))
						.map(CacheFileBuilder::buildCacheFileHeader)
//						.filter(cacheFile -> cacheFile.getName().startsWith("map"))
						.sorted(Constants.CACHE_FILE_COMPARATOR)
						.collect(Collectors.toList());

			}

			@Override
			protected void succeeded() {
				resultCallback.accept(getValue());
			}

			@Override
			protected void cancelled() {
				resultCallback.accept(Collections.emptyList());
			}

			@Override
			protected void failed() {
				getException().printStackTrace();
				resultCallback.accept(Collections.emptyList());
			}
		};
		Extractor.EXECUTOR_SERVICE.submit(cachedFilesLoader);

	}

	private boolean cacheFileFilter(@NotNull final File file) {
		return file.getName().matches(Constants.CACHE_FILE_NAME_PATTERN);
	}

}
