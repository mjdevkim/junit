package com.sprint.mission.repository;

import com.sprint.mission.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/menudb",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.datasource.username=menu_user",
        "spring.datasource.password=menu_pass",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.sql.init.mode=never"
})
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
                .status("ACTIVE")
                .build();

        // when
        userRepository.save(user);

        // then
        Optional<User> result = userRepository.findById(user.getId());
        assertTrue(result.isPresent()); // <- userRepository.save 통해서 저장 됐는지 확인
    }

    @Test
    @DisplayName("이메일이 없으면 저장 실패")
    // 테스트 대상 엔티티의 필드 제약 (@NotNull, @Column(nullable = false))을 데이터베이스와 동시에 검증
    void givenMissingEmail_whenSaveUser_thenThrowsException() {
        // given
        User user = User.builder()
                .email(null)
                .name("tester")
                .status("ACTIVE")
                .build();

        // when & then
        DataIntegrityViolationException exception =
                assertThrows(DataIntegrityViolationException.class, () -> {
                    userRepository.saveAndFlush(user);  // saveAndFlush를 써야 DB 까지 실제 반영이 이뤄지고 제약조건 위반이 감지됨
                });
        System.out.println("=== NOT NULL 예외 메세지 ===");
        System.out.println(exception.getMessage());
    }

    @Test
    @DisplayName("중복 이메일 저장 실패")
    void givenDuplicateEmail_whenSaveUser_thenThrowsException() {
        // given
        userRepository.saveAndFlush(
                User.builder()
                        .email("test@example.com")
                        .name("user1")
                        .status("ACTIVE")
                        .build()
        );

        User duplicate = User.builder()
                .email("test@example.com")
                .name("user2")
                .status("ACTIVE")
                .build();

        // when & then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicate);
        });
    }

}