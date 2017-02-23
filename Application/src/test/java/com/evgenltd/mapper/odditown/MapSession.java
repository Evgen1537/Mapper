package com.evgenltd.mapper.odditown;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 15-02-2017 01:08</p>
 */
public class MapSession implements Serializable {

	private static final long serialVersionUID = 9383234L;

	private File path;

	private Map<String, Object> properties;

	//Do not serialize this
	private transient List<Tile> tiles;

	public MapSession(String name)
	{
		properties = new HashMap<String, Object>();
		properties.put("name", name);

		tiles = new LinkedList<Tile>();
	}

	public void addTile(Tile t)
	{
		tiles.add(t);
	}

	public String getName()
	{
		return (String)properties.get("name");
	}

	public String getGUID()
	{
		if (properties.containsKey("guid"))
			return (String)properties.get("guid");
		else
			return "";
	}

	public void setGUID(String id)
	{
		properties.put("guid", id);
	}

	public boolean getMatched()
	{
		if (properties.containsKey("matched"))
			return (boolean)properties.get("matched");
		else
			return false;
	}

	public void setMatched(boolean matched)
	{
		properties.put("matched", matched);
	}

	public boolean tryMatched() {
		return properties.containsKey("matched");
	}

	public int getXOffset()
	{
		if (properties.containsKey("xoffset"))
			return (int)properties.get("xoffset");
		else
			return 0;
	}

	public void setXOffset(int offset)
	{
		properties.put("xoffset", offset);
	}

	public int getYOffset()
	{
		if (properties.containsKey("yoffset"))
			return (int)properties.get("yoffset");
		else
			return 0;
	}

	public void setYOffset(int offset)
	{
		properties.put("yoffset", offset);
	}

	public List<Tile> getTiles()
	{
		return tiles;
	}

	public int tileCount()
	{
		return tiles.size();
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;

		MapSession other = (MapSession)obj;

		return other.getName().equals(this.getName());
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
