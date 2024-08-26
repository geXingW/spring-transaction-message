package top.gexingw.spring.transaction.message.domain.message;

/**
 * @author GeXingW
 */
public enum MessageStatus {

    NORMAL,

    FAILED,
    ;

    public static MessageStatus of(String status) {
        return MessageStatus.valueOf(status);
    }

}
