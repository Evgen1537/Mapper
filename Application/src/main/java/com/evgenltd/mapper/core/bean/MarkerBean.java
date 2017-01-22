package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.impl.LayerImpl;
import com.evgenltd.mapper.core.entity.impl.MarkerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 06-07-2016 23:29
 */
@Component
@ParametersAreNonnullByDefault
public class MarkerBean extends AbstractBean {

	@Autowired
	private Loader loader;

	@Transactional
	public void removeMarkersById(final List<Long> markerIdList)	{

		markerIdList.forEach(markerId -> getEntityManager().remove(
				getEntityManager().find(MarkerImpl.class, markerId)
		));

	}

	@Transactional
	public void removeMarkers(final List<Marker> markerList)	{

		markerList.forEach(marker -> getEntityManager().remove(marker));

	}

	@Transactional
	public void removeLinkedMarkers(final List<Long> layerIdList)	{

		final List<Marker> markerList = loader.loadLinkedMarkerList(layerIdList);
		removeMarkers(markerList);

	}

	@Transactional
	public void unlinkMarkers(final List<Long> layerIdList)	{

		relinkMarkers(layerIdList, null);

	}

	@Transactional
	public void relinkMarkers(final List<Long> layerIdList, @Nullable final Long targetLayerId) {

		final Layer targetLayer = targetLayerId == null
				? null
				: getEntityManager().find(LayerImpl.class, targetLayerId);

		loader.loadLinkedMarkerList(layerIdList)
				.forEach(marker -> {

					if(marker.getLayer() != null && layerIdList.contains(marker.getLayer().getId()))	{

						marker.setLayer(targetLayer);
						getEntityManager().merge(marker);

					}else if(marker.getExit() != null && layerIdList.contains(marker.getExit().getId()))	{

						marker.setExit(targetLayer);
						getEntityManager().merge(marker);

					}

				});

	}

	@Transactional
	public void removeMarkerIcon(@NotNull final MarkerIcon markerIcon)	{

		final MarkerIcon defaultMarkerIcon = loader.loadDefaultMarkerIcon();

		if(Objects.equals(defaultMarkerIcon, markerIcon))	{
			throw new IllegalArgumentException(String.format("%s is default, removing restricted", markerIcon.getName()));
		}

		getEntityManager()
				.createNativeQuery("update markers set marker_icon_id = :defaultMarkerIconId where marker_icon_id = :markerIconId")
				.setParameter("defaultMarkerIconId",defaultMarkerIcon.getId())
				.setParameter("markerIconId",markerIcon.getId())
				.executeUpdate();

		getEntityManager()
				.createNativeQuery("update markers_AUD set marker_icon_id = :defaultMarkerIconId where marker_icon_id = :markerIconId")
				.setParameter("defaultMarkerIconId",defaultMarkerIcon.getId())
				.setParameter("markerIconId",markerIcon.getId())
				.executeUpdate();

		getEntityManager().remove(getEntityManager().merge(markerIcon));

	}

}
