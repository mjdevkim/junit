package com.sprint.mission.Head05_SpringServiceLayerTest.repository;

import com.sprint.mission.Head05_SpringServiceLayerTest.entity.Member;

public interface DiscountPolicy {
    int calculateDiscount(Member member, int price);
}
