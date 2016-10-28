package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.FolderEntry;
import com.evgenltd.mapper.core.enums.FolderState;
import com.evgenltd.mapper.core.exception.TrackerException;
import com.evgenltd.mapper.core.rule.LayerRefreshing;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.ui.component.eventlog.Message;
import math.geom2d.Point2D;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 20-08-2016 20:24
 */
@Component
@Transactional
@ParametersAreNonnullByDefault
public class TrackerBean extends AbstractBean {

	@Autowired
	@SuppressWarnings("unused")
	private LayerBean layerBean;

	@Autowired
	@SuppressWarnings("unused")
	private Loader loader;

	@Autowired
	@SuppressWarnings("unused")
	private SettingsBean settingsBean;

	private Long currentLayerId;
	private String currentLayerSessionPath;
	private Point2D currentLayerWorldPosition;
	private Point2D playerPosition;

	private Consumer<Boolean> enableStateListener = state -> {};
	private boolean trackedFolderInvalidated = false;
	private Consumer<List<FolderEntry>> trackedFolderChangingListener = folderEntryList -> {};

	@Scheduled(fixedDelay = Constants.TRACKER_REMINDER_DELAY)
	@SuppressWarnings("unused")
	public void remind() {

		if(!settingsBean.isEnableTracker())	{
			return;
		}

		try {
			doTrackerWork();
		}catch(TrackerException te)	{
			settingsBean.setEnableTracker(false);
			enableStateListener.accept(false);
			Message
					.warning()
					.text(te.getMessage())
					.publish();
		}catch(Exception e) {
			settingsBean.setEnableTracker(false);
			enableStateListener.accept(false);
			Message
					.error()
					.text(e.getMessage())
					.withException(e)
					.publish();
		}

	}

	private void doTrackerWork()	{

		trackedFolderInvalidated = false;

		scanMapFolder();	// todo load current layer info, rewrite  it
		syncInnerState();
		syncMapFolder();
		refreshLayer(currentLayerSessionPath);
		trackPlayerPosition();

		if(trackedFolderInvalidated) {
			trackedFolderChangingListener.accept(loadAllFolderEntry());
		}

	}

	private void syncInnerState()	{

		loadAllFolderEntry()
				.stream()
				.forEach(folderEntry -> {
					final boolean isLayerExists = isLayerExists(folderEntry.getSessionPath());
					if(folderEntry.getState().equals(FolderState.ACTUAL) && !isLayerExists) {
						deleteFolderEntryWithSessionFolder(folderEntry);
						trackedFolderInvalidated = true;
					}else if(folderEntry.getState().equals(FolderState.ACTUAL) && isLayerExists) {
						removeLayerIfPossible(folderEntry.getSessionFolder());
//					}else if(folderEntry.getState().equals(FolderState.DELETED) && !isLayerExists)	{
						// skip
					}else if(folderEntry.getState().equals(FolderState.DELETED) && isLayerExists)	{
						markFolderEntryAsActual(folderEntry);
						trackedFolderInvalidated = true;
					}
				});

		loadNewSessionFolderList().forEach((sessionPath) -> trackedFolderInvalidated = trackedFolderInvalidated | addFolderEntry(sessionPath));

	}

	private void syncMapFolder()	{

		scanMapFolder().stream().forEach(sessionFolder -> {

			final boolean sessionFolderAlreadyTracked = isFolderEntryExists(sessionFolder.getAbsolutePath());

			if(sessionFolderAlreadyTracked)	{
				return;
			}

			addOrDeleteLayerFromFileSystem(sessionFolder);

		});

	}

	private List<File> scanMapFolder() {
		final File mapFolder = settingsBean.getMapFolder();
		if(!Utils.checkDirectory(mapFolder))	{
			throw new TrackerException("Map folder does not specified or it is incorrect. Please check it in Settings -> Tracker");
		}

		final File[] mapFolderContent = mapFolder.listFiles();
		if(mapFolderContent == null) {
			return Collections.emptyList();
		}

		Arrays
				.stream(mapFolderContent)
				.filter(file -> file.getName().equals(Constants.TRACKER_CURRENT_SESSION_FILE))
				.findAny()
				.map(this::extractCurrentSessionPath)
				.ifPresent(file -> loadLayerWorldPositionBySessionPath(file.getAbsolutePath()));

		return Arrays
				.stream(mapFolderContent)
				.filter(File::isDirectory)
				.collect(Collectors.toList());
	}

