//package com.edos.Middleware.service;
//
//import com.edos.Middleware.dto.MonitoringData;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.Transport;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class MonitoringServiceTest {
//
//    @LocalServerPort
//    private int port;
//
//    private WebSocketStompClient stompClient;
//
//    private final CompletableFuture<MonitoringData> completableFuture = new CompletableFuture<>();
//
//    @BeforeEach
//    public void setup() {
//        // Configure a SockJS client for the test
//        List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
//        SockJsClient sockJsClient = new SockJsClient(transports);
//        this.stompClient = new WebSocketStompClient(sockJsClient);
//        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//    }
//
//    @Test
//    public void testMonitoringUpdateReceivesData() throws Exception {
//        // The URL for the WebSocket endpoint
//        String URL = String.format("ws://localhost:%d/ws", port);
//
//        // Connect to the server and subscribe to the topic
//        StompSession session = stompClient.connectAsync(URL, new StompSessionHandlerAdapter() {})
//                .get(1, TimeUnit.SECONDS);
//
//        session.subscribe("/topic/monitoring", new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return MonitoringData.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                // When a message is received, complete the future
//                completableFuture.complete((MonitoringData) payload);
//            }
//        });
//
//        // Wait for the scheduled task to send a message (max 10 seconds)
//        MonitoringData monitoringData = completableFuture.get(10, TimeUnit.SECONDS);
//
//        // Assert that we received a valid object
//        assertNotNull(monitoringData);
//        assertNotNull(monitoringData.getStatistics());
//        assertNotNull(monitoringData.getPredictions());
//    }
//}
