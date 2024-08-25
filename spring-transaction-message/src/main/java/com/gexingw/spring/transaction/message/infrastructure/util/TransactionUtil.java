package com.gexingw.spring.transaction.message.infrastructure.util;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author GeXingW
 */
public class TransactionUtil {

    public static void doAfterCommitted(Runnable callback) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionCompletionCallback(callback));
        }
    }

}
