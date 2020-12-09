package io.vertx.test.mqtt;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.messages.MqttConnAckMessage;

public class ClientMqttVerticle extends AbstractVerticle {

	private static final int BROKER_PORT = 2883;
	private static final String BROKER_HOST = "localhost";

	private MqttClient client;

	@Override
	public void start() {

		MqttClientOptions options = new MqttClientOptions();
		// options.setKeepAliveInterval(10);
		options.setAutoKeepAlive(true); // useless, default should be true
		client = MqttClient.create(vertx, options);

		Handler<AsyncResult<MqttConnAckMessage>> connectionHandler = ar -> {
			if (ar.succeeded()) {
				System.out.println("connected to " + BROKER_HOST + ":" + BROKER_PORT);
				client.subscribe("topic/topic", 0);
			} else {
				System.out.println("failed to connect to MQTT broker" + ar.cause());
			}
		};

		client.connect(BROKER_PORT, BROKER_HOST, connectionHandler);

		client.closeHandler(handler -> {
			System.out.println("connection to broker closed, reconnect ");
		});

		client.publishHandler(publishHandler -> {
			System.out.println("Client recieved a new message : " + publishHandler.payload().toString());
		});

		System.out.println("ClientMqttVerticle ready!");
	}

}
