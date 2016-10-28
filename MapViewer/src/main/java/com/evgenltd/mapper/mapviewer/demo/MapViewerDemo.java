package com.evgenltd.mapper.mapviewer.demo;

import com.evgenltd.mapper.mapviewer.common.MapViewer;
import com.evgenltd.mapper.mapviewer.common.MoveEvent;
import com.evgenltd.mapper.mapviewer.common.Node;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import math.geom2d.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 03-07-2016 20:06
 */
public class MapViewerDemo extends Application {

	private long identity = 0;
	private MapViewer mapViewer;
	private BlockGroup editedBlockGroup;
	private List<Node> data;

	public static void main(String[] args)	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		data = generateData();

		mapViewer = new MapViewer();
		mapViewer.setOnViewportChanged(event -> loadData());
		mapViewer.setOnMoved(this::moveHandler);

		primaryStage.setScene(new Scene(mapViewer, 800, 600));
		primaryStage.setTitle("MapViewer Demo");
		primaryStage.show();

		final Map<KeyCombination,Runnable> accelerators = primaryStage.getScene().getAccelerators();
		accelerators.put(KeyCombination.valueOf("Ctrl+E"), this::startEdit);
		accelerators.put(KeyCombination.valueOf("Ctrl+Enter"), this::endEdit);
		accelerators.put(KeyCombination.valueOf("Ctrl+Q"), this::cancelEdit);
		accelerators.put(KeyCombination.valueOf("Ctrl+P"), this::addBlock);
		accelerators.put(KeyCombination.valueOf("Ctrl+R"), this::removeBlock);
		accelerators.put(KeyCombination.valueOf("Ctrl+N"), this::addBlockGroup);
		accelerators.put(KeyCombination.valueOf("Ctrl+T"), this::increaseStep);
		accelerators.put(KeyCombination.valueOf("Ctrl+D"), this::decreaseStep);

		loadData();

	}

	// generation data

	private long newIdentity()  {
		return ++identity;
	}

	private List<Node> generateData()	{

		final List<Node> content = new ArrayList<>();

		for(int i=1; i<11; i++)	{

			content.add(new Block(newIdentity(), Color.RED, Math.random() * 500, Math.random() * 500, 30, 30));

		}

		final BlockGroup firstBlockGroup = new BlockGroup(newIdentity());
		content.add(firstBlockGroup);

		for(int i=30; i<41; i++)	{
			firstBlockGroup.add(new Block(newIdentity(), Color.YELLOW, Math.random() * 200, Math.random() * 200, 20, 20));
		}

		final BlockGroup secondBlockGroup = new BlockGroup(newIdentity());
		content.add(secondBlockGroup);

		for(int i=30; i<41; i++)	{
			secondBlockGroup.add(new Block(newIdentity(), Color.YELLOW, 200 + Math.random() * 200, 200 + Math.random() * 200, 20, 20));
		}

		return content;

	}

	// data

	private void loadData() {
		final List<Node> source = new ArrayList<>();
		for(Node node : data) {
			if(node instanceof Block)   {
				source.add(((Block)node).copy());
			}else if(node instanceof BlockGroup)    {
				source.add(((BlockGroup)node).copy());
			}
		}
		mapViewer.mergeNodes(source);
	}

	private void persist(Node node) {
		if(node.getIdentifier() != -1) {
			data.remove(node);
		}
		long id = node.getIdentifier() == -1
				? newIdentity()
				: node.getIdentifier();
		if(node instanceof Block)   {
			data.add(((Block)node).copy(id));
		}else if(node instanceof BlockGroup)    {
			data.add(((BlockGroup)node).copy(id));
		}
	}

	private void moveHandler(MoveEvent moveEvent)   {
		mapViewer
				.getSelectedNodes(node -> true)
				.forEach(this::persist);
	}

	// commands

	private void startEdit()	{
		final List<Node> selectedNodes = mapViewer.getSelectedNodes(node -> node instanceof BlockGroup);
		if(selectedNodes.size() != 1)	{
			return;
		}
		editedBlockGroup = (BlockGroup)selectedNodes.get(0);
		mapViewer.beginEditingNode(editedBlockGroup);
	}

	private void endEdit()	{
		mapViewer.endEditingNode();
		persist(editedBlockGroup);
		editedBlockGroup = null;
		loadData();
	}

	private void cancelEdit()   {
		mapViewer.endEditingNode();
		editedBlockGroup = null;
		loadData();
	}

	private void addBlock() {
		if(editedBlockGroup == null)    {
			return;
		}
		mapViewer.beginTargetingPoint(this::addBlockCallback);
	}

	private boolean addBlockCallback(@NotNull final Point2D selectedPoint) {
		final Block newBlock = new Block(newIdentity(), Color.YELLOW, selectedPoint.x(), selectedPoint.y(), 20,20);
		// mark as added
		editedBlockGroup.add(newBlock);
		return false;
	}

	private void removeBlock()  {
		if(editedBlockGroup == null)    {
			return;
		}
		editedBlockGroup
				.getChildren()
				.stream()
				.filter(Node::isSelected)
				.collect(Collectors.toList())
				.forEach(node -> editedBlockGroup.remove(node));
	}

	private void addBlockGroup()	{
		mapViewer.beginTargetingPoint(this::addBlockGroupCallback);
	}

	private boolean addBlockGroupCallback(@NotNull final Point2D selectedPoint)	{
		editedBlockGroup = new BlockGroup(-1);
		mapViewer.beginEditingNode(editedBlockGroup);
		return false;
	}

	private void increaseStep()	{
		mapViewer.setMoveStep(100);
	}

	private void decreaseStep()	{
		mapViewer.setMoveStep(1);
	}
}
