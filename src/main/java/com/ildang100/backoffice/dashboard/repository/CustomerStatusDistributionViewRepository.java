package com.ildang100.backoffice.dashboard.repository;

import com.ildang100.backoffice.dashboard.entity.CustomerStatusDistributionView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerStatusDistributionViewRepository
        extends JpaRepository<CustomerStatusDistributionView, Integer> {
}
