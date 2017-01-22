package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Picture;
import com.evgenltd.mapper.core.entity.impl.PictureImpl;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 22-01-2017 00:29
 */
@Component
public class ImageCache extends AbstractBean {

	private static final int POOL_SIZE = 4;

	private final Map<Long, SoftReference<Picture>> imageCache = new ConcurrentHashMap<>();
	private Long threadCounter = 0L;
	private final ExecutorService imageCachePool = Executors.newFixedThreadPool(POOL_SIZE, r -> {
		final Thread thread = new Thread(r);
		thread.setName(String.format("ImageCachePool-%s", ++threadCounter));
		return thread;
	});
	private final AtomicInteger hitCount = new AtomicInteger(0);
	private final AtomicInteger loadCount = new AtomicInteger(0);

	@PreDestroy
	public void preDestroy() {
		imageCachePool.shutdownNow();
	}

	public void cleanCache() {
		imageCache.clear();
		hitCount.set(0);
		loadCount.set(0);
	}

	public Integer getCachedCount() {
		return imageCache.size();
	}

	public Integer getHitCount() {
		return hitCount.get();
	}

	public Integer getLoadCount() {
		return loadCount.get();
	}

	public Picture getImage(@NotNull final Long imageId) {

		final Picture image = getFromCache(imageId);
		if (image != null) {
			return image;
		}

		return loadPicture(imageId);

	}

	public void getImageAsync(@NotNull final Long imageId, @NotNull final Consumer<Picture> onCompleteCallback) {

		final Picture image = getFromCache(imageId);
		if (image != null) {
			onCompleteCallback.accept(image);
			return;
		}

		final Task<Picture> loadTask = new Task<Picture>() {
			@Override
			protected Picture call() throws Exception {
				return loadPicture(imageId);
			}

			@Override
			protected void succeeded() {
				onCompleteCallback.accept(getValue());
			}
		};
		imageCachePool.submit(loadTask);

	}

	private Picture loadPicture(@NotNull final Long imageId) {
		final Picture picture = getEntityManager().find(PictureImpl.class, imageId);
		imageCache.put(imageId, new SoftReference<>(picture));
		loadCount.incrementAndGet();
		return picture;
	}

	private Picture getFromCache(@NotNull final Long imageId) {

		final SoftReference<Picture> holder = imageCache.get(imageId);
		if (holder == null) {
			return null;
		}

		final Picture picture = holder.get();
		if (picture == null) {
			imageCache.remove(imageId);
			return null;
		}

		hitCount.incrementAndGet();
		return picture;

	}
}
