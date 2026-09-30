package com.sprint.mission.service;

import org.springframework.stereotype.Service;

@Service
public interface AuditService {
    void logFailedRegistration(String email);
}
