package com.sprint.mission.Head05_SpringServiceLayerTest.service;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.sprint.mission.Head05_SpringServiceLayerTest.entity.Account;
import com.sprint.mission.Head05_SpringServiceLayerTest.entity.AccountStatus;
import com.sprint.mission.Head05_SpringServiceLayerTest.entity.TransactionHistory;
import com.sprint.mission.Head05_SpringServiceLayerTest.exception.InactiveAccountException;
import com.sprint.mission.Head06_ControllerTest.service.AdminService;
import com.sprint.mission.Head04_MockitoBasic.service.AuditService;
import com.sprint.mission.Head04_MockitoBasic.service.EmailService;
import com.sprint.mission.Head04_MockitoBasic.service.PasswordEncoder;
import com.sprint.mission.Head05_SpringServiceLayerTest.repository.AccountRepository;
import com.sprint.mission.Head05_SpringServiceLayerTest.repository.TransactionHistoryRepository;
import com.sprint.mission.Head06_ControllerTest.service.OrderService;
import com.sprint.mission.Head06_ControllerTest.service.ProductService;
import com.sprint.mission.Head06_ControllerTest.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TransactionBehaviorTest {
    // 검증 대상임 - 찐 객체 사용
    @Autowired private AccountService accountService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionHistoryRepository historyRepository;

    // 검증 대상 아님 - 짭 객체로 대체
    @MockBean private EmailService emailService;
    @MockBean private AuditService auditService;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private AdminService adminService;
    @MockBean private OrderService orderService;
    @MockBean private ProductService productService;
    @MockBean private UserService userService;

    @Test
    @DisplayName("계좌 이체 중 오류 발생시 모든 변경사항이 롤백되어야 함")
    void transferRollbackTest() {
        // given
        // 보내는 사람: 잔고 10만원
        Account sender = accountRepository.save(new Account("sender", 100000));
        // 받는 사람: 잔고 5만원
        Account receiver = accountRepository.save(new Account("receiver", 50000));

        receiver.setStatus(AccountStatus.INACTIVE); // 받는 사람: 비활성화 상태임
        accountRepository.save(receiver);

        // when, then
        assertThrows(InactiveAccountException.class, () -> {
            accountService.transfer(sender.getId(), receiver.getId(), 30000);
        }); // 비활성화인 사람에게 보낼 수 없음

        Account unchangedSender = accountRepository.findById(sender.getId()).orElseThrow();
        assertEquals(100000, unchangedSender.getBalance()); // 보낸사람의 잔고 변하지 않음
        List<TransactionHistory> histories =
                historyRepository.findBySenderAccountId(sender.getId());    // 보낸사람의 히스토리에도 송금 이력 없음
        assertTrue(histories.isEmpty());
    }

    @Test
    @DisplayName("중첩 트랜잭션 실패시 전체 롤백")
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // 이 테스트 메서드는 transction없이 실행
    // "트랜잭션을 시작한 쪽이 테스트이므로, 테스트가 끝날 때 롤백하는 것입니다."
    void nestedTransactionTest() {
        // given
        Account account = accountRepository.save(new Account("test", 100000));

        // when, then
        assertThrows(DataIntegrityViolationException.class, () -> {
            accountService.performComplexOperation(account.getId());
        }); // 1000원 출금하는 로직 있고 - 직후에 throw DataIntegrityViolationException 함.
        Account unchanged = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(100000, unchanged.getBalance());
        assertEquals(AccountStatus.ACTIVE, unchanged.getStatus());
    }
}
