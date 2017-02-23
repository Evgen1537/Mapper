package com.evgenltd.mapper.core.rule;

import com.evgenltd.extractor.HafenCache;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 06-02-2017 01:32</p>
 */
public class HafenCacheIntegration {

	private final HafenCache hafenCache = new HafenCache();
	private boolean overwrite;
	private Function<Long,Optional<Tile>> tileByGridIdProvider;
	private Function<TileInfo,Optional<Tile>> tileByInfoProvider;
	private UnaryOperator<Tile> tilePersistent;
	private Progress progress;

	private HafenCacheIntegration() {
	}

	public static HafenCacheIntegration create() {
		return new HafenCacheIntegration();
	}

	public HafenCacheIntegration setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
		return this;
	}

	public HafenCacheIntegration setTileByGridIdProvider(Function<Long, Optional<Tile>> tileByGridIdProvider) {
		this.tileByGridIdProvider = tileByGridIdProvider;
		return this;
	}

	public HafenCacheIntegration setTileByInfoProvider(Function<TileInfo, Optional<Tile>> tileByInfoProvider) {
		this.tileByInfoProvider = tileByInfoProvider;
		return this;
	}

	public HafenCacheIntegration setTilePersistent(UnaryOperator<Tile> tilePersistent) {
		this.tilePersistent = tilePersistent;
		return this;
	}

	public HafenCacheIntegration setProgress(Progress progress) {
		this.progress = progress;
		return this;
	}

	//

	public void refreshHafenCacheLayer(
			@NotNull final Layer layer
	) {

		final Map<Point2D,Long> index = hafenCache.loadMapIndex();
		final AtomicLong progressDone = new AtomicLong();
		final List<Tile> producedTileList = new ArrayList<>();

		index.forEach((position,gridId) -> {

			final Optional<Tile> tileFromDataBase = tileByGridIdProvider.apply(gridId);
			if (tileFromDataBase.isPresent() && !overwrite) {
				return;
			}

			final Image image = hafenCache.loadGrid(gridId);
			final Tile tile = tileFromDataBase.orElse(EntityFactory.createTile());
			tile.setGridId(gridId);
			tile.setX(position.getX());
			tile.setY(position.getY());
			tile.setZ(ZLevel.Z1);
			tile.setLayer(layer);
			tile.setHash(Utils.calculateHash(image));
			tile.setImage(image);
			final Tile savedTile = tilePersistent.apply(tile);

			producedTileList.add(savedTile);

			progress.updateProgress(progressDone.incrementAndGet(), (long)index.size());

		});

		LayerLevelGeneration.execute(
				layer,
				producedTileList,
				tileByInfoProvider,
				tilePersistent,
				progress::updateMessage,
				progress::updateProgress
		);

	}

}
