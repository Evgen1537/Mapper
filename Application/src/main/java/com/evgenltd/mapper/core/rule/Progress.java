package com.evgenltd.mapper.core.rule;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Project: mapper</p>
 * <p>Author:  Evgeniy Lebedev</p>
 * <p>Created: 06-02-2017 09:04</p>
 */
public interface Progress {

	void updateTitle(@NotNull String title);

	void updateMessage(@NotNull String message);

	void updateProgress(@NotNull Long workDone, @NotNull Long workMax);

}
