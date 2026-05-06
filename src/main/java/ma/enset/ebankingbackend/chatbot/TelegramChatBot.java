package ma.enset.ebankingbackend.chatbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true")
@Slf4j
public class TelegramChatBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final BankingRagService ragService;
    private final RestTemplate restTemplate = new RestTemplate();
    private long lastUpdateId = 0;

    private static final String TELEGRAM_API = "https://api.telegram.org/bot";

    public TelegramChatBot(BankingRagService ragService) {
        this.ragService = ragService;
    }

    @Scheduled(fixedDelay = 2000)
    @SuppressWarnings("unchecked")
    public void pollUpdates() {
        try {
            String url = TELEGRAM_API + botToken + "/getUpdates?offset=" + (lastUpdateId + 1) + "&timeout=1";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getBody() == null) return;

            List<Map<String, Object>> updates = (List<Map<String, Object>>) response.getBody().get("result");
            if (updates == null || updates.isEmpty()) return;

            for (Map<String, Object> update : updates) {
                lastUpdateId = ((Number) update.get("update_id")).longValue();
                Map<String, Object> message = (Map<String, Object>) update.get("message");
                if (message == null) continue;

                String text = (String) message.get("text");
                Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                long chatId = ((Number) chat.get("id")).longValue();

                if (text != null && !text.isEmpty()) {
                    String reply = ragService.chat(text);
                    sendMessage(chatId, reply);
                }
            }
        } catch (Exception e) {
            log.error("Telegram polling error: {}", e.getMessage());
        }
    }

    private void sendMessage(long chatId, String text) {
        try {
            String url = TELEGRAM_API + botToken + "/sendMessage";
            Map<String, Object> body = Map.of("chat_id", chatId, "text", text, "parse_mode", "HTML");
            restTemplate.postForEntity(url, body, Map.class);
        } catch (Exception e) {
            log.error("Failed to send Telegram message: {}", e.getMessage());
        }
    }
}
