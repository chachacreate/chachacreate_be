package com.create.chacha.domains.shared.insertai.seller.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Service
@PropertySource("classpath:application.properties")
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private WebClient client() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                .exchangeStrategies(strategies)
                .build();
    }

    public String generateText(String system, String user) {
        String body = """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {"role": "system", "content": %s},
            {"role": "user",   "content": %s}
          ],
          "temperature": 0.7
        }
        """.formatted(json(system), json(user));

        try {
            String resp = client().post()
                    .uri("/v1/chat/completions")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(resp);
            return root.path("choices").get(0).path("message").path("content").asText(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String json(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}
