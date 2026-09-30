package com.sprint.mission.Head06_ControllerTest.dto.admin;

import com.sprint.mission.Head06_ControllerTest.dto.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private boolean active;
}
