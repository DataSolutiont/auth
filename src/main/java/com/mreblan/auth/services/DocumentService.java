package com.mreblan.auth.services;

import com.mreblan.auth.dto.DocumentFilter;
import com.mreblan.auth.entities.Document;
import com.mreblan.auth.entities.User;
import com.mreblan.auth.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public Page<Document> findAll(DocumentFilter filter, User currentUser) {
        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!"ADMIN".equals(currentUser.getRole().name())) {
                predicates.add(cb.equal(root.get("owner"), currentUser));
            }
            if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getDateFrom().atStartOfDay()));
            }
            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getDateTo().atTime(23, 59, 59)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort.Direction direction = Sort.Direction.fromString(filter.getSortDir());
        Sort sort = Sort.by(direction, filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        return documentRepository.findAll(spec, pageable);
    }

    public Document findById(Long id, User currentUser) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return null;
        if (!"ADMIN".equals(currentUser.getRole().name()) && !doc.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Access denied");
        }
        return doc;
    }

    @Transactional
    public Document create(Document document, User currentUser) {
        document.setOwner(currentUser);
        document.setId(null);
        document.setCreatedAt(LocalDateTime.now());
        return documentRepository.save(document);
    }

    @Transactional
    public Document update(Long id, Document updated, User currentUser) {
        Document existing = findById(id, currentUser);
        if (existing == null) return null;
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        return documentRepository.save(existing);
    }

    @Transactional
    public boolean delete(Long id, User currentUser) {
        Document doc = findById(id, currentUser);
        if (doc == null) return false;
        documentRepository.delete(doc);
        return true;
    }

    @Transactional
    public void attachFileKey(Long id, String fileKey, User currentUser) {
        Document doc = findById(id, currentUser);
        if (doc != null) {
            doc.setFileKey(fileKey);
            documentRepository.save(doc);
        }
    }
}