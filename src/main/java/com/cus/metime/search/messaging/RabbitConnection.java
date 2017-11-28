package com.cus.metime.search.messaging;

import com.rabbitmq.client.ConnectionFactory;

/**
 *
 * @author Handoyo
 */
public class RabbitConnection {

    private ConnectionFactory connectionFactory = new ConnectionFactory();
//    private final String host = "10.17.50.48";
//    private final String username = "admin";
//    private final String password = "admin";

    public RabbitConnection(String host, String username, String password) {
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
    }

    public RabbitConnection(String uri, int requestedHeartbeat, int connectionTimeout) {
        try {
            connectionFactory.setUri(uri);
            connectionFactory.setRequestedHeartbeat(requestedHeartbeat);
            connectionFactory.setConnectionTimeout(connectionTimeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
