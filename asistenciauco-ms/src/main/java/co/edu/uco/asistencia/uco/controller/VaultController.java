package co.edu.uco.asistencia.uco.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class VaultController {

    private final WebClient webClient;

    @Value("${vault.token}")
    private String vaultToken;

    @Value("${vault.url}")
    private String vaultUrl;

    @Value("${vault.secret-path}")
    private String secretPath;

    public VaultController(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @GetMapping("/vault/secret")
    public Mono<String> getSecret() {
        return webClient.get()
                .uri(vaultUrl + "/v1/" + secretPath)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + vaultToken)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    String valor = json.at("/data/data/test").asText("NO-SECRET-FOUND");
                    System.out.println("🔐 Secreto recibido desde Vault: " + valor);
                    return valor;
                });
    }
}