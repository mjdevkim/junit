package com.sprint.mission.Head04_MockitoBasic.service;

import org.springframework.stereotype.Service;

@Service
public interface AuditService {
    void logFailedRegistration(String email);
}
