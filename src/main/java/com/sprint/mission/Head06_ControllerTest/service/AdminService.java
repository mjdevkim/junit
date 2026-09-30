package com.sprint.mission.Head06_ControllerTest.service;

import com.sprint.mission.Head06_ControllerTest.dto.admin.AdminUserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {
    List<AdminUserResponse> getAllUsers();
}