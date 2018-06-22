package com.pannala.ravinder.filter;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

import com.camel.jaxB.example.Address;
import com.camel.jaxB.example.Order;
import com.camel.jaxB.example.PurchaseOrderType;
import com.pannala.ravinder.config.BeanUtil;
import com.pannala.ravinder.config.Constant;
import com.pannala.ravinder.config.Customer;
import com.pannala.ravinder.repository.CustomerRepository;

public class DeliveryAddressPredicate implements Predicate {

	private boolean addressFlag = false;

	@Override
	public boolean matches(Exchange exchange) {

		PurchaseOrderType purchaseOrderType = (PurchaseOrderType) exchange.getIn().getBody();

		List<Order> order = purchaseOrderType.getOrder();
		Address deliveryAddress = order.get(0).getDeliveryAddress();
		if (deliveryAddress != null) {
			Customer customer = getCustomer(deliveryAddress.getCustomerNumber());
			if (customer != null) {
				addressFlag = true;
				order.get(0).setOrderStatus(Constant.ORDER_DELIVERED);
			} else {
				addressFlag = false;
				order.get(0).setOrderStatus(Constant.ADDRESS_MISMATCHED);
			}
		}
		return addressFlag;
	}

	private Customer getCustomer(String customerNumber) {
		CustomerRepository customerRepository = BeanUtil.getBean(CustomerRepository.class);
		Customer findByCustomerNumber = customerRepository.findByCustomerNumber(customerNumber);
		return findByCustomerNumber;

	}

}