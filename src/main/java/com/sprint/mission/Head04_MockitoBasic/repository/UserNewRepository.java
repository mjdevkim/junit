package com.sprint.mission.Head04_MockitoBasic.repository;

import com.sprint.mission.Head04_MockitoBasic.entity.UserNew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNewRepository extends JpaRepository<UserNew, Long> {
    boolean existsByEmail(String email);
}
