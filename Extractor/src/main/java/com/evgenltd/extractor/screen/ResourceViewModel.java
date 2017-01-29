package com.evgenltd.extractor.screen;

import com.evgenltd.extractor.Constants;
import com.evgenltd.extractor.Extractor;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import haven.Config;
import haven.Resource;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 28-01-2017 00:28</p>
 */
public class ResourceViewModel {

	private TreeItem<ResourceEntry> root;
	private final Map<String, Image> imageCache = new HashMap<>();
	private Task<Image> imageLoaderTask;

	public void init() {
		Config.cmdline(new String[] {"-U","http://game.havenandhearth.com/hres/"});
	}

	public void destroy() {}

	public TreeItem<ResourceEntry> loadResourceDescriptors() {
		final ResourceEntry resourceEntry = new ResourceEntry();
		resourceEntry.setName(Constants.RES_ROOT);
		resourceEntry.setPath(Constants.RES_ROOT);
		resourceEntry.setDirectory(true);
		root = new TreeItem<>(resourceEntry, new ImageView(Constants.FOLDER));

		try {
			final JarFile jarFile = new JarFile(Constants.HAFEN_RES);
			final Enumeration<JarEntry> enumeration = jarFile.entries();
			while (enumeration.hasMoreElements()) {
				final JarEntry jarEntry = enumeration.nextElement();
				jarEntryToTreeItem(jarEntry);
			}
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
		return root;
	}

	public void loadImageResource(@NotNull final ResourceEntry resourceEntry, @NotNull final Consumer<Image> resultCallback) {

		if (imageLoaderTask != null && !imageLoaderTask.isCancelled()) {
			imageLoaderTask.cancel();
		}

		imageLoaderTask = new Task<Image>() {
			@Override
			protected Image call() throws Exception {
				return loadImageResourceImpl(resourceEntry);
			}

			@Override
			protected void succeeded() {
				final Image image = getValue();
				imageCache.put(resourceEntry.getPath(), image);
				resultCallback.accept(image);
			}

			@Override
			protected void cancelled() {
				resultCallback.accept(null);
			}

			@Override
			protected void failed() {
				resultCallback.accept(null);
				getException().printStackTrace();
			}

		};
		Extractor.EXECUTOR_SERVICE.submit(imageLoaderTask);

	}

	private Image loadImageResourceImpl(@NotNull final ResourceEntry resourceEntry) {
		final String path = resourceEntry.getPath().replaceAll("\\.res", "");
		final Resource resource = Resource.remote().loadwait(path);
		final Resource.Image hafenImage = resource.layer(Resource.imgc);
		if (hafenImage == null) {
			return null;
		}
		final BufferedImage awtImage = hafenImage.img;

		return SwingFXUtils.toFXImage(awtImage, null);
	}

	private void jarEntryToTreeItem(@NotNull final JarEntry jarEntry) {

		final String path = jarEntry.getName();
		final String[] parts = path.split("/");

		if (!Objects.equals(parts[0], "res")) {
			return;
		}

		TreeItem<ResourceEntry> item = root;
		final StringBuilder partPathBuilder = new StringBuilder();

		for (final String part : parts) {
			if (part.equals("res") || part.length() == 0) {
				continue;
			}

			if (partPathBuilder.length() > 0) {
				partPathBuilder.append("/");
			}

			partPathBuilder.append(part);
			TreeItem<ResourceEntry> child = lookupChild(item, part);
			if (child == null) {
				final ResourceEntry resourceEntry = new ResourceEntry();
				resourceEntry.setName(part);
				resourceEntry.setPath(partPathBuilder.toString());
				resourceEntry.setDirectory(true);
				child = new TreeItem<>(resourceEntry, new ImageView(Constants.FOLDER));
				item.getChildren().add(child);
			}


			item = child;

		}

		item.getValue().setDirectory(false);
		item.setGraphic(new ImageView(Constants.FILE));

	}

	private TreeItem<ResourceEntry> lookupChild(@NotNull final TreeItem<ResourceEntry> parent, @NotNull final String name) {
		for (final TreeItem<ResourceEntry> child : parent.getChildren()) {
			if (Objects.equals(child.getValue().getName(), name)) {
				return child;
			}
		}

		return null;
	}

	private static class ImageLoaderService extends Service<Image> {

		private ResourceEntry resourceEntry;

		public void setResourceEntry(ResourceEntry resourceEntry) {
			this.resourceEntry = resourceEntry;
		}

		@Override
		protected Task<Image> createTask() {
			return new Task<Image>() {
				@Override
				protected Image call() throws Exception {

					final String path = resourceEntry.getPath().replaceAll("\\.res", "");
					final Resource resource = Resource.remote().loadwait(path);
					final Resource.Image hafenImage = resource.layer(Resource.imgc);
					if (hafenImage == null) {
						return null;
					}
					final BufferedImage awtImage = hafenImage.img;

					return SwingFXUtils.toFXImage(awtImage, null);

				}
			};
		}
	}
}
