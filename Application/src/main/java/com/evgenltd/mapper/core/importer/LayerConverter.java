package com.evgenltd.mapper.core.importer;

import com.evgenltd.mapper.core.entity.Layer;
import com.evgenltd.mapper.core.entity.Tile;
import com.evgenltd.mapper.core.entity.impl.EntityFactory;
import com.evgenltd.mapper.core.util.Utils;
import com.evgenltd.mapper.mapviewer.common.ZLevel;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

/**
 * Project: Mapper
 * Author:  Lebedev
 * Created: 04-September-2015 11:54
 */
@ParametersAreNonnullByDefault
public class LayerConverter implements Converter {

	private File applicationPath;
	private Layer targetLayer;

	public void setApplicationPath(final File applicationPath) {
		this.applicationPath = applicationPath;
	}

	public void setTargetLayer(Layer targetLayer) {
		this.targetLayer = targetLayer;
	}

	private String resolveTilePath(final String relativeTilePath)	{
		final File absoluteTilePath = new File(applicationPath, relativeTilePath);
		return absoluteTilePath.getAbsolutePath();
	}
	
    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
        int version = reader.getAttribute("version") == null ? 1 : Integer.parseInt(reader.getAttribute("version"));
        switch (version)    {
            case 1:
                return unmarshalVersion1(reader, unmarshallingContext);
            case 2:
                return unmarshalVersion2(reader, unmarshallingContext);
            case 3:
                return unmarshalVersion3(reader, unmarshallingContext);
            default:
                throw new IllegalArgumentException("Unknown data version: "+version);
        }
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(LayerOld.class);
    }

    private Layer unmarshalVersion1(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext)    {
		
        reader.moveDown();  reader.moveUp();    // descriptor file path

        reader.moveDown();
        final String sessionFolder = reader.getValue();
        reader.moveUp();

        reader.moveDown();
		targetLayer.setX(Double.parseDouble(reader.getAttribute("x")));
		targetLayer.setY(Double.parseDouble(reader.getAttribute("y")));
        reader.moveUp();
		
        if(!targetLayer.getType().isGlobal())   {
            final File sessionFolderFile = new File(sessionFolder);
            targetLayer.setName(sessionFolderFile.getName());
        }
        if(sessionFolder != null && !sessionFolder.equals(""))  {
            targetLayer.setSessionPath(sessionFolder);
        }


        while(reader.hasMoreChildren())    {
            reader.moveDown();
            final String zLevel = reader.getAttribute("zoom");
            if(!zLevel.equals("Z1"))    {
                reader.moveUp();
                continue;
            }

            while(reader.hasMoreChildren()) {
                reader.moveDown();

                final String tileFile = reader.getAttribute("file");
                final Point2D point = new Point2D(
                        Double.parseDouble(reader.getAttribute("x")),
                        Double.parseDouble(reader.getAttribute("y"))
                );
                final Image image = new Image("file:" + resolveTilePath(tileFile));
                final String hash = Utils.calculateHash(image);
				final Tile tile = EntityFactory.createTile();
				tile.setX(point.getX());
				tile.setY(point.getY());
				tile.setZ(ZLevel.Z1);
				tile.setLayer(targetLayer);
				tile.setImage(image);
				tile.setHash(hash);
				targetLayer.getTileSet().add(tile);

                reader.moveUp();
            }

            reader.moveUp();
        }

        return targetLayer;
    }

    private Layer unmarshalVersion2(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext)    {

        reader.moveDown();  reader.moveUp();    // descriptor file path

        reader.moveDown();
        final String sessionFolder = reader.getValue();
        reader.moveUp();

        reader.moveDown();
		targetLayer.setX(Double.parseDouble(reader.getAttribute("x")));
		targetLayer.setY(Double.parseDouble(reader.getAttribute("y")));
        reader.moveUp();

        reader.moveDown();  reader.moveUp();    // outline

        if(!targetLayer.getType().isGlobal())   {
            final File sessionFolderFile = new File(sessionFolder);
			targetLayer.setName(sessionFolderFile.getName());
        }
        if(sessionFolder != null && !sessionFolder.equals(""))  {
			targetLayer.setSessionPath(sessionFolder);
        }

        while(reader.hasMoreChildren())    {
            reader.moveDown();
            final String zLevel = reader.getAttribute("zoom");
            if(!zLevel.equals("Z1"))    {
                reader.moveUp();
                continue;
            }

            while(reader.hasMoreChildren()) {
                reader.moveDown();

                final String tileFile = reader.getAttribute("file");
                final Point2D point = new Point2D(
                        Double.parseDouble(reader.getAttribute("x")),
                        Double.parseDouble(reader.getAttribute("y"))
                );

				final Image image = new Image("file:" + resolveTilePath(tileFile));
				final String hash = Utils.calculateHash(image);
				final Tile tile = EntityFactory.createTile();
				tile.setX(point.getX());
				tile.setY(point.getY());
				tile.setZ(ZLevel.Z1);
				tile.setLayer(targetLayer);
				tile.setImage(image);
				tile.setHash(hash);
				targetLayer.getTileSet().add(tile);

                reader.moveUp();
            }

            reader.moveUp();
        }

        return targetLayer;
    }


    private Layer unmarshalVersion3(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext)    {

        reader.moveDown();  reader.moveUp();    // descriptor file path

        reader.moveDown();
        final String name = reader.getValue();
        reader.moveUp();

        reader.moveDown();
        final String sessionFolder = reader.getValue();
        reader.moveUp();

        reader.moveDown();
		targetLayer.setX(Double.parseDouble(reader.getAttribute("x")));
		targetLayer.setY(Double.parseDouble(reader.getAttribute("y")));
        reader.moveUp();

        reader.moveDown();  reader.moveUp();    // visibility

        reader.moveDown();  reader.moveUp();    // outline

        if(!targetLayer.getType().isGlobal())   {
            if(name != null && !name.equals(""))    {
				targetLayer.setName(name);
            }else {
                final File sessionFolderFile = new File(sessionFolder);
				targetLayer.setName(sessionFolderFile.getName());
            }
        }
        if(sessionFolder != null && !sessionFolder.equals(""))  {
			targetLayer.setSessionPath(sessionFolder);
        }

        while(reader.hasMoreChildren())    {
            reader.moveDown();
            final String zLevel = reader.getAttribute("zoom");
            if(!zLevel.equals("Z1"))    {
                reader.moveUp();
                continue;
            }

            while(reader.hasMoreChildren()) {
                reader.moveDown();

                final String tileFile = reader.getAttribute("file");
                final Point2D point = new Point2D(
                        Double.parseDouble(reader.getAttribute("x")),
                        Double.parseDouble(reader.getAttribute("y"))
                );

				final Image image = new Image("file:" + resolveTilePath(tileFile));
				final String hash = Utils.calculateHash(image);
				final Tile tile = EntityFactory.createTile();
				tile.setX(point.getX());
				tile.setY(point.getY());
				tile.setZ(ZLevel.Z1);
				tile.setLayer(targetLayer);
				tile.setImage(image);
				tile.setHash(hash);
				targetLayer.getTileSet().add(tile);

                reader.moveUp();
            }

            reader.moveUp();
        }

        return targetLayer;
    }

}
