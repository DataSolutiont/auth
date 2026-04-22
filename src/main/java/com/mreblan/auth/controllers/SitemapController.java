package com.mreblan.auth.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class SitemapController {

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
               "  <url><loc>http://localhost:8080/</loc></url>\n" +
               "  <url><loc>http://localhost:8080/auth</loc></url>\n" +
               "  <url><loc>http://localhost:8080/signup</loc></url>\n" +
               "</urlset>";
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return "User-agent: *\n" +
               "Disallow: /api/\n" +
               "Disallow: /dashboard\n" +
               "Disallow: /documents\n" +
               "Disallow: /profile\n" +
               "Sitemap: http://localhost:8080/sitemap.xml";
    }

    @GetMapping("/api/external/hello")
    public Map<String, String> externalHello() {
        return Map.of("message", "Привет из внешнего API (заглушка)");
    }
}