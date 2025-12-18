package com.tms.backend.controller;

import com.tms.backend.dto.MessageResponse;
import com.tms.backend.model.Resource;
import com.tms.backend.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private com.tms.backend.service.FileStorageService fileStorageService;
    
    @PostMapping
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<Resource> createResource(
            @RequestParam Long therapistId,
            @RequestParam(required = false) Long clientId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam String fileType) {
        Resource resource = resourceService.createResource(therapistId, clientId, title, description, file, fileType);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> getFile(@PathVariable String fileName) {
        org.springframework.core.io.Resource file = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
    
    @GetMapping("/therapist/{therapistId}")
    @PreAuthorize("hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<Resource>> getTherapistResources(@PathVariable Long therapistId) {
        return ResponseEntity.ok(resourceService.getTherapistResources(therapistId));
    }
    
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<Resource>> getClientResources(@PathVariable Long clientId) {
        return ResponseEntity.ok(resourceService.getClientResources(clientId));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(new MessageResponse("Resource deleted successfully"));
    }
}
