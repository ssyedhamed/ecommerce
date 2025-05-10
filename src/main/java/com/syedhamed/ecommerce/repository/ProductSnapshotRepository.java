package com.syedhamed.ecommerce.repository;

import com.syedhamed.ecommerce.payload.ProductSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSnapshotRepository extends JpaRepository<ProductSnapshot, Long> {
}
