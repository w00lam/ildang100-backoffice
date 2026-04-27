package com.ildang100.backoffice.product.repository;

import com.ildang100.backoffice.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}