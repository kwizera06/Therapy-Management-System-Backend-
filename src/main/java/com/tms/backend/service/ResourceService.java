package com.tms.backend.service;

import com.tms.backend.model.Resource;
import com.tms.backend.model.User;
import com.tms.backend.repository.ResourceRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ResourceService {
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;
    
    public Resource createResource(Long therapistId, Long clientId, String title, String description, MultipartFile file, String fileType) {
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        String fileName = fileStorageService.saveFile(file);
        String fileUrl = "/api/resources/files/" + fileName;

        Resource resource = new Resource();
        resource.setTherapist(therapist);
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setFileUrl(fileUrl);
        resource.setFileType(fileType);
        
        // If clientId is provided, make it client-specific
        if (clientId != null) {
            User client = userRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            resource.setClient(client);
        }
        
        return resourceRepository.save(resource);
    }
    
    public List<Resource> getTherapistResources(Long therapistId) {
        return resourceRepository.findByTherapistId(therapistId);
    }
    
    public List<Resource> getClientResources(Long clientId) {
        // Get resources that are either general (client is null) or specific to this client
        return resourceRepository.findByClientIdOrClientIsNull(clientId);
    }
    
    public void deleteResource(Long resourceId) {
        resourceRepository.deleteById(resourceId);
    }
}
