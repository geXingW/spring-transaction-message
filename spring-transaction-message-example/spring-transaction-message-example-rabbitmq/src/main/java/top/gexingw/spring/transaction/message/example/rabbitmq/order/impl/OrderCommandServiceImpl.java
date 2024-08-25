package top.gexingw.spring.transaction.message.example.rabbitmq.order.impl;

import top.gexingw.spring.transaction.message.example.rabbitmq.order.OrderCommandService;
import top.gexingw.spring.transaction.message.message.TransactionMessage;
import top.gexingw.spring.transaction.message.service.TransactionMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author GeXingW
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class OrderCommandServiceImpl implements OrderCommandService {

    private final TransactionMessageService transactionMessageService;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void create(Long id) {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        TransactionMessage transactionMessage = new TransactionMessage("order-topic", "order-key", 3, payload);
        transactionMessageService.send(transactionMessage,
                () -> {
                    CorrelationData correlationData = new CorrelationData(transactionMessage.getId().toString());
                    rabbitTemplate.convertAndSend(transactionMessage.getTopic(), transactionMessage.getKey(), transactionMessage.getPayload(), correlationData);
                }
        );
    }

}
