package com.evgenltd.mapper.ui.component.globalmap;

import com.evgenltd.mapper.core.Context;
import com.evgenltd.mapper.core.bean.Loader;
import com.evgenltd.mapper.core.bean.GlobalMapBean;
import com.evgenltd.mapper.core.bean.SettingsBean;
import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.dto.GlobalMapResponse;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.exception.GlobalMapException;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import com.evgenltd.mapper.ui.util.UIConstants;
import com.evgenltd.mapper.ui.util.UIUtils;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import math.geom2d.Point2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 23-01-2017 02:16
 */
public class GlobalMapModel {

	private static final Logger log = LogManager.getLogger(GlobalMapModel.class);

	private static final String GENERIC_TITLE = "Global Map Integration";

	private Status integrationStatus = Status.UNLINKED;
	private String guid;
	private Consumer<Status> integrationStatusChangeCallback = status -> {};

	private SettingsBean settingsBean = Context.get().getSettingsBean();
	private GlobalMapBean globalMapBean = Context.get().getGlobalMapBean();
	private Loader loader = Context.get().getLoader();

	public GlobalMapModel() {}

	public void setIntegrationStatusChangeCallback(@NotNull final Consumer<Status> integrationStatusChangeCallback) {
		this.integrationStatusChangeCallback = integrationStatusChangeCallback;
	}

	public Status getIntegrationStatus() {
		return integrationStatus;
	}

	public void reLinkGlobalMap() {
		final Task<Void> linkingTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				updateTitle("Linking global map");
				doLinkGlobalMap();
				return null;
			}

			@Override
			protected void succeeded() {
				onLinkCompleted();
				Message.information()
						.title(GlobalMapModel.GENERIC_TITLE)
						.text("Linking cancelled")
						.publish();
			}

			@Override
			protected void cancelled() {
				onLinkFailed();
			}

			@Override
			protected void failed() {
				log.error(getException().getMessage(), getException());
				onLinkFailed();
				UIUtils.showAlert(
						Alert.AlertType.ERROR,
						GlobalMapModel.GENERIC_TITLE,
						getException().getMessage()
				);
			}
		};
		UIContext.get().submit(UIConstants.GLOBAL_MAP_INTEGRATION, linkingTask);
		integrationStatusChangeCallback.accept(Status.IN_PROGRESS);
	}

	public void unLinkGlobalMap() {
		settingsBean.setGlobalMapIntegrationEnabled(false);
		settingsBean.setGlobalMapOffsetX(0D);
		settingsBean.setGlobalMapOffsetY(0D);
		guid = null;
		integrationStatus = Status.UNLINKED;
	}

	public void stopLinkingGlobalMap() {
		final Task<?> linkingTask = UIContext.get().getTaskList().get(UIConstants.GLOBAL_MAP_INTEGRATION);
		if (linkingTask == null) {
			return;
		}

		linkingTask.cancel();
		unLinkGlobalMap();
		integrationStatusChangeCallback.accept(integrationStatus);
	}

	//

	private void doLinkGlobalMap() {

		unLinkGlobalMap();
		integrationStatus = Status.IN_PROGRESS;

		Utils.checkInterruption();

		final GlobalMapResponse<String> addSessionResponse = globalMapBean.addSession(String.valueOf(System.currentTimeMillis()));
		if (Objects.equals(addSessionResponse.getStatus(), GlobalMapResponse.Status.FAILED)) {
			throw new GlobalMapException("Adding session rejected - GUID is not received");
		}

		guid = addSessionResponse.getData();

		Utils.checkInterruption();

		final List<String> rejectedTiles = new ArrayList<>();
		final Optional<Layer> groundLayer = loader.loadLayerListByTypes(Collections.singleton(LayerType.GROUND)).stream().findFirst();
		if (!groundLayer.isPresent()) {
			throw new IllegalStateException("Ground layer not found");
		}

		final List<Tile> groundLayerFirstLevelTileList = loader.loadTileList(groundLayer.get().getId(), ZLevel.Z1);
		groundLayerFirstLevelTileList.stream().limit(1).forEach(tile -> {

			Utils.checkInterruption();

			final GlobalMapResponse<String> addTileResponse = globalMapBean.addTile(guid, tile);
			if (Objects.equals(addTileResponse.getStatus(), GlobalMapResponse.Status.FAILED)) {
				rejectedTiles.add(addSessionResponse.getData());
			}

		});

		if (!rejectedTiles.isEmpty()) {
			// todo publish list of rejected tiles
			Message
					.information()
					.title(GlobalMapModel.GENERIC_TITLE)
					.text("Some tiles are rejected")
					.publish();
		}

		Utils.checkInterruption();

		final GlobalMapResponse<Point2D> lockSessionResponse = globalMapBean.lockSession(guid);
		if (Objects.equals(lockSessionResponse.getStatus(), GlobalMapResponse.Status.FAILED)) {
			throw new GlobalMapException("No matches found. Try to expand your discovered map");
		}

		final Point2D globalOffset = lockSessionResponse.getData();
		settingsBean.setGlobalMapOffsetX(globalOffset.x());
		settingsBean.setGlobalMapOffsetY(globalOffset.y());
		settingsBean.setGlobalMapIntegrationEnabled(true);
		integrationStatus = Status.LINKED;

	}

	private void onLinkCompleted() {
		integrationStatusChangeCallback.accept(integrationStatus);
	}

	private void onLinkFailed() {
		unLinkGlobalMap();
		onLinkCompleted();
	}

	public enum Status {
		LINKED, IN_PROGRESS, UNLINKED
	}

}
