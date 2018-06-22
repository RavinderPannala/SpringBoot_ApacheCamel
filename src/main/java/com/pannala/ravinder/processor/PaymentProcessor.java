package com.pannala.ravinder.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.camel.jaxB.example.Order;
import com.camel.jaxB.example.Payment;
import com.camel.jaxB.example.PurchaseOrderType;
import com.pannala.ravinder.config.Constant;

@Component
public class PaymentProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		PurchaseOrderType purchaseOrderType = (PurchaseOrderType) exchange.getIn().getBody();

		Order order = purchaseOrderType.getOrder().get(0);
		if (order.getOrderStatus().equals(Constant.ORDER_DELIVERED)) {
			Payment payment = order.getPayment();
			if (payment != null) {
				order.getPayment().setPaymentMode(Constant.COD);
			} else {
				Payment payment2 = new Payment();
				payment2.setPaymentMode(Constant.COD);
				order.setPayment(payment2);
			}
		}
	}

}