	private void trackPlayerPosition()	{
		if(!settingsBean.isTrackPlayerPosition())	{
			return;
		}

		if(currentLayerWorldPosition == null)	{
			return;
		}

		final File playerPositionFile = settingsBean.getPlayerPositionFile();
		if(!Utils.checkFile(playerPositionFile))	{
			throw new TrackerException("Player position file does not specified or it is incorrect. Please check it in Settings -> Tracker");
		}

		try {
			final IntBuffer buffer = FileChannel
					.open(playerPositionFile.toPath())
					.map(FileChannel.MapMode.READ_ONLY, 0, 8)
					.asIntBuffer();

			final int relativePlayerX = buffer.get() / 10 + Constants.TILE_SIZE / 2;
			final int relativePlayerY = buffer.get() / 10 + Constants.TILE_SIZE / 2;

			playerPosition = new Point2D(
					relativePlayerX + currentLayerWorldPosition.x(),
					relativePlayerY + currentLayerWorldPosition.y()
			);

		}catch(IOException ignored) {
		}

	}

	public Point2D getWorldPlayerPosition()	{
		return playerPosition;
	}

	public boolean isLayerCurrent(final Long layerId) {
		return Objects.equals(layerId,currentLayerId);
	}

	public void setEnableStateListener(@Nullable final Consumer<Boolean> enableStateListener) {
		if(enableStateListener == null)	{
			this.enableStateListener = state -> {};
		}else {
			this.enableStateListener = enableStateListener;
		}
	}

	public void setTrackedFolderChangingListener(@Nullable final Consumer<List<FolderEntry>> trackedFolderChangingListener) {
		if(trackedFolderChangingListener == null) {
			this.trackedFolderChangingListener = folderEntryList -> {};
		}else {
			this.trackedFolderChangingListener = trackedFolderChangingListener;
		}
	}

	// layer crud

	private void addOrDeleteLayerFromFileSystem(final File sessionFolder)	{

		if(isPossibleToDeleteSessionFolder(sessionFolder))	{
			removeSessionFolder(sessionFolder);
			return;
		}

		if(!settingsBean.isAddNewLayers())	{
			return;
		}

		layerBean.addSessionLayerFromFileSystem(
				sessionFolder,
				TrackerBean::emptyMessageUpdater,
				TrackerBean::emptyProgressUpdater
		);
		trackedFolderInvalidated = trackedFolderInvalidated | addFolderEntry(sessionFolder.getAbsolutePath());

	}

	private boolean isPossibleToDeleteSessionFolder(final File sessionFolder)	{

		if(!settingsBean.isRemoveSmallSessionFolders())	{
			return false;
		}

		if(Objects.equals(sessionFolder.getAbsolutePath(), currentLayerSessionPath))	{
			return false;
		}

		final File[] sessionFolderContent = sessionFolder.listFiles();
		if(sessionFolderContent == null || sessionFolderContent.length == 0)	{
			return false;
		}

		final Long smallSessionFolderSize = settingsBean.getSmallSessionFolderSize();
		final Long actualTileCount = Arrays
				.stream(sessionFolderContent)
				.filter(LayerRefreshing::isTile)
				.count();

		return actualTileCount < smallSessionFolderSize;

	}

	private void refreshLayer(@Nullable final String sessionPath)	{

		if(sessionPath == null)	{
			return;
		}

		if(!settingsBean.isRefreshLayers())	{
			return;
		}

		getLayerIdBySessionPath(sessionPath)
				.ifPresent(layerId -> layerBean.refreshLayer(
						layerId,
						TrackerBean::emptyMessageUpdater,
						TrackerBean::emptyProgressUpdater
				));

	}

	private void removeLayerIfPossible(final File sessionFolder)	{
		if(!isPossibleToDeleteSessionFolder(sessionFolder))	{
			return;
		}
		getLayerIdBySessionPath(sessionFolder.getAbsolutePath())
				.ifPresent(layerId -> layerBean.removeLayers(
						Collections.singletonList(layerId),
						Optional.of(false)
				));
		removeSessionFolder(sessionFolder);
	}

	// folder entry crud

	public boolean addFolderEntry(final String sessionPath)	{
		if(isFolderEntryExists(sessionPath))	{
			return false;
		}
		final FolderEntry folderEntry = new FolderEntry();
		folderEntry.setSessionPath(sessionPath);
		folderEntry.setState(FolderState.ACTUAL);
		getEntityManager().persist(folderEntry);
		return true;
	}

