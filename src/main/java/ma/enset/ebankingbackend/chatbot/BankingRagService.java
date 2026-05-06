package ma.enset.ebankingbackend.chatbot;

import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BankingRagService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository operationRepository;

    private VectorStore vectorStore;

    public BankingRagService(ChatModel chatModel,
                             EmbeddingModel embeddingModel,
                             CustomerRepository customerRepository,
                             BankAccountRepository bankAccountRepository,
                             AccountOperationRepository operationRepository) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.embeddingModel = embeddingModel;
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.operationRepository = operationRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadDataIntoVectorStore() {
        try {
            vectorStore = SimpleVectorStore.builder(embeddingModel).build();
            List<Document> documents = new ArrayList<>();

            customerRepository.findAll().forEach(customer -> {
                String text = String.format(
                    "Client ID: %d, Nom: %s, Email: %s",
                    customer.getId(), customer.getName(), customer.getEmail()
                );
                documents.add(new Document(text, Map.of("type", "customer")));
            });

            bankAccountRepository.findAll().forEach(account -> {
                String text = String.format(
                    "Compte ID: %s, Type: %s, Solde: %.2f MAD, Statut: %s, Client: %s",
                    account.getId(), account.getClass().getSimpleName(),
                    account.getBalance(), account.getStatus(),
                    account.getCustomer() != null ? account.getCustomer().getName() : "N/A"
                );
                documents.add(new Document(text, Map.of("type", "account")));
            });

            documents.add(new Document(String.format(
                "Statistiques globales: %d clients, %d comptes, %d opérations enregistrées.",
                customerRepository.count(), bankAccountRepository.count(), operationRepository.count()
            ), Map.of("type", "stats")));

            vectorStore.add(documents);
            log.info("RAG vector store initialized with {} documents", documents.size());
        } catch (Exception e) {
            log.error("Failed to initialize RAG vector store: {}", e.getMessage());
        }
    }

    public String chat(String question) {
        if (vectorStore == null) {
            return "Le service RAG n'est pas encore prêt. Réessayez dans quelques instants.";
        }
        try {
            List<Document> relevantDocs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(question).topK(5).build());

            String context = relevantDocs.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));

            String prompt = String.format("""
                Tu es un assistant bancaire intelligent pour l'application Digital Banking.
                Réponds en français de manière concise et professionnelle.
                Si tu ne sais pas, dis-le honnêtement.

                Données bancaires disponibles :
                %s

                Question de l'utilisateur : %s
                """, context, question);

            return chatClient.prompt().user(prompt).call().content();
        } catch (Exception e) {
            log.error("AI service error: {}", e.getMessage());
            return "Désolé, une erreur s'est produite : " + e.getMessage();
        }
    }
}
