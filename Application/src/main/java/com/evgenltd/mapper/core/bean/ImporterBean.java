package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.enums.LayerType;
import com.evgenltd.mapper.core.importer.LayerConverter;
import com.evgenltd.mapper.core.importer.LayerOld;
import com.evgenltd.mapper.core.importer.MarkerConverter;
import com.evgenltd.mapper.core.importer.MarkerOld;
import com.evgenltd.mapper.core.util.Constants;
import com.evgenltd.mapper.core.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 08-07-2016 00:42
 */
@Component
@ParametersAreNonnullByDefault
public class ImporterBean extends AbstractBean{
	private static final Logger log = LogManager.getLogger(ImporterBean.class);

	private static final String LAYER_DESCRIPTOR = "layer.xml";

	private static final String PROJECT_FOLDER = File.separator + "project";

	private static final String BASE_LAYER_FOLDER = PROJECT_FOLDER + File.separator + "base";
	private static final String BASE_LAYER_DESCRIPTOR = BASE_LAYER_FOLDER + File.separator + LAYER_DESCRIPTOR;

	private static final String CAVE_FOLDER = PROJECT_FOLDER + File.separator + "cave";
	private static final String CAVE1_LAYER_DESCRIPTOR = CAVE_FOLDER + File.separator + "level1" + File.separator + LAYER_DESCRIPTOR;
	private static final String CAVE2_LAYER_DESCRIPTOR = CAVE_FOLDER + File.separator + "level2" + File.separator + LAYER_DESCRIPTOR;
	private static final String CAVE3_LAYER_DESCRIPTOR = CAVE_FOLDER + File.separator + "level3" + File.separator + LAYER_DESCRIPTOR;
	private static final String CAVE4_LAYER_DESCRIPTOR = CAVE_FOLDER + File.separator + "level4" + File.separator + LAYER_DESCRIPTOR;

	private static final String MARKERS_DESCRIPTOR = File.separator + "markers.xml";

	@Autowired
	@SuppressWarnings("unused")
	private XStreamMarshaller xStreamMarshaller;
	@Autowired
	@SuppressWarnings("unused")
	private Loader loader;
	@Autowired
	@SuppressWarnings("unused")
	private LayerBean layerBean;

	private Consumer<String> messageConsumer = message -> {};
	private LayerConverter layerConverter;
	private MarkerConverter markerConverter;

	@PostConstruct
	@SuppressWarnings("unused")
	public void postConstruct()	{
		layerConverter = (LayerConverter)xStreamMarshaller
				.getXStream()
				.getConverterLookup()
				.lookupConverterForType(LayerOld.class);
		markerConverter = (MarkerConverter)xStreamMarshaller
				.getXStream()
				.getConverterLookup()
				.lookupConverterForType(MarkerOld.class);
	}

	public void setMessageConsumer(final Consumer<String> messageConsumer) {
		this.messageConsumer = messageConsumer;
	}

	private void logMessage(final String message)	{
		messageConsumer.accept(message);
	}

	private void logError(final Exception exception)	{
		messageConsumer.accept(exception.getMessage());
		log.error("An error has occurred during importing", exception);
	}

	@Transactional
	public void doImport(
			final File applicationPath,
			final boolean skipLayerImport,
			final boolean skipMarkerImport,
			final BiConsumer<Long,Long> progressUpdater
	) {

		if(!checkApplicationPath(applicationPath))	{
			messageConsumer.accept("Import process was interrupted");
			return;
		}

		final MarkerIcon markerIcon = loader.loadMarkerIcon(Constants.MARKER_ICON_NAME_NONE);
		if(markerIcon == null)	{
			throw new NullPointerException("None marker is not found");
		}
		layerConverter.setApplicationPath(applicationPath);
		markerConverter.setApplicationPath(applicationPath);
		markerConverter.setMarkerIconList(loader.loadMarkerIconList());
		markerConverter.setNoneMarkerIcon(markerIcon);

		if(!skipLayerImport) {
			Utils.checkInterruption();
			final File groundLayerDescriptor = getGroundLayerDescriptor(applicationPath);
			final Layer groundLayer = loader.loadLayerListByTypes(Collections.singleton(LayerType.GROUND)).get(0);
			logMessage("");
			logMessage("########################################");
			logMessage("Importing ground layer...");
			logMessage("########################################");
			doImportLayer(groundLayerDescriptor, groundLayer);

			Utils.checkInterruption();
			final List<File> caveLayerDescriptors = getCaveLayerDescriptors(applicationPath);
			final List<Layer> caveLayers = loader.loadLayerListByTypes(Collections.singleton(LayerType.CAVE));
			for(int i = 0; i < caveLayerDescriptors.size(); i++) {
				logMessage("");
				logMessage("########################################");
				logMessage("Importing cave layer...");
				logMessage("########################################");
				progressUpdater.accept((long)i,(long)caveLayerDescriptors.size());
				doImportLayer(caveLayerDescriptors.get(i), caveLayers.get(i));
			}

			final List<File> sessionLayerDescriptors = getSessionLayerDescriptors(applicationPath);
			long counter = 0;
			for(final File sessionLayerDescriptor : sessionLayerDescriptors) {
				Utils.checkInterruption();
				logMessage("");
				logMessage("########################################");
				logMessage("Importing session layer...");
				logMessage("########################################");
				progressUpdater.accept(++counter,(long)sessionLayerDescriptors.size());
				final Layer layer = EntityFactory.createLayer();
				layer.setOrderNumber(layerBean.getNewOrderNumber());
				doImportLayer(sessionLayerDescriptor, layer);
			}
		}

		if(!skipMarkerImport) {
			Utils.checkInterruption();
			final File markerDescriptor = getMarkerDescriptor(applicationPath);
			logMessage("");
			logMessage("########################################");
			logMessage("Importing marker...");
			logMessage("########################################");
			doImportMarker(markerDescriptor);
		}

	}

