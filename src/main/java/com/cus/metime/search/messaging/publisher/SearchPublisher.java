package com.cus.metime.search.messaging.publisher;

import com.cus.metime.search.domain.SearchParameter;
import com.cus.metime.search.messaging.RabbitConnection;
import com.cus.metime.search.domain.KeyValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang.math.RandomUtils;

/**
 *
 * @author Handoyo
 */
public class SearchPublisher {

    private BasicProperties properties;
    private final String exchangeName = "search-exchange";
    private final String host = "10.17.50.48";
    private final String username = "admin";
    private final String password = "admin";
    private final String uri = "amqp://fecduabw:7-bgxN5iZ2PWKdW7mAdbmbXY7UP4CR5R@crocodile.rmq.cloudamqp.com/fecduabw";
    private final int requestedHeartbeat = 30;
    private final int connectionTimeout = 30000;

    public void send() {
        try {
            //connect to local
//            RabbitConnection rabbitConnection = new RabbitConnection(host, username, password);
            
            //connect to cloud amqp
            RabbitConnection rabbitConnection = new RabbitConnection(uri, requestedHeartbeat, connectionTimeout);
            Connection connection = rabbitConnection.getConnectionFactory().newConnection();

            properties = new BasicProperties().builder()
                    .expiration("1000000") //message otomatis di hapus setelah 16.6 menit
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            Channel channel = connection.createChannel();

            SearchParameter searchParameter = new SearchParameter();
            searchParameter.setId(RandomUtils.nextLong());
            searchParameter.setServiceName("Searchs");
            searchParameter.setType("Search");
            searchParameter.setLongitude(0.2);
            searchParameter.setLatitude(0.2);
            List<KeyValue> parameters = new ArrayList();
            parameters.add(new KeyValue("nama", UUID.randomUUID().toString()));
            parameters.add(new KeyValue("nama2", "tes2"));
            searchParameter.setParameters(parameters);
            channel.basicPublish(exchangeName, "", properties, mapper.writeValueAsString(searchParameter).getBytes());
            System.out.println("Sent : Success");
            channel.close();
            connection.close();

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
