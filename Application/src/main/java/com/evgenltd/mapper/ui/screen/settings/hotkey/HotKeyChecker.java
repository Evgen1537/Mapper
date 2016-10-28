package com.evgenltd.mapper.ui.screen.settings.hotkey;

import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Project: Mapper
 * Author:  Evgeniy
 * Created: 19-06-2016 01:50
 */
class HotKeyChecker {

	public static boolean check(@NotNull final List<TreeItem<HotKey>> hotKeyList)	{

		boolean passed = true;

		for(TreeItem<HotKey> nextTreeItem : hotKeyList) {

			final boolean failed = hotKeyList
					.stream()
					.filter(item -> checkDuplication(item.getValue(), nextTreeItem.getValue()))
					.findAny()
					.isPresent();

			if(failed) {
				markAsInvalid(nextTreeItem);
				passed = false;
			}
		}

		return passed;

	}

	private static boolean checkDuplication(@NotNull final HotKey sourceHotKey, @NotNull final HotKey template)	{
		return !Objects.equals(sourceHotKey.getId(), template.getId())
				&& Objects.equals(sourceHotKey.getScope(), template.getScope())
				&& sourceHotKey.getKeyCombination() != null
				&& template.getKeyCombination() != null
				&& Objects.equals(sourceHotKey.getKeyCombination(), template.getKeyCombination());
	}

	private static void markAsInvalid(@NotNull final TreeItem<HotKey> treeItem)	{
		TreeItem<HotKey> currentTreeItem = treeItem;
		while(currentTreeItem.getParent() != null) {
			treeItem.getValue().setInvalid(true);
			currentTreeItem = currentTreeItem.getParent();
		}
	}
}
