package org.taskmanager.websocket;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.taskmanager.BaseTest;
import org.taskmanager.WebSocket;
import org.taskmanager.enums.WebSocketMessageType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseWebSocketTest extends BaseTest {
    protected WebSocket webSocket;

    @BeforeAll
    public void initializeWs() throws Exception {
        webSocket = new WebSocket(wsUrl);
        webSocket.connect();
    }

    @Test
    void checkWebSocketTest() {
        Assertions.assertTrue(webSocket.isRunning(), "WebSocket is not running");
    }

    @AfterAll
    public void tearDownWs() {
        if (webSocket != null) {
            webSocket.disconnect();
        }
    }

    protected void assertWebSocketMessage(String expectedMessage, WebSocketMessageType type, int timeoutSeconds) throws InterruptedException {
        String webSocketMessage = webSocket.waitForMessage(timeoutSeconds);

        Assertions.assertNotNull(webSocketMessage, "WebSocket did not receive any message.");
        Assertions.assertTrue(webSocketMessage.contains(expectedMessage),
                "Expected WebSocket message to contain: " + expectedMessage);
        Assertions.assertTrue(webSocketMessage.contains(type.getName()),
                "Expected WebSocket message to contain type: " + type.getName());
    }
}
