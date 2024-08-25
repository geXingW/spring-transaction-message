package com.gexingw.spring.transaction.message.infrastructure.util;

import org.springframework.transaction.support.TransactionSynchronization;

/**
 * @author GeXingW
 */
public class TransactionCompletionCallback implements TransactionSynchronization {

    private final Runnable callback;

    public TransactionCompletionCallback(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void afterCompletion(int status) {
        if (status == TransactionSynchronization.STATUS_COMMITTED) {
            callback.run();
        }
    }

}
