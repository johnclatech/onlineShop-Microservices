package com.order.ordermicroservices.repository;

import com.order.ordermicroservices.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
