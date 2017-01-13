package com.evgenltd.mapper.core.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 10-01-2017 23:43
 */
@Component
@Aspect
public class BeanInterceptor {

	private static final Logger log = LogManager.getLogger(BeanInterceptor.class);

	@Before("execution(* com.evgenltd.mapper.core.bean.*.*(..))")
	private void writeTransaction(JoinPoint joinPoint) {
		final String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		if (transactionName == null) {
			return;
		}
		final boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		log.info(String.format("%s; transaction=[%s]; readOnly=[%s]", joinPoint.toShortString(), transactionName, isReadOnly));
	}

}
