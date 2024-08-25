package top.gexingw.spring.transaction.message.example.rabbitmq.order;

/**
 * @author GeXingW
 */
public interface OrderCommandService {

    void create(Long id);

}
