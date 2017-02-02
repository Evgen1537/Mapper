package com.evgenltd.extractor.screen;

import com.evgenltd.extractor.Constants;
import com.evgenltd.extractor.Extractor;
import com.evgenltd.extractor.HafenCache;
import com.evgenltd.extractor.entity.CacheFile;
import com.evgenltd.extractor.entity.CacheFileBuilder;
import haven.Resource;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
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

	private StringProperty quickSearch = new SimpleStringProperty();
	public String getQuickSearch() {
		return quickSearch.get();
	}
	public StringProperty quickSearchProperty() {
		return quickSearch;
	}
	public void setQuickSearch(String quickSearch) {
		this.quickSearch.set(quickSearch);
	}

	private ObservableList<CacheFile> dataList = FXCollections.observableArrayList();
	private FilteredList<CacheFile> filteredDataList = new FilteredList<>(dataList);
	public FilteredList<CacheFile> getFilteredDataList() {
		return filteredDataList;
	}

	private HafenCache hafenCache = new HafenCache();
	private Task<List<CacheFile>> cachedFilesLoader;

	// lifecycle

	public void init() {
		quickSearchProperty().addListener(observable -> filteredDataList.setPredicate(cacheFile -> cacheFile.getName().startsWith(getQuickSearch())));
	}

	public void destroy() {

	}

	//

	@SuppressWarnings("ConstantConditions")
	public void loadCachedFiles(@NotNull final Runnable onComplete) {

		if (cachedFilesLoader != null) {
			cachedFilesLoader.cancel();
		}

		cachedFilesLoader = new Task<List<CacheFile>>() {
			@Override
			protected List<CacheFile> call() throws Exception {
				return hafenCache.loadCacheFileList();
			}

			@Override
			protected void succeeded() {
				dataList.setAll(getValue());
				onComplete.run();
			}

			@Override
			protected void cancelled() {
				dataList.setAll(Collections.emptyList());
				onComplete.run();
			}

			@Override
			protected void failed() {
				// todo add alert
				getException().printStackTrace();
				dataList.setAll(Collections.emptyList());
				onComplete.run();
			}
		};
		Extractor.EXECUTOR_SERVICE.submit(cachedFilesLoader);

	}

	public Image loadImageResource(@NotNull final CacheFile cacheFile) {
		try {
			final String resourceName = cacheFile.getName();
			final Resource resource = hafenCache.loadResource(resourceName);
			return hafenCache.extractImage(resource);
		}catch (Exception e) {
			return null;
		}
	}

}
