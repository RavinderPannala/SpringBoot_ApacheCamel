package com.pannala.ravinder.processor;

import java.math.BigDecimal;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.camel.jaxB.example.Order;
import com.camel.jaxB.example.Product;
import com.camel.jaxB.example.PurchaseOrderType;
import com.pannala.ravinder.config.Constant;

@Component
public class OrderDeliveredProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		PurchaseOrderType type = (PurchaseOrderType) exchange.getIn().getBody();
		Order orders = type.getOrder().get(0);
		if (orders.getOrderStatus().equals(Constant.ORDER_DELIVERED)) {
			String paymentMode = orders.getPayment().getPaymentMode();
			if (paymentMode != null) {
				if (paymentMode.equalsIgnoreCase(Constant.COD) || paymentMode.equalsIgnoreCase(Constant.CC)
						|| paymentMode.equalsIgnoreCase(Constant.DC)) {
					orders.getPayment().setPaymentMode(paymentMode);
					orders.getPayment().setPaymentStatus(Constant.DONE);
					List<Product> products = orders.getProduct();
					BigDecimal totalPayment = products.stream().map(Product::getProductPrice).reduce(BigDecimal.ZERO,
							(b1, b2) -> b1.add(b2));
					orders.getPayment().setPaymentAmount(totalPayment);
				}
			}
		}
	}

}