	public void markFolderEntryAsActual(final FolderEntry folderEntry)	{
		folderEntry.setState(FolderState.ACTUAL);
		getEntityManager().merge(folderEntry);
	}

	public void markFolderEntryAsDeleted(final FolderEntry folderEntry)	{
		folderEntry.setState(FolderState.DELETED);
		getEntityManager().merge(folderEntry);
	}

	public void deleteFolderEntry(final FolderEntry folderEntry)	{
		getEntityManager().remove(getEntityManager().merge(folderEntry));
	}

	private void deleteFolderEntryWithSessionFolder(final FolderEntry folderEntry)	{
		markFolderEntryAsDeleted(folderEntry);
		if(settingsBean.isRemoveSessionFoldersFromDisk())	{
			removeSessionFolder(folderEntry.getSessionFolder());
		}
	}

	private void removeSessionFolder(final File sessionFolder)	{
		if(!Utils.checkDirectory(sessionFolder))	{
			return;
		}

		if(sessionFolder.list().length > 0){
			for(final String childPath : sessionFolder.list()) {
				final File childFile = new File(sessionFolder, childPath);
				if(!childFile.delete())	{
					throw new TrackerException(String.format("Unable to delete folder %s",sessionFolder));
				}
			}
		}
		if(!sessionFolder.delete())	{
			throw new TrackerException(String.format("Unable to delete folder %s",sessionFolder));
		}
	}

	// loaders

	private boolean isLayerExists(final String sessionPath)	{
		return !getEntityManager()
				.createNativeQuery("select 1 from layers where session_path = :sessionPath")
				.setParameter("sessionPath", sessionPath)
				.getResultList()
				.isEmpty();
	}

	private boolean isFolderEntryExists(final String sessionPath)	{
		return !getEntityManager()
				.createNativeQuery("select 1 from tracker_folders where session_path = :sessionPath")
				.setParameter("sessionPath", sessionPath)
				.getResultList()
				.isEmpty();
	}

	public List<FolderEntry> loadAllFolderEntry()	{
		return getEntityManager()
				.createQuery("from FolderEntry", FolderEntry.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<String> loadNewSessionFolderList()	{

		return getEntityManager()
				.createNativeQuery("select l.session_path from layers l left join tracker_folders tf on l.session_path = tf.session_path where l.session_path is not null and tf.session_path is null")
				.getResultList();

	}

	@SuppressWarnings("unchecked")
	private Optional<Long> getLayerIdBySessionPath(final String sessionPath)	{

		return getEntityManager()
				.createNativeQuery("select id from layers where session_path = :sessionPath")
				.setParameter("sessionPath", sessionPath)
				.getResultList()
				.stream()
				.findAny();

	}

	@SuppressWarnings("unchecked")
	private void loadLayerWorldPositionBySessionPath(final String sessionPath) {
		currentLayerSessionPath = sessionPath;
		getEntityManager()
				.createNativeQuery("select id,x,y from layers where session_path = :sessionPath")
				.setParameter("sessionPath", sessionPath)
				.getResultList()
				.stream()
				.findAny()
				.ifPresent(o -> {
					final Object[] result = (Object[])o;
					currentLayerId = (long)result[0];
					final double x = (float)result[1];
					final double y = (float)result[2];
					currentLayerWorldPosition = new Point2D(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE);
				});
	}

	// utils

	private File extractCurrentSessionPath(final File currentSessionIdentifier)	{
		try {
			return Files
					.readAllLines(currentSessionIdentifier.toPath())
					.stream()
					.findFirst()
					.map(firstLine -> {
						final String currentSessionName = firstLine
								.replaceAll("var currentSession = '","")
								.replaceAll("';","")
								.trim();
						final File currentSessionFolder = new File(currentSessionIdentifier.getParent(), currentSessionName);
						if(currentSessionFolder.exists() && currentSessionFolder.isDirectory())	{
							return currentSessionFolder;
						}else {
							return null;
						}
					})
					.orElse(null);
		}catch(IOException e) {
			// todo add log
			return null;
		}

	}

	private static void emptyMessageUpdater(final String message)	{}

	private static void emptyProgressUpdater(final Long workDone, final Long workMax)	{}
}
