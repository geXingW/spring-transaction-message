package top.gexingw.spring.transaction.message.example.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author GeXingW
 */
@EnableScheduling
@SpringBootApplication
public class RabbitMQSpringTransMsgApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMQSpringTransMsgApplication.class, args);
    }

}
