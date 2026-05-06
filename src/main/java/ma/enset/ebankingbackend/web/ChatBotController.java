package ma.enset.ebankingbackend.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.chatbot.BankingRagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "ChatBot", description = "AI Banking Assistant powered by RAG")
@CrossOrigin("*")
public class ChatBotController {

    private final BankingRagService ragService;

    @PostMapping
    @Operation(summary = "Send a message to the AI banking assistant")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String question = request.get("message");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }
        String response = ragService.chat(question);
        return ResponseEntity.ok(Map.of("response", response));
    }
}
