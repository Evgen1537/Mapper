package com.evgenltd.mapper.ui.node;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.mapviewer.common.Node;
import com.evgenltd.mapper.mapviewer.common.PaintContext;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.util.UIConstants;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 09-07-2016 19:07
 */
public class DebugInfoNode extends Node {

	private final long identifier = (long)(Math.random() * Long.MAX_VALUE);

	private final SettingsBean settingsBean = Context.get().getSettingsBean();

	public DebugInfoNode() {
		setOrderNumber(Long.MAX_VALUE);
	}

	@Override
	public long getIdentifier() {
		return identifier;
	}

	@Override
	protected void paint(PaintContext context) {

		if(!UIContext.get().isDebugMode())	{
			return;
		}

		context.getGraphicsContext().setFill(Color.RED);

		int lineHeight = 20;

		int row = lineHeight + UIConstants.RULER_THICKNESS;
		for(Map.Entry<String,String> entry : UIContext.get().getAllDebugInfo().entrySet()) {

			context.getGraphicsContext().fillText(
					String.format("%s : %s", entry.getKey(), entry.getValue()),
					UIConstants.RULER_THICKNESS,
					row

			);
			row = row + lineHeight;

		}
	}

	@Override
	protected boolean setHighlighted(@NotNull Predicate<Node> changeCondition) {
		return false;
	}

	@Override
	protected boolean setSelected(@NotNull Predicate<Node> changeCondition) {
		return false;
	}

	@Override
	protected boolean setMoved(@NotNull Predicate<Node> changeCondition) {
		return false;
	}

	@Override
	protected void setBlocked(boolean blocked) {}

	@Override
	protected void setVisible(boolean visible) {}

	@Override
	protected void setEditing(boolean editing) {}


	@Override
	protected boolean intersect(double worldX, double worldY) {
		return false;
	}

	@Override
	protected boolean intersect(
			double worldX,
			double worldY,
			double worldWidth,
			double worldHeight
	) {
		return false;
	}

	@Override
	protected void move(double worldDeltaX, double worldDeltaY) {}

	@Override
	protected void merge(@NotNull Node source) {}
}
