package com.gexingw.spring.transaction.message.service;

import com.gexingw.spring.transaction.message.message.TransactionMessage;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * @author GeXingW
 */
public interface TransactionMessageService {

    List<TransactionMessage> queryRetryableMessages();

    List<TransactionMessage> queryRetryableMessages(long currentTimestamp);

    void sendSucceed(Serializable id);

    void sendFailed(Serializable id);

    @Transactional(rollbackFor = Exception.class)
    void send(TransactionMessage transactionMessage, Runnable sendCallback);

}
