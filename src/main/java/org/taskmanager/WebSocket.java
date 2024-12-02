package org.taskmanager;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocket {
    private final WebSocketClient client;
    private final CountDownLatch messageLatch;
    private String lastReceivedMessage;

    public WebSocket(String uri) {
        this.messageLatch = new CountDownLatch(1);

        this.client = new WebSocketClient(URI.create(uri)) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {
                System.out.println("Connected to WebSocket: " + handshakeData.getHttpStatusMessage());
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Received message: " + message);
                lastReceivedMessage = message;
                messageLatch.countDown();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed. Code: " + code + ", Reason: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        };
    }

    public void connect() throws Exception {
        client.connectBlocking(10, TimeUnit.SECONDS);
    }

    public void disconnect() {
        client.close();
    }

    public String waitForMessage(int timeoutSeconds) throws InterruptedException {
        if (messageLatch.await(timeoutSeconds, TimeUnit.SECONDS)) {
            return lastReceivedMessage;
        }
        return null;
    }

    public boolean isRunning() {
        return client.isOpen();
    }
}