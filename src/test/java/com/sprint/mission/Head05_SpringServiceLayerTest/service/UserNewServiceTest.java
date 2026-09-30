package com.sprint.mission.Head05_SpringServiceLayerTest.service;

import com.sprint.mission.Head04_MockitoBasic.entity.UserNew;
import com.sprint.mission.Head04_MockitoBasic.repository.UserNewRepository;
import com.sprint.mission.Head04_MockitoBasic.service.AuditService;
import com.sprint.mission.Head04_MockitoBasic.service.EmailService;
import com.sprint.mission.Head04_MockitoBasic.service.PasswordEncoder;
import com.sprint.mission.Head04_MockitoBasic.service.UserNewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserNewServiceTest {
    @Mock private UserNewRepository userNewRepository;
    @Mock private EmailService emailService;
    @Mock private AuditService auditService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserNewService userNewService;

    @Test
    @DisplayName("회원가입시 비밀번호 암호화와 이메일 전송이 수행되어야 한다")
    void userRegistrationProcess() {
        // given
        UserNew user = UserNew.builder()
                .email("test@test.com")
                .name("홍길동")
                .password("rawPassword")
                .status("ACTIVE")
                .build();
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode("rawPassword")).thenReturn(encodedPassword);
        when(userNewRepository.save(any(UserNew.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailService.send("test@test.com", "가입을 환영합니다!")).thenReturn(true);

        // when
        boolean result = userNewService.register(user);

        // then
        assertTrue(result);

        // 비밀번호 암호화 검증
        verify(passwordEncoder).encode("rawPassword");

        // save시 password가 암호화 되었는지 검증 (중요!)
            // 실수로 원문 비밀번호인 객체를 저장했으면 fail한다
        verify(userNewRepository).save(argThat(savedUser ->
                savedUser.getPassword().equals(encodedPassword)
        ));
        verify(emailService).send("test@test.com", "가입을 환영합니다!");
        verify(auditService, never()).logFailedRegistration(anyString());
    }
}
