package com.pannala.ravinder.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class AreawiseDispachedProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String body = (String) exchange.getIn().getBody();
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<purchaseOrder xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"purchaseOrder.xsd\">");
		builder.append("\n").append(body).append("\n").append("</purchaseOrder>");
		exchange.getIn().setBody(builder.toString());
	}

}
