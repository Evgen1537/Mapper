package com.evgenltd.mapper.core.aop;

import com.evgenltd.mapper.core.Context;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 13-07-2016 23:56
 */
public class PersistenceInterceptor extends EmptyInterceptor {

	@Override
	public void beforeTransactionCompletion(Transaction tx) {

		final String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		if(transactionName == null || Objects.equals(transactionName, getClass().getName() + ".undo"))	{
			return;
		}

		final boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		if(isReadOnly)	{
			return;
		}
		
		Context
				.get()
				.getEnversBean()
				.beforeTransactionCommit();
		
	}
}
