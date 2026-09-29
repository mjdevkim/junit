package com.sprint.mission.repository;

import com.sprint.mission.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        // 1. 접속 url
        "spring.datasource.url=jdbc:postgresql://localhost:5432/menudb",
        // 2. JDBC 드라이버
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        // 3. Username
        "spring.datasource.username=menu_user",
        // 4. Password
        "spring.datasource.password=menu_pass",
        // etc. DB 관련
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.sql.init.mode=never"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // TestFixture - 기본 사용자 생성
    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .status("ACTIVE")
                .build();
    }

    // TestFixture - 상태를 포함한 사용자 생성
    private User createUserWithStatus(String email, String name, String status) {
        return User.builder()
                .email(email)
                .name(name)
                .status(status)
                .build();
    }

    // TestFixture - 비활성 사용자
    private User createInactiveUser() {
        return User.builder()
                .email("inactive@test.com")
                .name("비활성유저")
                .status("INACTIVE")
                .build();
    }

    @BeforeEach // 각 테스트 메서드 실행 전에 공통으로 필요한 데이터를 사전에 세팅할 수 있도록 도와준 - 반복되는 준비 작업 캡슐화
    void setUp() {
        userRepository.save(new User(3L, "a@example.com", "Alice", "ONLINE"));
        userRepository.save(new User(4L, "b@example.com", "Bob", "ONLINE"));
    }

    @Test
    @DisplayName("기본 유저가 4명 존재해야 한다")
    void givenSetup_whenCountUsers_thenReturns2() {
        // when
        long count = userRepository.count();
        // then
        assertEquals(4, count);
    }

    @Test
    @DisplayName("사용자 저장 후 정상적으로 조회되어야 한다")
    void whenSaveUser_thenItShouldBePersisted() {
        // given
        User user = createUser("test@example.com", "tester");

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
        User user = createUser(null, "tester");

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

    @Test
    @DisplayName("삭제 이후 DB에 해당 사용자가 존재하지 않아야 한다")
    void givenDeletedUser_whenCheckDb_thenUserShouldNotExist() {
        // given
        User user = userRepository.save(
                createUserWithStatus(
                        "delete@example.com",
                        "Delete",
                        "ACTIVE"
                )
        );
        Long id = user.getId();

        // when
        userRepository.delete(user);

        // flush를 통해 db까지 반영
        userRepository.flush();

        boolean exists = userRepository.existsById(id);

        // then
        assertFalse(exists);
    }

}