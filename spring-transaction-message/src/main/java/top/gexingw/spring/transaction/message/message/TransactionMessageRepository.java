package top.gexingw.spring.transaction.message.message;

import java.io.Serializable;
import java.util.List;

/**
 * @author GeXingW
 */
public interface TransactionMessageRepository {

    List<TransactionMessage> queryAllRetryable(long currentTimestamp);

    void save(TransactionMessage transactionMessage);

    void remove(Serializable id);

    TransactionMessage find(Serializable id);

}
