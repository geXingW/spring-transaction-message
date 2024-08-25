package top.gexingw.spring.transaction.message.message;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author GeXingW
 */
@SuppressWarnings("LombokGetterMayBeUsed")
public class TransactionMessage implements Serializable {

    private Long id;

    private final String topic;

    private final String key;

    private final Integer maxRetryCount;

    private Integer retriedCount;

    private Long nextRetryTime;

    private final Object payload;

    private MessageStatus messageStatus;

    public TransactionMessage(String topic, String key, Object payload) {
        this(null, topic, key, 3, 0, Instant.now().getEpochSecond(), payload, MessageStatus.NORMAL);
    }

    public TransactionMessage(String topic, String key, Integer maxRetryCount, Object payload) {
        this(null, topic, key, maxRetryCount, 0, Instant.now().getEpochSecond(), payload, MessageStatus.NORMAL);
    }

    public TransactionMessage(
            Long id, String topic, String key, Integer maxRetryCount, Integer retriedCount, Long nextRetryTime, Object payload
            , MessageStatus messageStatus
    ) {
        this.id = id;
        this.topic = topic;
        this.key = key;
        this.maxRetryCount = maxRetryCount;
        this.retriedCount = retriedCount;
        this.nextRetryTime = nextRetryTime;
        this.payload = payload;
        this.messageStatus = messageStatus;
    }

    public Long getId() {
        return id;
    }

    public TransactionMessage setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public String getKey() {
        return key;
    }

    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    public Integer getRetriedCount() {
        return this.retriedCount;
    }

    public Long getNextRetryTime() {
        return nextRetryTime;
    }

    public Object getPayload() {
        return payload;
    }

    public MessageStatus getMessageStatus() {
        return this.messageStatus;
    }

    public void sendFailed() {
        this.retriedCount++;

        if (this.retriedCount >= this.maxRetryCount) {
            this.messageStatus = MessageStatus.FAILED;
        }

        this.nextRetryTime += this.retriedCount * 60;
    }

}
