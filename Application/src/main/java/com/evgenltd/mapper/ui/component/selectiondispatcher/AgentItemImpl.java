package com.evgenltd.mapper.ui.component.selectiondispatcher;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 21-08-2016 04:46
 */
public class AgentItemImpl<T> implements SelectionDispatcher.AgentItem<T> {

	private Long identifier;
	private Class<T> type;

	public AgentItemImpl(Long identifier, final Class<T> type) {
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public Long getIdentifier() {
		return identifier;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		AgentItemImpl<?> agentItem = (AgentItemImpl<?>)o;

		if(!identifier.equals(agentItem.identifier)) return false;
		return type.equals(agentItem.type);

	}

	@Override
	public int hashCode() {
		int result = identifier.hashCode();
		result = 31 * result + type.hashCode();
		return result;
	}
}
