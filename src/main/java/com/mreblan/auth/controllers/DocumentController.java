package com.mreblan.auth.controllers;

import com.mreblan.auth.dto.DocumentFilter;
import com.mreblan.auth.entities.Document;
import com.mreblan.auth.entities.User;
import com.mreblan.auth.services.DocumentService;
import com.mreblan.auth.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final S3Service s3Service;

    @GetMapping
    public ResponseEntity<Page<Document>> getAll(DocumentFilter filter, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.findAll(filter, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.findById(id, user));
    }

    @PostMapping
    public ResponseEntity<Document> create(@RequestBody Document document, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.create(document, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> update(@PathVariable Long id, @RequestBody Document document, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.update(id, document, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Document doc = documentService.findById(id, user);
        if (doc != null && doc.getFileKey() != null) s3Service.deleteFile(doc.getFileKey());
        documentService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> upload(@PathVariable Long id, @RequestParam MultipartFile file, @AuthenticationPrincipal User user) throws IOException {
        Document doc = documentService.findById(id, user);
        if (doc == null) return ResponseEntity.notFound().build();
        if (file.getSize() > 2_000_000) return ResponseEntity.badRequest().body("File too large");
        String key = s3Service.uploadFile(file, "documents/" + user.getId());
        if (doc.getFileKey() != null) s3Service.deleteFile(doc.getFileKey());
        documentService.attachFileKey(id, key, user);
        return ResponseEntity.ok(Map.of("message", "ok"));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Document doc = documentService.findById(id, user);
        if (doc == null || doc.getFileKey() == null) return ResponseEntity.notFound().build();
        String url = s3Service.generatePresignedUrl(doc.getFileKey(), 60);
        return ResponseEntity.ok(Map.of("url", url));
    }
}