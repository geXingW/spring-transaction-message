package top.gexingw.spring.transaction.message.example.rabbitmq.adapter.schedule;

import top.gexingw.spring.transaction.message.application.service.TransactionMessageService;
import top.gexingw.spring.transaction.message.domain.message.TransactionMessage;
import top.gexingw.spring.transaction.message.example.rabbitmq.infrastructure.util.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author GeXingW
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class TransactionMessageSchedule {

    private final RabbitTemplate rabbitTemplate;

    private final TransactionMessageService transactionMessageService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void retry() {
        log.info("Retry transaction message ...");

        List<TransactionMessage> transactionMessages = transactionMessageService.queryRetryableMessages();
        if (transactionMessages.isEmpty()) {
            log.info("未查询到需要发送的事务消息！");
        } else {
            log.info("查询到需要发送的事务消息：{}", JacksonUtil.toJson(transactionMessages));
        }

        for (TransactionMessage transactionMessage : transactionMessages) {
            CorrelationData correlationData = new CorrelationData(transactionMessage.getId().toString());
            rabbitTemplate.convertAndSend(
                    transactionMessage.getTopic(), transactionMessage.getKey(), transactionMessage.getPayload(), correlationData
            );
        }

    }

}
