package com.sprint.mission.repository;

import com.sprint.mission.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 후 정상적으로 조회되어야 한다")
    void whenSaveUser_thenItShouldBePersisted() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .name("tester")
                .status("ACTIVE")인
                .build();

        // when
        userRepository.save(user);

        // then
        Optional<User> result = userRepository.findById(user.getId());
        assertTrue(result.isPresent()); // <- userRepository.save 통해서 저장 됐는지 확인
    }
}