	public boolean checkApplicationPath(final File applicationPath)	{
		boolean result = false;

		final File groundDescriptor = getGroundLayerDescriptor(applicationPath);
		if(groundDescriptor.exists() && groundDescriptor.isFile())	{
			logMessage("Found ground layer");
			result = true;
		}

		final List<File> caveDescriptors = getCaveLayerDescriptors(applicationPath);
		for(final File caveDescriptor : caveDescriptors) {
			if(caveDescriptor.exists() && caveDescriptor.isFile())	{
				logMessage("Found cave layer");
				result = true;
			}
		}

		final List<File> sessionDescriptors = getSessionLayerDescriptors(applicationPath);
		for(final File sessionDescriptor : sessionDescriptors) {
			if(sessionDescriptor.exists() && sessionDescriptor.isFile())	{
				logMessage("Found session layer");
				result = true;
			}
		}

		final File markerDescriptor = getMarkerDescriptor(applicationPath);
		if(markerDescriptor.exists() && markerDescriptor.isFile())	{
			logMessage("Found markers");
			result = true;
		}

		return result;
	}

	private File getGroundLayerDescriptor(final File applicationPath)	{
		return new File(applicationPath, BASE_LAYER_DESCRIPTOR);
	}

	private List<File> getCaveLayerDescriptors(final File applicationPath)	{
		return Arrays.asList(
				new File(applicationPath, CAVE1_LAYER_DESCRIPTOR),
				new File(applicationPath, CAVE2_LAYER_DESCRIPTOR),
				new File(applicationPath, CAVE3_LAYER_DESCRIPTOR),
				new File(applicationPath, CAVE4_LAYER_DESCRIPTOR)
		);
	}

	private List<File> getSessionLayerDescriptors(final File applicationPath)	{

		final File projectPath = new File(applicationPath, PROJECT_FOLDER);
		final List<File> sessionDescriptors = new ArrayList<>();

		final String[] folderContent = projectPath.list();
		if(folderContent == null)	{
			return sessionDescriptors;
		}

		for(final String folder : projectPath.list()) {

			if(folder.equals("base") || folder.equals("cave"))	{
				continue;
			}

			sessionDescriptors.add(new File(projectPath, folder + File.separator + LAYER_DESCRIPTOR));

		}

		return sessionDescriptors;

	}

	private File getMarkerDescriptor(final File applicationPath)	{
		return new File(applicationPath, MARKERS_DESCRIPTOR);
	}

	private void doImportLayer(final File layerDescriptor, Layer targetLayer) {

		try(final FileInputStream descriptorStream = new FileInputStream(layerDescriptor)) {

			layerConverter.setTargetLayer(targetLayer);
			logMessage("Reading tiles...");
			xStreamMarshaller.unmarshalInputStream(descriptorStream);
			getEntityManager().persist(targetLayer);
			logMessage("Generating zoom levels...");
			layerBean.generateLevels(targetLayer.getId(), s -> {}, (aLong, aLong2) -> {});

		}catch(final Exception e) {
			logError(e);
		}

	}

	private void doImportMarker(final File markerDescriptor)	{

		try(final FileInputStream descriptorStream = new FileInputStream(markerDescriptor)) {

			logMessage("Reading markers...");
			final MarkerOld markerOld = (MarkerOld)xStreamMarshaller.unmarshalInputStream(descriptorStream);
			logMessage("Storing marker types...");
			markerOld.getMarkerIconList().forEach(markerIcon -> getEntityManager().persist(markerIcon));
			logMessage("Storing markers...");
			markerOld.getMarkerList().forEach(marker -> getEntityManager().persist(marker));

		}catch(final Exception e)	{
			logError(e);
		}

	}
}
