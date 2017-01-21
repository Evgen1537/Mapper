package com.evgenltd.mapper.core.importer;

import com.evgenltd.mapper.core.entity.Marker;
import com.evgenltd.mapper.core.entity.MarkerIcon;
import com.evgenltd.mapper.core.entity.MarkerPoint;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.enums.MarkerType;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import javafx.scene.image.Image;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 16-04-2016 19:06
 */
@ParametersAreNonnullByDefault
public class MarkerConverter implements Converter {

	private File applicationPath;
	private List<MarkerIcon> markerIconList;
	private List<MarkerIcon> readiedIconList = new ArrayList<>();
	private MarkerIcon noneMarkerIcon;

	public void setApplicationPath(final File applicationPath) {
		this.applicationPath = applicationPath;
	}

	public void setMarkerIconList(final List<MarkerIcon> markerIconList) {
		this.markerIconList = markerIconList;
	}

	public void setNoneMarkerIcon(final MarkerIcon noneMarkerIcon) {
		this.noneMarkerIcon = noneMarkerIcon;
	}

	private MarkerIcon getMarkerIconByName(final String name)	{

		return readiedIconList
				.stream()
				.filter(markerIcon -> Objects.equals(markerIcon.getName(), name))
				.findAny()
				.orElse(
						markerIconList
								.stream()
								.filter(markerIcon -> Objects.equals(markerIcon.getName(), name))
								.findAny()
								.orElse(noneMarkerIcon)
				);

	}

	@Override
	public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		final int version = Integer.parseInt(reader.getAttribute("version"));
		switch(version) {
			case 2:
				return unmarshallVersion2(reader, context);
			case 1:
				return unmarshallVersion1(reader, context);
			default:
				throw new IllegalArgumentException("Unknown data version: "+version);
		}
	}

	private MarkerOld unmarshallVersion2(HierarchicalStreamReader reader, UnmarshallingContext context) {
		final MarkerOld markerOld = new MarkerOld();

		// marker types

		reader.moveDown();

		while(reader.hasMoreChildren()) {

			reader.moveDown();

			final String markerIconName = reader.getAttribute("label");
			final File imagePath = new File(applicationPath, reader.getAttribute("path"));

			if(imagePath.exists())	{

				final String absolutePath = imagePath.getAbsolutePath();
				final Image image = new Image("file:" + absolutePath);
				final MarkerIcon markerIcon = EntityFactory.createMarkerIcon(markerIconName, image);
				markerOld.addMarkerIcon(markerIcon);
				readiedIconList.add(markerIcon);

			}

			reader.moveUp();

		}

		reader.moveUp();

		// markers

		reader.moveDown();

		while(reader.hasMoreChildren()) {

			reader.moveDown();

			reader.moveDown();  // point
			final MarkerPoint markerPoint = EntityFactory.createMarkerPoint();
			markerPoint.setOrderNumber(1L);
			markerPoint.setX(Double.parseDouble(reader.getAttribute("x")));
			markerPoint.setY(Double.parseDouble(reader.getAttribute("y")));
			reader.moveUp();

			reader.moveDown();  // type
			final String type = reader.getValue();
			reader.moveUp();

			reader.moveDown();  // quality
			final String essence = reader.getAttribute("essence");
			final String substance = reader.getAttribute("substance");
			final String vitality = reader.getAttribute("vitality");
			reader.moveUp();

			reader.moveDown();  // comment
			final String comment = reader.getValue();
			reader.moveUp();

			reader.moveUp();

			final Marker area = EntityFactory.createMarker();
			area.setType(MarkerType.AREA);
			area.setMarkerIcon(getMarkerIconByName(type));
			area.setMarkerPointList(Collections.singleton(markerPoint));
			area.setEssence(essence);
			area.setSubstance(substance);
			area.setVitality(vitality);
			area.setComment(comment);

			markerPoint.setMarker(area);

			markerOld.addMarker(area);
		}

		reader.moveUp();

		return markerOld;
	}

	private MarkerOld unmarshallVersion1(HierarchicalStreamReader reader, UnmarshallingContext context) {
		final MarkerOld markerOld = new MarkerOld();

		while(reader.hasMoreChildren()) {
			reader.moveDown();

			reader.moveDown();  // point
			final MarkerPoint markerPoint = EntityFactory.createMarkerPoint();
			markerPoint.setX(Double.parseDouble(reader.getAttribute("x")));
			markerPoint.setY(Double.parseDouble(reader.getAttribute("y")));
			markerPoint.setOrderNumber(1L);
			reader.moveUp();

			reader.moveDown();  // type
			final String type = reader.getValue();
			reader.moveUp();

			reader.moveDown();  // quality
			final String essence = reader.getAttribute("essence");
			final String substance = reader.getAttribute("substance");
			final String vitality = reader.getAttribute("vitality");
			reader.moveUp();

			reader.moveDown();  // comment
			final String comment = reader.getValue();
			reader.moveUp();

			reader.moveUp();

			final Marker area = EntityFactory.createMarker();
			area.setType(MarkerType.AREA);
			area.setMarkerIcon(getMarkerIconByName(type));
			area.setMarkerPointList(Collections.singleton(markerPoint));
			area.setEssence(essence);
			area.setSubstance(substance);
			area.setVitality(vitality);
			area.setComment(comment);

			markerPoint.setMarker(area);

			markerOld.addMarker(area);
		}

		return markerOld;
	}

	@Override
	public boolean canConvert(Class aClass) {
		return aClass.equals(MarkerOld.class);
	}
}
