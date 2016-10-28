package com.evgenltd.mapper.ui.screen.settings.hotkey;

import com.evgenltd.mapper.core.entity.Settings;
import com.evgenltd.mapper.ui.UIContext;
import com.evgenltd.mapper.ui.component.command.Command;
import com.evgenltd.mapper.ui.component.command.CommandManager;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 00:45
 */
class HotKeyContainer {

	private final List<TreeItem<HotKey>> flatContainer = new ArrayList<>();
	private final TreeItem<HotKey> treeContainer = new TreeItem<>();

	private final CommandManager commandManager = UIContext.get().getCommandManager();

	void fillContainer()	{

		final List<Command> commandList = commandManager.getCommandList();

		flatContainer.addAll(
				commandList
						.stream()
						.map(this::makeLeafTreeItem)
						.collect(Collectors.toList())
		);

		flatContainer
				.stream()
				.sorted((first, second) -> Integer.compare(first.getValue().getPosition(),second.getValue().getPosition()))
				.forEach(treeItem -> {
					final TreeItem<HotKey> targetNodeTreeItem = getTargetTreeItem(treeItem.getValue().getPath());
					targetNodeTreeItem.getChildren().add(treeItem);
				});

	}

	void fillEntity(@NotNull final Settings settings)	{
		settings.setHotKeyMap(
				flatContainer
						.stream()
						.filter(treeItem -> treeItem.getValue().getKeyCombination() != null)
						.collect(Collectors.toMap(
								treeItem -> treeItem.getValue().getId(),
								treeItem -> treeItem.getValue().getKeyCombination()
						))
		);

	}

	TreeItem<HotKey> getTreeRoot() {
		return treeContainer;
	}

	List<TreeItem<HotKey>> getTreeItemList() {
		return flatContainer;
	}

	@NotNull
	private TreeItem<HotKey> getTargetTreeItem(@NotNull final String commandPath)	{

		String preparedPath = commandPath.trim();

		if(preparedPath.isEmpty())	{
			throw new IllegalArgumentException(String.format("Illegal path=[%s]",preparedPath));
		}

		preparedPath = preparedPath.startsWith("/")
				? preparedPath.substring(1)
				: preparedPath;

		final String[] paths = preparedPath.split("/");
		TreeItem<HotKey> currentTreeItem = getTreeRoot();

		// loop will drill down into tree structure
		for(String path : paths) {
			currentTreeItem = getTreeItem(currentTreeItem, path);
		}

		return currentTreeItem;
	}

	/**
	 * @param parentTreeItem parent tree item in which method will search for child tree item
	 * @param itemName name of tree item which should be founded
	 * @return child with specified name of specified currentTreeItem, if does not exist then will be created
	 */
	@NotNull
	private TreeItem<HotKey> getTreeItem(
			@NotNull final TreeItem<HotKey> parentTreeItem,
			@NotNull final String itemName
	) {
		return parentTreeItem
				.getChildren()
				.stream()
				.filter(treeItem -> checkIfTreeItemAcceptable(itemName, treeItem))
				.findAny()
				.orElseGet(() -> makeNodeTreeItem(parentTreeItem, itemName));
	}

	private boolean checkIfTreeItemAcceptable(@NotNull final String name, @NotNull final TreeItem<HotKey> treeItem) {
		final HotKey hotKey = treeItem.getValue();
		return hotKey.isNode()
				&& Objects.equals(hotKey.getName(),name);
	}

	@NotNull
	private TreeItem<HotKey> makeNodeTreeItem(
			@NotNull final TreeItem<HotKey> parentTreeItem,
			@NotNull final String itemName
	)	{
		final HotKey nodeHotKey = new HotKey(itemName);
		final TreeItem<HotKey> newTreeItem = new TreeItem<>(nodeHotKey);
		parentTreeItem.getChildren().add(newTreeItem);
		return newTreeItem;
	}

	private TreeItem<HotKey> makeLeafTreeItem(@NotNull final Command command)	{
		final HotKey hotKey = new HotKey(command);
		return new TreeItem<>(hotKey);
	}
}
