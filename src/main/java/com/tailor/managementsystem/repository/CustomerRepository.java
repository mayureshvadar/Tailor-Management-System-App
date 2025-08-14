package com.tailor.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tailor.managementsystem.model.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // You can add custom queries here later (e.g. findByMobile)
}