package com.ildang100.backoffice.product.repository;

import com.ildang100.backoffice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 재고가 특정 수량 이하인 상품 개수를 조회합니다.
     *
     * <p>
     * 대시보드에서 재고 부족 상품 수를 계산하기 위해 사용됩니다.
     * </p>
     *
     * @param quantity 기준 재고 수량
     * @return 재고 부족 상품 수
     *
     * @author 이우람
     * @since 2026-04-28
     */
    long countByStockLessThanEqual(int quantity);
}