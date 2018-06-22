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
public class SendEmailProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		PurchaseOrderType puOrderType = (PurchaseOrderType) exchange.getIn().getBody();
		Order order = puOrderType.getOrder().get(0);
		if (order.getOrderStatus().equals(Constant.ORDER_DELIVERED)) {
			List<Product> products = order.getProduct();
			StringBuilder builder = new StringBuilder();
			builder.append("----------------------------- Ordered Items ------------------------- ").append("\n")
					.append("\n");
			builder.append("OrderId : " + order.getOrderNumber());
			int counter = 1;
			for (Product prod : products) {
				builder.append("\n").append("\n").append("Product " + counter).append("\n").append("\n");
				builder.append("\n").append("Product Name : " + prod.getProductName()).append("\n")
						.append("Product Brand : " + prod.getProductBrand()).append("\n")
						.append("Product Type : " + prod.getProductType()).append("\n")
						.append("Product Price : " + prod.getProductPrice()).append("\n");
				counter++;
			}
			BigDecimal totalPayment = products.stream().map(Product::getProductPrice).reduce(BigDecimal.ZERO,
					(b1, b2) -> b1.add(b2));
			builder.append("\n").append("\n").append("TotalAmount:" + totalPayment);
			exchange.getIn().setBody(builder.toString());
		}
	}

}
