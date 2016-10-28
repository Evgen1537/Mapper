package com.evgenltd.mapper.ui.component.selectiondispatcher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-08-2016 04:25
 */
public class SelectionDispatcher {

	private final List<Agent> registeredAgentCollection = new ArrayList<>();

	public void registerAgent(@NotNull final Agent agent)	{
		agent.setSelectionListener(this::selectionHandler);
		registeredAgentCollection.add(agent);
	}

	public void unregisterAgent(@NotNull final Agent agent)	{
		agent.clearSelectionListener();
		registeredAgentCollection.remove(agent);
	}

	private void selectionHandler(@NotNull final Agent invoker)	{

		final List<AgentItem<?>> selectedItems = invoker.getSelectedItems();

		registeredAgentCollection.forEach((agent) -> {

			agent.clearSelectionListener();
			if(!agent.equals(invoker))	{
				agent.setSelectedItems(selectedItems);
			}
			agent.setSelectionListener(this::selectionHandler);

		});

	}

	public interface Agent	{

		void setSelectionListener(final @NotNull Consumer<Agent> listener);

		void clearSelectionListener();

		@NotNull
		List<AgentItem<?>> getSelectedItems();

		void setSelectedItems(@NotNull final List<AgentItem<?>> selectedItems);

	}

	public interface AgentItem<T>	{

		Long getIdentifier();

		Class<T> getType();

	}

}
