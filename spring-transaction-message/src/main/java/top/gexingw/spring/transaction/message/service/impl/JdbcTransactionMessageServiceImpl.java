package top.gexingw.spring.transaction.message.service.impl;

import top.gexingw.spring.transaction.message.infrastructure.util.TransactionUtil;
import top.gexingw.spring.transaction.message.message.TransactionMessage;
import top.gexingw.spring.transaction.message.message.TransactionMessageRepository;
import top.gexingw.spring.transaction.message.service.TransactionMessageService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * @author GeXingW
 */
public class JdbcTransactionMessageServiceImpl implements TransactionMessageService {

    private final TransactionMessageRepository transactionMessageRepository;

    public JdbcTransactionMessageServiceImpl(TransactionMessageRepository transactionMessageRepository) {
        this.transactionMessageRepository = transactionMessageRepository;
    }

    @Override
    public List<TransactionMessage> queryRetryableMessages() {
        return queryRetryableMessages(Instant.now().getEpochSecond());
    }

    @Override
    public List<TransactionMessage> queryRetryableMessages(long currentTimestamp) {
        return transactionMessageRepository.queryAllRetryable(currentTimestamp);
    }

    @Override
    public void sendSucceed(Serializable id) {
        transactionMessageRepository.remove(id);
    }

    @Override
    public void sendFailed(Serializable id) {
        TransactionMessage transactionMessage = transactionMessageRepository.find(id);

        transactionMessage.sendFailed();
        transactionMessageRepository.save(transactionMessage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(TransactionMessage transactionMessage, Runnable sendCallback) {
        transactionMessageRepository.save(transactionMessage);

        TransactionUtil.doAfterCommitted(sendCallback);
    }

}
