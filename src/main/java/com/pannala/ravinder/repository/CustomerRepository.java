package com.pannala.ravinder.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pannala.ravinder.config.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String> {

	Customer findByCustomerNumber(String customerNumber);

}
