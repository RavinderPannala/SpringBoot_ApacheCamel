package com.pannala.ravinder.processor;

import java.math.BigDecimal;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.camel.jaxB.example.Invoice;
import com.camel.jaxB.example.Order;
import com.camel.jaxB.example.Product;
import com.camel.jaxB.example.PurchaseOrderType;
import com.pannala.ravinder.config.Constant;

@Component
public class InvoiceProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		PurchaseOrderType ordertype = (PurchaseOrderType) exchange.getIn().getBody();
		Order order = ordertype.getOrder().get(0);
		List<Product> products = order.getProduct();
		BigDecimal totalInvoiceAmount = products.stream().map(Product::getProductPrice).reduce(BigDecimal.ZERO,
				(b1, b2) -> b1.add(b2));
		if (order.getPayment().getPaymentStatus().equals(Constant.DONE)) {
			Invoice invoice = order.getInvoice();
			if (invoice != null) {
				order.getInvoice().setInvoiceId("INV_" + order.getOrderNumber());
				order.getInvoice().setInvoiceAmount(totalInvoiceAmount);
				order.getInvoice().setInvoiceStatus(Constant.INV_GENERATED);
			} else {
				Invoice invoice2 = new Invoice();
				invoice2.setInvoiceId("INV_" + order.getOrderNumber());
				invoice2.setInvoiceAmount(totalInvoiceAmount);
				invoice2.setInvoiceStatus(Constant.INV_GENERATED);
				order.setInvoice(invoice2);
			}
		} else {
			order.getInvoice().setInvoiceStatus(Constant.INV_PENDING);
			order.getPayment().setPaymentStatus(Constant.PENDING);
		}

	}

}
