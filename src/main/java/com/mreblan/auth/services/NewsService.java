package com.mreblan.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NewsService {

    private final WebClient webClient;

    public NewsService(@Value("${external.news.url}") String newsUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(newsUrl)
                .build();
    }

    public List<Map<String, Object>> getTopHeadlines() {
        try {
            Map<String, Object> response = webClient.get()
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                              resp -> Mono.error(new RuntimeException("News API error: " + resp.statusCode())))
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofMillis(5000))
                    .block();
            return (List<Map<String, Object>>) response.getOrDefault("articles", Collections.emptyList());
        } catch (Exception e) {
            log.error("Failed to fetch news", e);
            return Collections.emptyList(); // graceful degradation
        }
    }
}