package com.sprint.mission.Head05_SpringServiceLayerTest.repository;

import com.sprint.mission.Head05_SpringServiceLayerTest.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    List<TransactionHistory> findBySenderAccountId(Long senderId);
}
