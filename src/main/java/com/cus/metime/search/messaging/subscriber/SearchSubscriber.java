package com.cus.metime.search.messaging.subscriber;

import com.cus.metime.search.SearchApp;
import com.cus.metime.search.config.ApplicationProperties;
import com.cus.metime.search.domain.SearchParameter;
import com.cus.metime.search.messaging.RabbitConnection;
import com.cus.metime.search.service.ElasticSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author Handoyo
 */
@Configuration
public class SearchSubscriber extends RabbitConnection {

    private static final Logger log = LoggerFactory.getLogger(SearchApp.class);

    private String queueName = "";
    private String exchangeName = "";
    private String elasticServer = "";
    private String elasticUsername = "";
    private String elasticPassword = "";

    public SearchSubscriber(ApplicationProperties applicationProperties) {
        //connect to local
//        super(
//                applicationProperties.getRabbit().getHost(),
//                applicationProperties.getRabbit().getUsername(),
//                applicationProperties.getRabbit().getPassword());

        //connect to cloud amqp
        super(
                applicationProperties.getRabbit().getUri(),
                applicationProperties.getRabbit().getRequestedHeartbeat(),
                applicationProperties.getRabbit().getConnectionTimeout());
        
        this.exchangeName = applicationProperties.getRabbit().getExchangeName();
        this.queueName = applicationProperties.getRabbit().getQueueName();
        this.elasticServer = applicationProperties.getElastic().getServerUrl();
        this.elasticUsername = applicationProperties.getElastic().getUsername();
        this.elasticPassword = applicationProperties.getElastic().getPassword();
    }

    @PostConstruct
    @Async("taskExecutor")
    public void consume() {
        try {
            log.info("\n----------------------------------------------------------\n\t"
                    + "Elastic server: \t{}\n----------------------------------------------------------",
                    elasticServer);
            log.info("\n----------------------------------------------------------\n\t"
                    + "RabbitMQ Status: \t{}\n----------------------------------------------------------",
                    "Starting");
            Connection connection = getConnectionFactory().newConnection();
            Channel channel = connection.createChannel();
            log.info("\n----------------------------------------------------------\n\t"
                    + "RabbitMQ Status: \t{}\n----------------------------------------------------------",
                    "Consuming the message");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                        BasicProperties properties, byte[] body) throws IOException {
                    if (convert(body)) {
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };
            channel.exchangeDeclare(this.exchangeName, BuiltinExchangeType.FANOUT, true);
            channel.queueDeclare(this.queueName, true, false, false, null);
            channel.queueBind("", this.exchangeName, "");
            channel.basicConsume(this.queueName, consumer);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            log.info("\n----------------------------------------------------------\n\t"
                    + "RabbitMQ Status: \t{}\n----------------------------------------------------------",
                    "Failed to start");
//            throw new Exception(e.getMessage());
        }
    }

    private Boolean convert(byte[] body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            sendToElastic(mapper.readValue(body, SearchParameter.class));
            System.out.println(" [x] Received ");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendToElastic(SearchParameter searchParameter) throws Exception {
        new ElasticSenderService(this.elasticUsername, this.elasticPassword, this.elasticServer, searchParameter);
    }
}
