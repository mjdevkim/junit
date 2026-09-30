package com.sprint.mission.Head05_SpringServiceLayerTest.repository;

import com.sprint.mission.Head05_SpringServiceLayerTest.entity.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
}
