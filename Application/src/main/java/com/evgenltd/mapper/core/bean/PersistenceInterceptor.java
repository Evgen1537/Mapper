package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.Context;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 13-07-2016 23:56
 */
public class PersistenceInterceptor extends EmptyInterceptor {

	@Override
	public void beforeTransactionCompletion(Transaction tx) {
		Context
				.get()
				.getEnversBean()
				.beforeTransactionCommit();
	}
}
