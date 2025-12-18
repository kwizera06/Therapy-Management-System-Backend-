package com.tms.backend.repository;

import com.tms.backend.model.Resource;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByTherapistId(Long therapistId);
    List<Resource> findByClientIdOrClientIsNull(Long clientId);
    List<Resource> findByClient(User client);
    List<Resource> findByTherapist(User therapist);
}
