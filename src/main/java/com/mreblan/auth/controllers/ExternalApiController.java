package com.mreblan.auth.controllers;

import com.mreblan.auth.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
public class ExternalApiController {

    private final NewsService newsService;

    public ExternalApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public ResponseEntity<List<Map<String, Object>>> getNews() {
        return ResponseEntity.ok(newsService.getTopHeadlines());
    }
}