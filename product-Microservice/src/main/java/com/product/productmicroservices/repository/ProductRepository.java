package com.product.productmicroservices.repository;

import com.product.productmicroservices.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
