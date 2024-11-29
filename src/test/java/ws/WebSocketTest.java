package ws;

import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;

class WebSocketTest {

    @Test
    void testWebSocketConnection() {
        WebSocketClient client = new WebSocketClient(URI.create("ws://localhost:4242/ws")) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {
                System.out.println("Connected to server" + handshakeData.getHttpStatusMessage());
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Received message: " + message);
                // Verify the message content
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Connection closed");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connect();
        // ... (wait for connection and send/receive messages)
        client.onMessage("update");
        client.onMessage("Update");
        client.close();
    }
}
