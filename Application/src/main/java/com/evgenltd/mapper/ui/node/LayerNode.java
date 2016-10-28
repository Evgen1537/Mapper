package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.NodeGroup;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 13-06-2016 01:59
 */
public class LayerNode extends NodeGroup {

	private Layer layer;
	private ZLevel level;

	private final SettingsBean settingsBean = Context.get().getSettingsBean();

	public LayerNode(@NotNull final Layer layer, @NotNull final ZLevel level) {
		this.layer = layer;
		this.level = level;
		setOrderNumber(layer.getOrderNumber());
		for(final Tile tile : layer.getTileSet()) {
			add(new TileNode(this, tile, level));
		}
	}

	@Override
	public long getIdentifier() {
		return layer.getId();
	}

	public Layer getLayer() {
		return layer;
	}

	Double getX() {
		return layer.getX();
	}

	Double getY() {
		return layer.getY();
	}

	@Override
	public void paint(PaintContext context) {

		if(layer.getVisibility().isNone())  {
			return;
		}

		if(isSelected() || isHighlighted()) {

			Color color = Color.RED;
			if(isSelected()) {
				color = UIConstants.SELECTION_COLOR;
			}
			if(isHighlighted()) {
				color = UIConstants.HIGHLIGHT_COLOR;
			}

			for(Node node : getChildren()) {
				final TileNode tile = (TileNode)node;
				for(Point2D point : tile.getLowLevelTilePoints()) {
					context.rectFill(
							color,
							Utils.convertTileToPixel(point.getX() + getX()) - UIConstants.LAYER_OUTLINE_WIDTH * level.getMeasure(),
							Utils.convertTileToPixel(point.getY() + getY()) - UIConstants.LAYER_OUTLINE_WIDTH * level.getMeasure(),
							Constants.TILE_SIZE + 2 * UIConstants.LAYER_OUTLINE_WIDTH * level.getMeasure(),
							Constants.TILE_SIZE + 2 * UIConstants.LAYER_OUTLINE_WIDTH * level.getMeasure()
					);
				}
			}

			for(Node node : getChildren()) {
				final TileNode tile = (TileNode)node;
				for(Point2D point : tile.getLowLevelTilePoints()) {
					context.rectFill(
							Color.WHITE,
							Utils.convertTileToPixel(point.getX() + getX()),
							Utils.convertTileToPixel(point.getY() + getY()),
							Constants.TILE_SIZE,
							Constants.TILE_SIZE
					);
				}
			}
		}

		if(layer.getVisibility().isPartly())    {
			context.setAlpha(settingsBean.getPartlyVisibilityAlpha());
		}

		super.paint(context);

		context.setAlpha(1);

	}

	@Override
	protected boolean setMoved(@NotNull Predicate<Node> changeCondition) {
		return !layer.getType().isGlobal()
				&& super.setMoved(changeCondition);
	}

	@Override
	protected boolean setHighlighted(@NotNull Predicate<Node> changeCondition) {
		return !layer.getType().isGlobal()
				&& super.setHighlighted(changeCondition);
	}

	@Override
	protected boolean setSelected(@NotNull Predicate<Node> changeCondition) {
		return !layer.getType().isGlobal()
				&& super.setSelected(changeCondition);
	}

	@Override
	public void move(double worldDeltaX, double worldDeltaY) {
		layer.setX(layer.getX() + Utils.toTileSize(worldDeltaX));
		layer.setY(layer.getY() + Utils.toTileSize(worldDeltaY));
	}

	@Override
	public void merge(@NotNull Node source) {
		final LayerNode sourceLayerNode = (LayerNode)source;

		layer = sourceLayerNode.layer;
		level = sourceLayerNode.level;
		setOrderNumber(sourceLayerNode.getOrderNumber());

	}

	@Deprecated
	private String printDebug()	{
		final StringBuilder sb = new StringBuilder();

		sb.append(String.format("id=[%s] hashCode=[%s] x=[%s] y=[%s]\n", getIdentifier(), layer.toString(), getX(), getY()));

		for(Node node : getChildren()) {
			final TileNode tileNode = (TileNode)node;
			final LayerNode layerNode = tileNode.getParent();

			sb.append(String.format(
					"id=[%s]; parent : id=[%s] hashCode=[%s] x=[%s] y=[%s]\n",
					tileNode.getIdentifier(),
					layerNode.getIdentifier(),
					layerNode.getLayer(),
					layerNode.getX(),
					layerNode.getY()
			));

		}

		return sb.toString();
	}
}
