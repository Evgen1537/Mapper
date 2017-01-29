package com.evgenltd.extractor;

import com.evgenltd.extractor.screen.Main;
import haven.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 27-01-2017 23:56</p>
 */
public class Extractor extends Application {

	public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

	private Main main;

	public static void main(String[] args) {
//		launch(args);

		try {
			Config.resurl = new URL(Constants.RES_ID_URL);
		}catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		try {
			Resource.setcache(ResCache.global);
			final Resource.Pool pool = Resource.remote();
			final Field parentField = pool.getClass().getDeclaredField("parent");
			parentField.setAccessible(true);
			parentField.set(pool, null);
		}catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

//		final Resource.Pool pool = new Resource.Pool();
//		pool.add(new Resource.CacheSource(ResCache.global));
//
//		final Resource resource = pool.loadwait("gfx/tiles/beechgrove");
//		final Resource.Image image = resource.layer(Resource.Image.class);

//		final MapFile mapFile = MapFile.load(ResCache.global);
//
//		Resource.remote().loadwait("gfx/tiles/dryflat");
//		Resource.remote().loadwait("gfx/tiles/rootbosk");
//
//		final MapFile.Grid grid = MapFile.Grid.load(ResCache.global, 6220807755573809900L);
//		final BufferedImage image = grid.render(new Coord(23,45));

		final MapFile mapFile = MapFile.load(ResCache.global);
		for (final Long segmentId : mapFile.knownsegs) {
			mapFile.lock.readLock().tryLock();
			try {
				MapFile.Segment segment = mapFile.segments.get(segmentId);
				System.err.println("");
			} finally {
				mapFile.lock.readLock().unlock();
			}
			break;
		}


	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		main = new Main();
		main.init();

		final Scene scene = new Scene(main.getRoot(), 800, 600);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Extractor");
		primaryStage.show();

	}

	@Override
	public void stop() throws Exception {
		main.destroy();
		EXECUTOR_SERVICE.shutdownNow();
	}
}
