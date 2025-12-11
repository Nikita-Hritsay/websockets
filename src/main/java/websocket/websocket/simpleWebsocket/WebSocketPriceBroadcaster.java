package websocket.websocket.simpleWebsocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketPriceBroadcaster
{
    private final PriceWebSocketHandler priceWebSocketHandler;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private double price = 0.0;

    public WebSocketPriceBroadcaster(
            PriceWebSocketHandler priceWebSocketHandler)
    {
        this.priceWebSocketHandler = priceWebSocketHandler;
        executor.scheduleAtFixedRate(() -> {
            price = price + (Math.random() - 0.5);
            broadcast(price);
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void broadcast(double price)
    {
        String payload = String.format("{\"price\": %.4f}", price);
        for (WebSocketSession session: priceWebSocketHandler.getSessions())
        {
            try
            {
                session.sendMessage(new TextMessage(payload));
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
