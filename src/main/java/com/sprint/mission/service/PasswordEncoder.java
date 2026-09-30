package com.sprint.mission.service;

import org.springframework.stereotype.Component;

// 단위 테스트에서 Mock으로 대체할 비밀번호 인코더입니다.
@Component
public interface PasswordEncoder {
    String encode(String rawPassword);
}
