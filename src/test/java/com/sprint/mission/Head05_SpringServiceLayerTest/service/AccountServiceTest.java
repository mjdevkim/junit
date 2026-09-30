package com.sprint.mission.Head05_SpringServiceLayerTest.service;

import com.sprint.mission.Head05_SpringServiceLayerTest.entity.Account;
import com.sprint.mission.Head05_SpringServiceLayerTest.entity.AccountStatus;
import com.sprint.mission.Head05_SpringServiceLayerTest.exception.InactiveAccountException;
import com.sprint.mission.Head05_SpringServiceLayerTest.repository.AccountRepository;
import com.sprint.mission.Head05_SpringServiceLayerTest.repository.TransactionHistoryRepository;
import com.sprint.mission.Head04_MockitoBasic.repository.UserNewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(AccountService.class)
// 서비스 자체의 트랜잭션이 종료된 뒤 DB를 다시 조회하여 커밋/롤백을 검증합니다.
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AccountServiceTest {
    @Autowired AccountService service;
    @Autowired AccountRepository accounts;
    @Autowired TransactionHistoryRepository histories;
    @Autowired UserNewRepository users;

    @BeforeEach
    void cleanAccounts() {
        histories.deleteAll();
        accounts.deleteAll();
    }

    @Test
    void transferCommitsBalancesAndHistory() {
        Account sender = accounts.save(new Account("sender", 10000));
        Account receiver = accounts.save(new Account("receiver", 2000));

        service.transfer(sender.getId(), receiver.getId(), 3000);

        assertEquals(7000, accounts.findById(sender.getId()).orElseThrow().getBalance());
        assertEquals(5000, accounts.findById(receiver.getId()).orElseThrow().getBalance());
        var history = histories.findBySenderAccountId(sender.getId());
        assertEquals(1, history.size());
        assertEquals(receiver.getId(), history.get(0).getReceiverAccountId());
        assertEquals(3000, history.get(0).getAmount());
    }

    @Test
    void inactiveReceiverLeavesBalancesAndHistoryUnchanged() {
        Account sender = accounts.save(new Account("sender", 10000));
        Account receiver = new Account("receiver", 2000);
        receiver.setStatus(AccountStatus.INACTIVE);
        accounts.save(receiver);

        assertThrows(InactiveAccountException.class,
                () -> service.transfer(sender.getId(), receiver.getId(), 3000));

        assertEquals(10000, accounts.findById(sender.getId()).orElseThrow().getBalance());
        assertEquals(2000, accounts.findById(receiver.getId()).orElseThrow().getBalance());
        assertEquals(0, histories.count());
    }

    @Test
    void complexOperationRollsBackWithdrawal() {
        Account account = accounts.save(new Account("sender", 10000));

        assertThrows(DataIntegrityViolationException.class,
                () -> service.performComplexOperation(account.getId()));

        assertEquals(10000, accounts.findById(account.getId()).orElseThrow().getBalance());
    }

    @Test
    void initialDataAndExistsByEmailWork() {
        assertTrue(users.existsByEmail("a@test.com"));
        assertTrue(users.existsByEmail("b@test.com"));
        assertFalse(users.existsByEmail("missing@test.com"));
    }
}
